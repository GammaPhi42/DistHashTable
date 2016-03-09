/**
 * 
 */
package cs455.overlay.util;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import cs455.overlay.transport.TCPConnection;

/**
 * @author Michael Miller
 *
 */
public class ConnectionManager {
	private static final boolean DEBUG = false;
	ArrayList<Connection> connections;
	
	public ConnectionManager() {
		connections = new ArrayList<Connection>();
	}
	public ArrayList<Connection> getConnections() {
		return connections;
	}
	
	public Connection getConnection(Socket socket) {
		for(Connection c : connections) {
			if(c.getTCPConnection().getSocket().equals(socket)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param assignedID the ID of the node to loop up
	 * @return the connection associated with the ID
	 */
	public Connection getConnection(int assignedID) {
		for(Connection c : connections) {
			if(c.getId() == assignedID) {
				return c;
			}
		}
		return null;
	}
	
	public void sort() {
		Collections.sort(connections);
	}
	
	public boolean containsConnection(Socket socket) {
		for(Connection c : connections) {
			if(c.getTCPConnection().getSocket().equals(socket)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @param assignedID
	 * @return
	 */
	public boolean containsID(int assignedID) {
		for(Connection c : connections) {
			if(c.getId() == assignedID) {
				return true;
			}
		}
		return false;
	}
	
	public void addConnection(byte[] destinationIP, int listeningPort, int id, TCPConnection tcpConnection) {
		connections.add(new Connection(destinationIP, listeningPort, id, tcpConnection));
	}
	public int size() {
		return connections.size();
	}
	
	public Connection get(int index) {
		return connections.get(index);
	}
	
	public Connection remove(Socket socket) {
		Connection returnedConnection = null;
		Iterator<Connection> iterator = connections.iterator();
		while(iterator.hasNext()) {
			returnedConnection = iterator.next();
			if(returnedConnection.getTCPConnection().getSocket().equals(socket)) {
				connections.remove(returnedConnection);
				break;
			}
		}
		return returnedConnection;
		
	}
	
	public int[] getIDs() {
		int[] ids = new int[size()];
		for(int i = 0; i < size(); i++) {
			ids[i] = connections.get(i).getId();
		}
		
		return ids;
	}
	/**
	 * @return
	 */
	public boolean isReadyToInitTask() {
		for(Connection c : connections) {
			if(! c.isRoutingTableInstalled()) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	 */
	public void printConnections() {
		for(Connection c : connections) {
			try {
				System.out.println("Hostname: " + InetAddress.getByAddress(c.getDestinationIP()) + " Port-number: " + c.getListeningPort() + " Node ID: " + c.getId());
			} catch (UnknownHostException e) {
				System.out.println("Could not resolve hostname for Node with ID: " + c.getId() + " with destination port " + c.getListeningPort());
			}
		}
	}
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return connections.isEmpty();
	}
	
}
