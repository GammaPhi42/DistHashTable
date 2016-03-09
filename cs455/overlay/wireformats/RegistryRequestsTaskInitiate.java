package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryRequestsTaskInitiate implements Event {

	byte messageType;
	int numPacketsToSend;
	
	public RegistryRequestsTaskInitiate() {
		
	}
	public RegistryRequestsTaskInitiate(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		
		messageType = din.readByte();
		
		numPacketsToSend = din.readInt();
		
		baInputStream.close();
		din.close();
	}
	

	public int getNumPacketsToSend() {
		return numPacketsToSend;
	}
	
	public void setNumPacketsToSend(int numPacketsToSend) {
		this.numPacketsToSend = numPacketsToSend;
	}

	@Override
	public byte getType() {
		// TODO Auto-generated method stub
		return Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeInt(numPacketsToSend);
		
		dout.flush();
		
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}

}
