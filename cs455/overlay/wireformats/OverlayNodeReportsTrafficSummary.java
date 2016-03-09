package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTrafficSummary implements Event {

	byte messageType;
	int nodeID;
	int numPacketsSent;
	int numPacketsRelayed;
	long sumPacketsSent;
	int numPacketsReceived;
	long sumPacketsReceived;
	
	public OverlayNodeReportsTrafficSummary(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(baInputStream);

		messageType = din.readByte();		

		if(messageType != Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY) {
			System.out.println("Incorrect message type, received " + Protocol.getType(messageType) + " expected " + getType());
		}

		
		nodeID = din.readInt();
		numPacketsSent = din.readInt();
		numPacketsRelayed = din.readInt();
		sumPacketsSent = din.readLong();
		numPacketsReceived = din.readInt();
		sumPacketsReceived = din.readLong();
				
		baInputStream.close();
		din.close();
	}

	/**
	 * 
	 */
	public OverlayNodeReportsTrafficSummary() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte getType() {
		// TODO Auto-generated method stub
		return Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeInt(nodeID);
		dout.writeInt(numPacketsSent);
		dout.writeInt(numPacketsRelayed);
		dout.writeLong(sumPacketsSent);
		dout.writeInt(numPacketsReceived);
		dout.writeLong(sumPacketsReceived);
		
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

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getNumPacketsSent() {
		return numPacketsSent;
	}

	public void setNumPacketsSent(int numPacketsSent) {
		this.numPacketsSent = numPacketsSent;
	}

	public int getNumPacketsRelayed() {
		return numPacketsRelayed;
	}

	public void setNumPacketsRelayed(int numPacketsRelayed) {
		this.numPacketsRelayed = numPacketsRelayed;
	}

	public long getSumPacketsSent() {
		return sumPacketsSent;
	}

	public void setSummationPacketsSent(long sumPacketsSent) {
		this.sumPacketsSent = sumPacketsSent;
	}

	public int getNumPacketsReceived() {
		return numPacketsReceived;
	}

	public void setNumPacketsReceived(int numPacketsReceived) {
		this.numPacketsReceived = numPacketsReceived;
	}

	public long getSumPacketsReceived() {
		return sumPacketsReceived;
	}

	public void setSummationPacketsReceived(long sumPacketsReceived) {
		this.sumPacketsReceived = sumPacketsReceived;
	}

}
