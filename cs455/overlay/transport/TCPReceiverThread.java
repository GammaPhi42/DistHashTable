package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.util.EventHandlerThread;
import cs455.overlay.wireformats.EventFactory;


public class TCPReceiverThread implements Runnable{
	private static final boolean DEBUG = true;
	private Socket socket;
	private DataInputStream din;
	
	public TCPReceiverThread(Socket socket) throws IOException {
		this.socket = socket;
		din = new DataInputStream(socket.getInputStream());
	}

	public void run() {
		int dataLength;
		while(socket != null) {
			try {
				dataLength = din.readInt();
				
				byte[] data = new byte[dataLength];
				din.readFully(data, 0, dataLength);
				
				if(DEBUG && data.length == 0) {
					System.out.println("Incoming data has no length!!");
				}
				
				EventHandlerThread.queueEvent(EventFactory.getInstance().getEvent(data),socket );
				
			} catch (SocketException se) { // TODO need to escalate these exceptions?
				System.out.println(se.getMessage());
				break;
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
				break;
			} catch (Exception e) {
				e.getMessage();
				e.printStackTrace();
				System.out.println("Received a message of unknown type!");
			}
		}
	}
	
	
}