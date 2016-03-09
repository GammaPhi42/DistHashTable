package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsData implements Event {

	byte messageType;
	int destinationID;
	int sourceID;
	
	int payload;
	
	int numHops;
	
	int[] passthruNodeIDs;
	
	public OverlayNodeSendsData() {
		
	}
	public OverlayNodeSendsData(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		
		messageType = din.readByte();
		
		destinationID = din.readInt();
		sourceID = din.readInt();
		payload = din.readInt();
		numHops = din.readInt();
		passthruNodeIDs = new int[numHops + 1];
		for(int i = 0 ; i < numHops; i++) {
			passthruNodeIDs[i] = din.readInt();
		}
		
		
		baInputStream.close();
		din.close();
	}

	@Override
	public byte getType() {
		// TODO Auto-generated method stub
		return Protocol.OVERLAY_NODE_SENDS_DATA;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
	
		dout.writeByte(messageType);
		dout.writeInt(destinationID);
		dout.writeInt(sourceID);
		dout.writeInt(payload);
		dout.writeInt(numHops);
		for(int i = 0; i < numHops; i++) {
			dout.writeInt(passthruNodeIDs[i]);
		}
		
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
	public int getDestinationID() {
		return destinationID;
	}
	public void setDestinationID(int destinationID) {
		this.destinationID = destinationID;
	}
	public int getSourceID() {
		return sourceID;
	}
	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}
	public int getPayload() {
		return payload;
	}
	public void setPayload(int payload) {
		this.payload = payload;
	}
	public int getNumHops() {
		return numHops;
	}
	public void setNumHops(int numHops) {
		this.numHops = numHops;
	}
	public int[] getPassthruNodeIDs() {
		return passthruNodeIDs;
	}
	public void setPassthruNodeIDs(int[] passthruNodeIDs) {
		this.passthruNodeIDs = passthruNodeIDs;
	}

}
