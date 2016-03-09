package cs455.overlay.routing;

import java.net.InetAddress;
import java.net.UnknownHostException;

import cs455.overlay.transport.TCPConnection;

public class RoutingEntry implements Comparable<RoutingEntry> {
	
	int nodeID;
	int destinationPort;
	byte[] destinationIPAddress;
	int weight;
	
	public RoutingEntry(int nodeID, byte[] destinationIPAddress, int destinationPort, int weight) {
		this.nodeID = nodeID;
		this.destinationPort = destinationPort;
		this.destinationIPAddress = new byte[4];
		this.destinationIPAddress = destinationIPAddress;
		this.weight = weight;
	}
	public Integer getNodeID() {
		return nodeID;
	}
	public void setNodeID(Integer nodeID) {
		this.nodeID = nodeID;
	}
	public int getDestinationPort() {
		return destinationPort;
	}
	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}
	public byte[] getDestinationIPAddress() {
		return destinationIPAddress;
	}
	public void setDestinationIPAddress(byte[] destinationIPAddress) {
		this.destinationIPAddress = destinationIPAddress;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RoutingEntry o) {
		// TODO Auto-generated method stub
		if(this.getNodeID() > o.getNodeID())
			return 1;
		else return -1;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public String toString() {
		try {
			return "Hostname: " + InetAddress.getByAddress(destinationIPAddress) + " Port-number: " + destinationPort + " Node ID: " + nodeID + "\n";
		} catch (UnknownHostException e) {
			return "Could not resolve hostname for Node with ID: " + nodeID + " at destination port " + destinationPort;			
		}
	}
}
