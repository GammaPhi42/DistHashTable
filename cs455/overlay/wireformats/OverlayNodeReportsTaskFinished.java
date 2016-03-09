package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTaskFinished implements Event {

	byte messageType;
	byte ipAddressLength;
	byte[] ipAddress;
	int port;
	int nodeID;
	
	public OverlayNodeReportsTaskFinished(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(baInputStream);

		messageType = din.readByte();		

		if(messageType != Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED) {
			System.out.println("Incorrect message type, received " + Protocol.getType(messageType) + " expected " + getType());
		}

		ipAddressLength = din.readByte();
		ipAddress = new byte[ipAddressLength];
		din.readFully(ipAddress, 0 , ipAddressLength);
		nodeID = din.readInt();
		 
		
		baInputStream.close();
		din.close();
	}

	/**
	 * 
	 */
	public OverlayNodeReportsTaskFinished() {
		
	}

	@Override
	public byte getType() {
		// TODO Auto-generated method stub
		return Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(ipAddressLength);
		dout.write(ipAddress);
		dout.writeInt(nodeID);
		
		dout.flush();
		
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}

	public byte getMessageType() {
		return messageType;
	}

	public void setMessageType(byte messageType) {
		this.messageType = messageType;
	}

	public byte getIPAddressLength() {
		return ipAddressLength;
	}

	public void setIPAddressLength(byte ipAddressLength) {
		this.ipAddressLength = ipAddressLength;
	}

	public byte[] getIPAddress() {
		return ipAddress;
	}

	public void setIPAddress(byte[] ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
