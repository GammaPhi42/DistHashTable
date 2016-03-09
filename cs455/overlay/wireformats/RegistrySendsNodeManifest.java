package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import cs455.overlay.routing.RoutingTable;

public class RegistrySendsNodeManifest implements Event {

	byte messageType;
	
	byte tableCapacity;
	
	int[] connectingNodeIDs;
	byte[][] ipAddresses;
	int[] ports;
	byte ipAddressLength;
	
	int[] allNodeIDs;
	byte numNodeIDs;
	
	public RegistrySendsNodeManifest(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(baInputStream);

		messageType = din.readByte();		

		if(messageType != Protocol.REGISTRY_SENDS_NODE_MANIFEST) {
			System.out.println("Incorrect message type, received " + Protocol.getType(messageType) + " expected " + getType());
		}

		tableCapacity = din.readByte();
		connectingNodeIDs = new int[(int)tableCapacity];
		ipAddresses = new byte[(int)tableCapacity][4];
		ports = new int[(int)tableCapacity];
		
		for(int i = 0; i < (int)tableCapacity; i++) {
			connectingNodeIDs[i] = din.readInt();
			ipAddressLength = din.readByte();
			din.readFully(ipAddresses[i], 0, (int)ipAddressLength);
			ports[i] = din.readInt();			
		}
		
		numNodeIDs = din.readByte();
		allNodeIDs = new int[(int)numNodeIDs];
		for(int i = 0; i < (int)numNodeIDs; i++) {
			allNodeIDs[i] = din.readInt();
		}
		 
		
		baInputStream.close();
		din.close();
	}

	/**
	 * @param routingTable
	 */
	public RegistrySendsNodeManifest(RoutingTable routingTable, int[] nodeIDs) {
		tableCapacity = (byte) routingTable.getCapacity();
		connectingNodeIDs = new int[(int)tableCapacity];
		ipAddresses = new byte[(int)tableCapacity][4];
		ports = new int[(int)tableCapacity];
		
		allNodeIDs = new int[nodeIDs.length];
		numNodeIDs = (byte) nodeIDs.length;
		
		for(int i = 0; i < (int)tableCapacity; i++) {
			connectingNodeIDs[i] = routingTable.getIndex(i).getNodeID();
			ipAddresses[i] = routingTable.getIndex(i).getDestinationIPAddress();
			ports[i] = routingTable.getIndex(i).getDestinationPort();
		}
		
		for(int i = 0; i < nodeIDs.length; i++) {
			allNodeIDs[i] = nodeIDs[i];
		}
		
	}

	@Override
	public byte getType() {
		// TODO Auto-generated method stub
		return Protocol.REGISTRY_SENDS_NODE_MANIFEST;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(tableCapacity);

		for(int i = 0; i < (int)tableCapacity; i++) {
			dout.writeInt(connectingNodeIDs[i]);
			dout.writeByte((byte) ipAddresses[i].length);
			dout.write(ipAddresses[i]);
			dout.writeInt(ports[i]);			
		}
		
		dout.writeByte((byte) allNodeIDs.length);
		for(int i = 0; i < allNodeIDs.length; i++) {
			dout.writeInt(allNodeIDs[i]);
		}
		
		dout.flush();
		
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}

	public int getMessageType() {
		return messageType;
	}

	public byte getTableSize() {
		return tableCapacity;
	}

	public int[] getConnectingNodeIDs() {
		return connectingNodeIDs;
	}

	public byte[][] getIpAddresses() {
		return ipAddresses;
	}

	public int[] getPorts() {
		return ports;
	}

	public int[] getAllNodeIDs() {
		return allNodeIDs;
	}

	public byte getNumNodeIDs() {
		return numNodeIDs;
	}

}
