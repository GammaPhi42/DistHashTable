package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.ConnectionManager;
import cs455.overlay.util.EventHandlerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.EventSocket;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.OverlayNodeSendsData;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

public class MessagingNode implements Node {
	
	public static final int MAX_NUM_NODES = 127;
	private static final boolean DEBUG = true;
	private TCPConnection registryConnection;
	private TCPServerThread tcpServerThread; // listen for incoming connections from other MessagingNodes
	private volatile TCPConnectionsCache tcpConnectionsCache;
	private InteractiveCommandParser interactiveCommandParser;
	private volatile LinkedBlockingQueue<String> commandQueue;
		
	private EventHandlerThread eventHandlerThread;
	
	private volatile boolean acceptingCommands; // TODO does this need to be volatile?
	
	private int assignedID;
	
	private volatile RoutingTable routingTable;
	
	private int[] nodeIDs;
	
	private ConnectionManager connectionManager;
	
	private static Random payloadGenerator;
	
	private volatile int sendTracker;
	private volatile int receiveTracker;
	private volatile int relayTracker;
	private volatile long sendSummation;
	private volatile long receiveSummation;
	
	public MessagingNode(Socket registrySocket) throws IOException {
		sendTracker = 0;
		receiveTracker = 0;
		relayTracker = 0;
		sendSummation = 0L;
		receiveSummation = 0L;
		
		registryConnection = new TCPConnection(registrySocket);
		
		tcpConnectionsCache = new TCPConnectionsCache();
		
		tcpServerThread = new TCPServerThread(tcpConnectionsCache, 0);
		(new Thread(tcpServerThread)).start();
		
		
		commandQueue = new LinkedBlockingQueue<String>();
		interactiveCommandParser = new InteractiveCommandParser(this, commandQueue);
		(new Thread(interactiveCommandParser)).start();
		
		eventHandlerThread = new EventHandlerThread(this);
		eventHandlerThread.start();
		
		
		
		OverlayNodeSendsRegistration message = new OverlayNodeSendsRegistration();
		message.setMessageIPAddressLength((byte) registrySocket.getLocalAddress().getAddress().length);
		message.setMessageIPAddress(registrySocket.getLocalAddress().getAddress());
		message.setMessagePort(tcpServerThread.getListeningPort());
		
		registryConnection.send(message.getBytes());
		
		acceptingCommands = true;
		
		connectionManager = new ConnectionManager();
		
		payloadGenerator = new Random();
			
	}
	
	/**
	 * 
	 * @param args [0] registry hostname, [1] registry port
	 */
	public static void main(String args[]) { 
		int registryPort;
		String registryIP;
		
		if(args.length != 2) {
			System.out.println("MessagingNode: Not enough arguments!");
			return;
		}
		
		registryPort = Integer.parseInt(args[1]);
		registryIP = args[0];
		
		MessagingNode messagingNode = null;
		
		do {
			try {
				messagingNode = new MessagingNode(new Socket(registryIP, registryPort));
				
				messagingNode.waitForCommands();
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.out.println("\n\n\nCould not connect to Registry Socket!");
			} finally {
				System.out.println("Closing");
			}
			if(messagingNode == null) {
				System.out.println("Connection failed. Ensure you have the correct port.\n Enter port of Registry:");
				registryPort = Integer.parseInt((new Scanner(System.in)).next());
			}
		} while(messagingNode == null);
				
	}

