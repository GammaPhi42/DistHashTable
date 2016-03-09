package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
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
 * byte: Message Type (OVERLAY_NODE_SENDS_REGISTRATION)
 * byte: length of following "IP address" field
 * byte[^^]: IP address; from InetAddress.getAddress()
 * int: Port number
 */
public class OverlayNodeSendsRegistration implements Event {
	private byte messageType;
	private byte[] ipAddress;
	
	
	private byte messageIPAddressLength;
	private byte[] messageIPAddress;
	private int messagePort;
	
	public OverlayNodeSendsRegistration() {
		
	}
	
	public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
	
		messageType = din.readByte();
		
		if(messageType != Protocol.OVERLAY_NODE_SENDS_REGISTRATION) {
			System.out.println("Incorrect message type, received " + Protocol.getType(messageType) + " expected " + getType());
		}
		
		messageIPAddressLength = din.readByte();
		messageIPAddress = new byte[messageIPAddressLength];
		din.readFully(messageIPAddress, 0, messageIPAddressLength);
		
		
		messagePort = din.readInt();
		
		baInputStream.close();
		din.close();		
	}
	
	public byte getMessageIPAddressLength() {
		return messageIPAddressLength;
	}
	public void setMessageIPAddressLength(byte senderIPAddressLength) {
		this.messageIPAddressLength = senderIPAddressLength;
	}
	public byte[] getMessageIPAddress() {
		return messageIPAddress;
	}
	public void setMessageIPAddress(byte[] senderIPAddress) {
		this.messageIPAddress = senderIPAddress;
	}
	public int getMessagePort() {
		return messagePort;
	}
	public void setMessagePort(int port) {
		this.messagePort = port;
	}	
	@Override
	public byte getType() {
		return Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
	}

	/**
	 * Marshalls data specific to the wireformat
	 * Set lengthOfIPField, ipAddress, and port before using this
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
		
		dout.flush();
		
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}
	
	public void setIPAddress(byte[] ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public byte[] getIPAddress() {
		return ipAddress;
	}

}
