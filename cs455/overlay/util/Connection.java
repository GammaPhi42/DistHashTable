/**
 * 
 */
package cs455.overlay.util;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;

/**
 * @author Michael Miller
 *
 */
public class Connection implements Comparable<Connection> {
	private byte[] destinationIP;
	private int listeningPort;
	private int id;
	private TCPConnection tcpConnection;
	private boolean registered;
	private RoutingTable routingTable;
	
	public Connection(byte[] destinationIP, int listeningPort, int id, TCPConnection tcpConnection) {
		this.destinationIP = destinationIP;
		this.listeningPort = listeningPort;
		this.id = id;
		this.tcpConnection = tcpConnection;
		this.registered = false;
		this.routingTable = null;
		
	}

	public byte[] getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(byte[] destinationIP) {
		this.destinationIP = destinationIP;
	}

	public int getListeningPort() {
		return listeningPort;
	}

	public void setListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TCPConnection getTCPConnection() {
		return tcpConnection;
	}

	public void setTCPConnection(TCPConnection tcpConnection) {
		this.tcpConnection = tcpConnection;
	}

	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public boolean isRoutingTableInstalled() {
		return routingTable != null;
	}

	public void addRoutingTable(RoutingTable routingTable) {
		this.routingTable = routingTable;
	}
	
	public RoutingTable getRoutingTable() {
		return routingTable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Connection o) {
		if(this.id < o.id) 
			return -1;
		else if(this.id == o.id) 
			return 0;
		else //if(this.id > o.id)
			return 1;
	}
	
}