	private void waitForCommands() throws IOException {
		String commandToExecute;
		while(acceptingCommands) {
			try {
				commandToExecute = commandQueue.take();
				if(commandToExecute.equals("print-counters-and-diagnostics")) {
					System.out.println("This node has ID: " + assignedID);
					System.out.println("Packets sent: " + sendTracker);
					System.out.println("Packets relayed: " + relayTracker);
					System.out.println("Summation of packets sent: " + sendSummation);
					System.out.println("Number of packets received: " + receiveTracker);
					System.out.println("Summation of packets received: " + receiveSummation);
				} else if(commandToExecute.equalsIgnoreCase("exit-overlay")) {
					OverlayNodeSendsDeregistration message = new OverlayNodeSendsDeregistration();
					message.setMessageIPAddressLength((byte) InetAddress.getLocalHost().getAddress().length);
					message.setMessageIPAddress(InetAddress.getLocalHost().getAddress());
					message.setMessagePort(tcpServerThread.getListeningPort());
					message.setAssignedNodeID(assignedID);
					registryConnection.send(message.getBytes());
				} else if(commandToExecute.equals("RegistryRequestsTrafficSummary")) {
					handleRegistryRequestsTrafficSummary();
				} else {
					System.out.println("Command not recognized!\n" + commandUsage());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * @return
	 */
	private String commandUsage() {
		return "Available commands are:\nprint-counters-and-diagnostics\nexit-overlay";
	}

	@Override
	public void onEvent(EventSocket eventSocket) {

		if(eventSocket.getEvent() instanceof OverlayNodeSendsData) {
			try {
				handleOverlayNodeSendsData(eventSocket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("error receiving OverlayNodeSendsData");
			}
		} else if(eventSocket.getEvent() instanceof RegistryReportsRegistrationStatus) {
			handleRegistryReportsRegistrationStatus(eventSocket);			
		}
		else if(eventSocket.getEvent() instanceof RegistryReportsDeregistrationStatus) {
			handleRegistryReportsDeregistrationStatus(eventSocket);			
		}
		else if(eventSocket.getEvent() instanceof RegistrySendsNodeManifest) {
			handleRegistrySendsNodeManifest(eventSocket);
		}
		else if(eventSocket.getEvent() instanceof RegistryRequestsTaskInitiate) {
			try {
				handleRegistryRequestsTaskInitiate(eventSocket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		else if(eventSocket.getEvent() instanceof RegistryRequestsTrafficSummary) {
			try {
				handleRegistryRequestsTrafficSummary();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		else
			System.out.println("Received an event of unknown type!");
		
	}

	

	/**
	 * @param eventSocket
	 * @throws IOException 
	 */
	private void handleOverlayNodeSendsData(EventSocket eventSocket) throws IOException {
		OverlayNodeSendsData message = (OverlayNodeSendsData) eventSocket.getEvent();
		boolean messageSent = false;
		
		message.setNumHops(message.getNumHops() + 1);
		int[] passthruNodeIDs = new int[message.getNumHops()];
		
		passthruNodeIDs[passthruNodeIDs.length - 1] = assignedID;
		message.setPassthruNodeIDs(passthruNodeIDs);
		
		if(message.getDestinationID() == assignedID) {
			receiveSummation += (long)message.getPayload();
			receiveTracker += 1;
		}
		
		else if(routingTable.containsID(message.getDestinationID())) {
			// if exists in routing Table, send directly
			connectionManager.getConnection(message.getDestinationID()).getTCPConnection().send(message.getBytes());
			relayTracker += 1;
		}
		else {
			// if doesn't exist in routing table, send to greatest nodeID that does not exceed destination node ID
			connectionManager.getConnection(routingTable.findAppropriateNode(message,assignedID,nodeIDs)).getTCPConnection().send(message.getBytes());
			relayTracker += 1;
			
		}
		
	}

	public void handleRegistryReportsRegistrationStatus(EventSocket eventSocket) {
		RegistryReportsRegistrationStatus e = (RegistryReportsRegistrationStatus) eventSocket.getEvent();
		int successStatus = e.getSuccessStatus();
		if(successStatus == -1) { 
			System.out.println("Registry reports registration failed.");
			System.out.println(String.valueOf(e.getInfoString()));
			System.out.println("Launch the process again to try again.\nExiting...");
			System.exit(1);
		} else {
			assignedID = successStatus;
			System.out.println(new String(e.getInfoString()));
		}
		
		
	}
	
	public void handleRegistryReportsDeregistrationStatus(EventSocket eventSocket) {
		RegistryReportsDeregistrationStatus e = (RegistryReportsDeregistrationStatus) eventSocket.getEvent();
		int successStatus = (e.getSuccessStatus());
		if(successStatus == assignedID) {
			System.out.println("Registry reports deregistration successful.\nExiting...");
			System.exit(0);
		}
		else {
			System.out.println("Registry reports deregistration unsuccessful.");
			System.out.println(e.getInfoString());
		}
	}
	
	/**
	 * @param eventSocket
	 */
	private void handleRegistrySendsNodeManifest(EventSocket eventSocket) {
		System.out.println("Received manifest");
		RegistrySendsNodeManifest e = (RegistrySendsNodeManifest) eventSocket.getEvent();
		routingTable = new RoutingTable(e.getTableSize());
		// TODO this loop could be causing a problem, otherwise NPE in initiate connections
		for(int i = 0; i < routingTable.getCapacity(); i++) {
			routingTable.add(new RoutingEntry(e.getConnectingNodeIDs()[i], e.getIpAddresses()[i], e.getPorts()[i], (int)Math.pow((double)2, (double)i)));
		}
		
		nodeIDs = new int[e.getNumNodeIDs()];
		
		nodeIDs = e.getAllNodeIDs();
		
		try {
			initiateConnections();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Could not initiate connections!");
			// TODO send message back to registry saying so
		}		
	}
	
	private void initiateConnections() throws UnknownHostException, IOException {
		Socket socket;
		NodeReportsOverlaySetupStatus replyMessage = new NodeReportsOverlaySetupStatus();
		
		for(int i = 0 ; i < routingTable.getSize(); i++) {
			socket = new Socket(InetAddress.getByAddress(routingTable.getIndex(i).getDestinationIPAddress()),
										routingTable.getIndex(i).getDestinationPort());

			tcpConnectionsCache.addConnection(new TCPConnection(socket));
					
			connectionManager.addConnection(socket.getInetAddress().getAddress(), socket.getPort(), routingTable.getIndex(i).getNodeID(), tcpConnectionsCache.getConnection(socket));
		
			
		}
				
		replyMessage.setSuccessStatus(assignedID);
		String infoString = "Node has successfully initiated connections";
		replyMessage.setInfoStringLength((byte)infoString.length());
		replyMessage.setInfoString(infoString.getBytes());
		
		registryConnection.send(replyMessage.getBytes());
		
	}
	
	/**
	 * @param eventSocket
	 * @throws IOException 
	 */
	private void handleRegistryRequestsTaskInitiate(EventSocket eventSocket) throws IOException {
		int numPacketsToSend;
		RegistryRequestsTaskInitiate event = (RegistryRequestsTaskInitiate) eventSocket.getEvent();
		numPacketsToSend = event.getNumPacketsToSend();
		int payloadToSend;
		int destinationIDIndex;
		OverlayNodeSendsData message;
		
		sendTracker = 0;
		relayTracker = 0;
		sendSummation = 0L;
		receiveTracker = 0;
		receiveSummation = 0L;
		
		for(int i = 0; i < numPacketsToSend; i++) {
			message = new OverlayNodeSendsData();
			payloadToSend = payloadGenerator.nextInt();
			
			sendTracker += 1;
			
			sendSummation += (long) payloadToSend;
						
			// choosing random nodeID to send payload to
			do {
				destinationIDIndex = payloadGenerator.nextInt(nodeIDs.length);
			} while(nodeIDs[destinationIDIndex] == assignedID);
			
			message.setDestinationID(nodeIDs[destinationIDIndex]);
			message.setSourceID(assignedID);
			message.setNumHops(0);
			message.setPayload(payloadToSend);
			message.setPassthruNodeIDs(new int[nodeIDs.length]);
			message.setMessageType(Protocol.OVERLAY_NODE_SENDS_DATA);
			
			if(routingTable.containsID(message.getDestinationID())) {
				// if exists in routing Table, send directly
				try{
					connectionManager.getConnection(message.getDestinationID()).getTCPConnection().send(message.getBytes());
				} catch (NullPointerException | IOException ioe) {
					ioe.getStackTrace();
					ioe.getMessage();
				}
			}
			else {
				// else not in routing table
				connectionManager.getConnection(routingTable.findAppropriateNode(message,assignedID, nodeIDs)).getTCPConnection().send(message.getBytes());
			}
			
			// experimental
			try {
				Thread.sleep(2L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// After sending all messages
		reportTaskFinished();
		
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private void reportTaskFinished() throws IOException {
		OverlayNodeReportsTaskFinished message = new OverlayNodeReportsTaskFinished();
		message.setIPAddressLength((byte) registryConnection.getLocalAddress().length);
		message.setIPAddress(registryConnection.getLocalAddress());
		message.setPort(tcpServerThread.getListeningPort());
		registryConnection.send(message.getBytes());
		
	}
	
	/**
	 * @param eventSocket
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void handleRegistryRequestsTrafficSummary() throws IOException, InterruptedException {		
		OverlayNodeReportsTrafficSummary replyMessage = new OverlayNodeReportsTrafficSummary();
		
		replyMessage.setNodeID(assignedID);
		replyMessage.setNumPacketsSent(sendTracker);
		replyMessage.setNumPacketsRelayed(relayTracker);
		replyMessage.setSummationPacketsSent(sendSummation);
		replyMessage.setNumPacketsReceived(receiveTracker);
		replyMessage.setSummationPacketsReceived(receiveSummation);
		
		if(DEBUG) System.out.println("Sending traffic summary to registry");
		registryConnection.send(replyMessage.getBytes());
	}
}
