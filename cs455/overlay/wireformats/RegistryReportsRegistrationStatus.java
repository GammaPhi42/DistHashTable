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
 * byte: Message type (REGISTRY_REPORTS_REGISTRATION_STATUS)
 * int: Success status; Assigned ID if successful, -1 in case of a failure
 * byte: Length of following "Information string" field
 * byte[^^]: Information string; ASCII charset
 *
 */
public class RegistryReportsRegistrationStatus implements Event {
	private byte messageType;
	private int successStatus;
	private byte infoStringLength;
	private byte[] infoString;
	
	public RegistryReportsRegistrationStatus() {
		
	}
	
	public RegistryReportsRegistrationStatus(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(baInputStream);

		messageType = din.readByte();		

		if(messageType != Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS) {
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
		// TODO Auto-generated method stub
		return Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
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
