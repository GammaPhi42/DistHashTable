package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {
	private DataOutputStream dout;
	
	public TCPSender(Socket socket) throws IOException {
		dout = new DataOutputStream(socket.getOutputStream());
	}
	
	public void sendData(byte[] dataToSend) throws IOException {		
		dout.writeInt(dataToSend.length);
		dout.write(dataToSend, 0, dataToSend.length);
		dout.flush();
	}
}
