package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.Connection;
import cs455.overlay.util.ConnectionManager;
import cs455.overlay.util.EventHandlerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.EventSocket;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

/*
 * Registry is a singleton class
 */

/**
 * @author Michael Miller
 */
public class Registry implements Node {

	// Constants
	public static final int MAX_NUM_NODES = 127;

	private static TCPServerThread tcpServerThread; // listens for incoming connections
	
	private static InteractiveCommandParser interactiveCommandParser; // parses commands from System.in
	private volatile static LinkedBlockingQueue<String> commandQueue; // main blocks on this queue, waiting for commands
	
	private volatile static TCPConnectionsCache tcpConnectionsCache; // object to store TCPConnections
	
	private static EventHandlerThread eventHandlerThread;
		
	private boolean acceptingCommands;
	
	private byte[] localHost;
	private int portNum;
	
	private static Random randomIDGenerator;

	private volatile ConnectionManager connectionManager;
	
	private int numReadyNodes;
	private int numFinishedNodes;
	
	StatisticsCollectorAndDisplay stats;
		
	public Registry(int portNum) throws IOException {
		commandQueue = new LinkedBlockingQueue<String>();
		interactiveCommandParser = new InteractiveCommandParser(this, commandQueue);
		(new Thread(interactiveCommandParser)).start();
		
		eventHandlerThread = new EventHandlerThread(this);
		eventHandlerThread.start();
		
		tcpConnectionsCache = new TCPConnectionsCache();
	
		tcpServerThread = new TCPServerThread(tcpConnectionsCache, portNum);
		(new Thread(tcpServerThread)).start();
				
		randomIDGenerator = new Random();

		acceptingCommands = true;
		
		connectionManager = new ConnectionManager();
		
		numReadyNodes = 0;
		numFinishedNodes = 0;
		
		stats = null; // to be initialized later
	}


	public void stopListening() {
		tcpServerThread.stopListening();
	}

	public String listMessagingNodes() {
		return tcpConnectionsCache.printConnections();
	}

