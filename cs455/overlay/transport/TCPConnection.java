package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TCPConnection {
	private static final boolean DEBUG = true;
	private Socket socket;
	private TCPSender tcpSender;
	private TCPReceiverThread tcpReceiverThread;
	
	public TCPConnection(Socket socket) throws IOException {
		this.socket = socket;
		tcpSender = new TCPSender(socket);
		tcpReceiverThread = new TCPReceiverThread(socket);
		(new Thread(tcpReceiverThread,"ReceiverThread")).start();
			
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void send(byte[] data) throws IOException {
		if(DEBUG && data.length == 0) System.out.println("sending 0 length data!!!!!");
		
		tcpSender.sendData(data);
	}
	
	/*public String toString() {
		return socket.toString();
	}*/
	
	@SuppressWarnings("static-access")
	public boolean equals(byte[] destAddress) throws UnknownHostException {
		if(Arrays.equals(socket.getInetAddress().getAddress(), destAddress))
			return true;
		else
			return false;
	}
	
	public byte[] getDestinationAddress() {
		return socket.getInetAddress().getAddress();
	}
	
	public int getDestinationPort() {
		return socket.getPort();
	}
	
	public byte[] getLocalAddress() {
		return socket.getLocalAddress().getAddress();
	}
	
	public int getLocalPort() {
		return socket.getLocalPort();
	}
	public String toString() {
		return "Destination Address: " + getDestinationAddress() + "\n Local Address: " + getLocalAddress() + "\n destination port: " + getDestinationPort() + "\n Local port: " + getLocalPort() + "\n\n";
	}
}
