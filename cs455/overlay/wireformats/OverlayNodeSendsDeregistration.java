package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * @author Michael Miller
 *
 * Wireformat:
 * byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
 * byte: length of following "IP address" field
 * byte[^^]: IP address; from InetAddress.getAddress()
 * int: Port number
 * int: assigned Node ID
 * 
 */
public class OverlayNodeSendsDeregistration implements Event {
	private byte messageType;
	private byte messageIPAddressLength;
	private byte[] messageIPAddress;
	private int messagePort;
	private int assignedNodeID;
	
	public OverlayNodeSendsDeregistration() {
		
	}
	
	protected OverlayNodeSendsDeregistration(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(baInputStream);

		messageType = din.readByte();		

		if(messageType != Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION) {
			System.out.println("Incorrect message type, received " + Protocol.getType(messageType) + " expected " + getType());
		}

		messageIPAddressLength = din.readByte();

		messageIPAddress = new byte[messageIPAddressLength];
		din.readFully(messageIPAddress, 0 , messageIPAddressLength);

		messagePort = din.readInt();

		assignedNodeID = din.readInt();
		
		baInputStream.close();
		din.close();		
	}

	@Override
	public byte getType() {
		return Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
	}

	/**
	 * Marshalls data specific to the wireformat
	 * Set lengthOfIPField, ipAddress, port, and assignedNodeID before using this
	 * @return Marshalled data as a byte[]
	 * @throws IOException 
	 */
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(getMessageIPAddressLength());
		dout.write(getMessageIPAddress());
		dout.writeInt(getMessagePort());
		dout.writeInt(getAssignedNodeID());
		
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

	public byte getMessageIPAddressLength() {
		return messageIPAddressLength;
	}

	public void setMessageIPAddressLength(byte messageIPAddressLength) {
		this.messageIPAddressLength = messageIPAddressLength;
	}

	public byte[] getMessageIPAddress() {
		return messageIPAddress;
	}

	public void setMessageIPAddress(byte[] messageIPAddress) {
		this.messageIPAddress = messageIPAddress;
	}

	public int getMessagePort() {
		return messagePort;
	}

	public void setMessagePort(int messagePort) {
		this.messagePort = messagePort;
	}

	public int getAssignedNodeID() {
		return assignedNodeID;
	}

	public void setAssignedNodeID(int assignedNodeID) {
		this.assignedNodeID = assignedNodeID;
	}
}