	@Override
	public void onEvent(EventSocket eventSocket) {
		// TODO take action based on event type
		
		if(eventSocket.getEvent() instanceof OverlayNodeSendsRegistration) {
			try {
				handleOverlayNodeSendsRegistration(eventSocket);
			} catch (Exception exception) {
				System.out.println("Trouble receiving/replying to OverlayNodeSendsRegistration!\n" + exception.getMessage());
			}
		} else if(eventSocket.getEvent() instanceof OverlayNodeSendsDeregistration) {
			try {
				handleOverlayNodeSendsDeregistration(eventSocket);
			} catch (IOException exception) {
				System.out.println("Trouble receiving/replying to OverlayNodeSendsDeregistration!\n" + exception.getMessage());
			}
		} else if(eventSocket.getEvent() instanceof NodeReportsOverlaySetupStatus) {
			handleNodeReportsOverlaySetupStatus(eventSocket);
		} else if(eventSocket.getEvent() instanceof OverlayNodeReportsTaskFinished) {
			try {
				handleOverlayNodeReportsTaskFinished(eventSocket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(eventSocket.getEvent() instanceof OverlayNodeReportsTrafficSummary){
			handleOverlayNodeReportsTrafficSummary(eventSocket);
		}
		else {
			System.out.println("Received message of unknown type!");
		}
	}

	private void handleOverlayNodeSendsRegistration(EventSocket eventSocket) throws Exception {
		OverlayNodeSendsRegistration event = (OverlayNodeSendsRegistration) eventSocket.getEvent();
		
		RegistryReportsRegistrationStatus replyMessage = new RegistryReportsRegistrationStatus();
		String infoString;
		
		if(!Arrays.equals(event.getMessageIPAddress(),eventSocket.getSocket().getInetAddress().getAddress())){
			System.out.println("eventSocket.getAddress is: " + eventSocket.getSocket().getInetAddress().getAddress());
			System.out.println("e.getmessageIP is: " + event.getMessageIPAddress());
			infoString = "IP address & port within message did not match sender's IP Address & port";
			replyMessage.setSuccessStatus(-1);
			replyMessage.setInfoString(infoString.getBytes());
			replyMessage.setInfoStringLength((byte)infoString.length());
		}
		
		if(tcpConnectionsCache.containsConnection(eventSocket.getSocket()) && connectionManager.containsConnection(eventSocket.getSocket()) && connectionManager.getConnection(eventSocket.getSocket()).isRegistered() ) {
			// if connected & already assigned ID, reply with failed registration
		
			replyMessage.setSuccessStatus(-1);
			infoString = "This node has already been registered";
			replyMessage.setInfoString(infoString.getBytes());
			replyMessage.setInfoStringLength( (byte)infoString.length());
		} else if(tcpConnectionsCache.containsConnection(eventSocket.getSocket()) && !connectionManager.containsConnection(eventSocket.getSocket())) {
			// else if connected & not assigned ID
			int assignedID;
			
			do {
				assignedID = randomIDGenerator.nextInt(MAX_NUM_NODES + 1);
			} while(connectionManager.containsID(assignedID));
			
			connectionManager.addConnection(eventSocket.getSocket().getInetAddress().getAddress(), event.getMessagePort(), assignedID, tcpConnectionsCache.getConnection(eventSocket.getSocket()));
			infoString = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + connectionManager.size() + ")";
			replyMessage.setSuccessStatus(assignedID);
			replyMessage.setInfoString(infoString.getBytes());
			replyMessage.setInfoStringLength((byte)infoString.length());
		}
		else { 
			System.out.println("Received a registration message from " + tcpConnectionsCache.getConnection(eventSocket.getSocket()).getDestinationAddress() + ":" + tcpConnectionsCache.getConnection(eventSocket.getSocket()).getDestinationPort() + " that is not in the TCPConnectionsCache!!");
		}
		
		try{
			tcpConnectionsCache.getConnection(eventSocket.getSocket()).send(replyMessage.getBytes());
		} catch (IOException ioe) {
			System.out.println("A messaging node has failed before a reply to a registration request could be sent. Removing the node from the registry.");
			if(connectionManager.containsConnection(eventSocket.getSocket())) {
				connectionManager.remove(eventSocket.getSocket());
			}
			if(tcpConnectionsCache.containsConnection(eventSocket.getSocket())) {
				tcpConnectionsCache.remove(eventSocket.getSocket());
			}
		}
		
	}
	
	private void handleOverlayNodeSendsDeregistration(EventSocket eventSocket) throws IOException {
		OverlayNodeSendsDeregistration event = (OverlayNodeSendsDeregistration) eventSocket.getEvent();
		RegistryReportsDeregistrationStatus replyMessage = new RegistryReportsDeregistrationStatus();
		String infoString;
				
		if(Arrays.equals(event.getMessageIPAddress(),eventSocket.getSocket().getInetAddress().getAddress())){
			infoString = "IP address & port within message did not match sender's IP Address & port";
			replyMessage.setSuccessStatus(-1);
			replyMessage.setInfoString(infoString.getBytes());
			replyMessage.setInfoStringLength((byte)infoString.length());
			tcpConnectionsCache.getConnection(eventSocket.getSocket()).send(replyMessage.getBytes());
			return;
		}
		
		if(tcpConnectionsCache.containsConnection(eventSocket.getSocket())
		&& connectionManager.containsConnection(eventSocket.getSocket())
		&& event.getAssignedNodeID() == connectionManager.getConnection(eventSocket.getSocket()).getId()) {
		// if connected & registered, and sends correct node ID, successful deregistration
			
			replyMessage.setSuccessStatus(connectionManager.remove(eventSocket.getSocket()).getId());
			infoString = "Deregistration request successful. The number of messaging nodes currently constituting the overlay is (" + connectionManager.size() + ")";
			replyMessage.setInfoString(infoString.getBytes());
			replyMessage.setInfoStringLength((byte)infoString.length());
			tcpConnectionsCache.getConnection(eventSocket.getSocket()).send(replyMessage.getBytes());
		}
		else if(tcpConnectionsCache.containsConnection(eventSocket.getSocket())
		&& ! connectionManager.containsConnection(eventSocket.getSocket()))  {
		// if connected but not registered, unsuccessful deregistration 
			replyMessage.setSuccessStatus(-1);
			infoString = "Deregistration request unsuccessful. You are connected but not registered.";
			replyMessage.setInfoString(infoString.getBytes());
			replyMessage.setInfoStringLength((byte)infoString.length());
			connectionManager.getConnection(eventSocket.getSocket()).getTCPConnection().send(replyMessage.getBytes());
		}
		else {
			System.out.println("Received a deregistration message from " + tcpConnectionsCache.getConnection(eventSocket.getSocket()).getDestinationAddress() + ":" + tcpConnectionsCache.getConnection(eventSocket.getSocket()).getDestinationPort() + " that is not in the TCPConnectionsCache!!");
		}
	}

	public static void main(String args[]) {
		int portNum;

		if(args.length == 0) {
			System.out.println("Registry: too few args"); // TODO change this to a method
		}

		portNum = Integer.parseInt(args[0]);

		Registry registry;

		try {
			registry = new Registry(portNum);
			registry.waitForCommands();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void waitForCommands() {
		String commandToExecute;
		while(acceptingCommands) {
			try {
				commandToExecute = commandQueue.take();
				if(commandToExecute.equals("list-messaging-nodes")) {
					connectionManager.printConnections();
				}
				else if(commandToExecute.equals("list-routing-tables")) {
					if(! connectionManager.isEmpty() && ! connectionManager.get(0).isRoutingTableInstalled()) {
						System.out.println("The overlay must be set up before executing this command.");
					}
					else {
						for(Connection c : connectionManager.getConnections()) {
							System.out.println("Node ID: " + c.getId() + " has the following messaging nodes in its routing table");
							System.out.println(c.getRoutingTable().toString());
						}
					}
				}
				else if(commandToExecute.equals("setup-overlay")) {
					int numRoutingTableEntries = Integer.parseInt(commandQueue.take());
					if(numRoutingTableEntries < 1 || Math.pow(2, numRoutingTableEntries) > connectionManager.size()) {
						System.out.println("A messaging node must have a number of routing table entries, e, such that 0 < e <= log_2(number of messaging nodes). Please enter a valid argument for the setup-overlay command.");
					}
					else {
						try {
							setupOverlay(numRoutingTableEntries);
						} catch (IOException ioe) {
							System.out.println("Trouble setting up overlay!");
						}
					}
				} else if(commandToExecute.equals("start")) {
					int numPacketsToSend = Integer.parseInt(commandQueue.take());
					try {
						start(numPacketsToSend);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

					
			} catch (InterruptedException ie) {}
		}

		
	}


/**
	 * @param numPacketsToSend
 * @throws IOException 
	 */
	private void start(int numPacketsToSend) throws IOException {
		RegistryRequestsTaskInitiate message = new RegistryRequestsTaskInitiate();
		if(stats == null) {
			stats = new StatisticsCollectorAndDisplay(connectionManager.size());
		}
		stats.clear();		
		message.setNumPacketsToSend(numPacketsToSend);
		for(Connection c : connectionManager.getConnections()) {
			c.getTCPConnection().send(message.getBytes());
		}
		
	}


	/**
	 * @param numRegistryEntries
	 * @throws IOException 
	 */
	private void setupOverlay(int numRoutingTableEntries) throws IOException {
		int nodeIDIndex;
		
		connectionManager.sort();
		
		for(int i = 0; i < connectionManager.size(); i++) {
			RoutingTable tempRoutingTable = new RoutingTable(numRoutingTableEntries); // for each connection, a new routing table
			
			for(int j = 0; j < numRoutingTableEntries; j++) { // insert each RoutingEntry into the table
				
				// Routing entries are defined to be 2^0, 2^1, 2^2 ... 2^(log(n)) hops away from the origin, where n is the number of routing entries
				nodeIDIndex = (i + (int)Math.pow((double)2,(double)j)) % connectionManager.size();
				
				tempRoutingTable.add(new RoutingEntry(
										connectionManager.get(nodeIDIndex).getId(),
										connectionManager.get(nodeIDIndex).getDestinationIP(),
										connectionManager.get(nodeIDIndex).getListeningPort(),
										(int)Math.pow((double)2, (double)j)));
			}
			
			connectionManager.get(i).addRoutingTable(tempRoutingTable);
		}
		
		for(Connection c : connectionManager.getConnections()) {
			// send each connection its routing table and a list of all IDs
			c.getTCPConnection().send( (new RegistrySendsNodeManifest(c.getRoutingTable() , connectionManager.getIDs() )).getBytes() );
		}		
	}

	/** 
	 * 
	 * @param eventSocket
	 */
	private void handleNodeReportsOverlaySetupStatus(EventSocket eventSocket) {
		NodeReportsOverlaySetupStatus event = (NodeReportsOverlaySetupStatus) eventSocket.getEvent();
		
		if(event.getSuccessStatus() == -1) {
			System.out.println("A node was unsuccessful in setting up connections!");
			System.out.println(event.getInfoString());
		}
		else if(event.getSuccessStatus() != connectionManager.getConnection(eventSocket.getSocket()).getId()) {
			System.out.println("Received a message containing ID that does not match sender's registered ID!");
			return;
		}
		else {
			numReadyNodes += 1;
			
			if(numReadyNodes == connectionManager.size() && connectionManager.isReadyToInitTask()) {
				System.out.println("All messaging nodes have successfully connected. You may now initiate the task.");
			}
		}
	}
	
	/**
	 * @param eventSocket
	 * @throws IOException 
	 */
	private void handleOverlayNodeReportsTaskFinished(EventSocket eventSocket) throws IOException {
		numFinishedNodes += 1;
		if(numFinishedNodes == connectionManager.size()) {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(Connection c : connectionManager.getConnections()) {
				c.getTCPConnection().send((new RegistryRequestsTrafficSummary()).getBytes());
			}
			numFinishedNodes = 0;
		}
		
	}
	
	/**
	 * @param eventSocket
	 */
	private void handleOverlayNodeReportsTrafficSummary(EventSocket eventSocket) {
		OverlayNodeReportsTrafficSummary event = (OverlayNodeReportsTrafficSummary) eventSocket.getEvent();
		
		stats.addStatistics(event);
		
	}
}
