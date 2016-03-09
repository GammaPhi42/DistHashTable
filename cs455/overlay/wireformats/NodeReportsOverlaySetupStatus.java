package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NodeReportsOverlaySetupStatus implements Event {

	byte messageType;
	int successStatus;
	byte infoStringLength;
	byte[] infoString;
		
	public NodeReportsOverlaySetupStatus() {
		
	}
	
	public NodeReportsOverlaySetupStatus(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		int messageType;
		
		messageType = din.readByte();
		
		if(messageType != Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS) {
			System.out.println("Incorrect message type, received " + Protocol.getType(messageType) + " expected " + getType());
		}
		
		successStatus = din.readInt();
		infoStringLength = din.readByte();
		infoString = new byte[infoStringLength];
		din.readFully(infoString, 0, infoStringLength);
		
		baInputStream.close();
		din.close();
	}

	@Override
	public byte getType() {
		return Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeInt(successStatus);
		dout.writeByte(infoStringLength);
		dout.write(infoString);
		
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

	public int getSuccessStatus() {
		return successStatus;
	}

	public void setSuccessStatus(int successStatus) {
		this.successStatus = successStatus;
	}

	public byte getInfoStringLength() {
		return infoStringLength;
	}

	public void setInfoStringLength(byte infoStringLength) {
		this.infoStringLength = infoStringLength;
	}

	public byte[] getInfoString() {
		return infoString;
	}

	public void setInfoString(byte[] infoString) {
		this.infoString = infoString;
	}

}
