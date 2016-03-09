package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//import cs455.overlay.exceptions.PortException;


public class TCPServerThread implements Runnable {
	public ServerSocket serverSocket;
	private volatile boolean acceptingConnections;
	private volatile TCPConnectionsCache tcpConnectionsCache;
	private static final boolean DEBUG = false; 
	
	/**
	 * 
	 * @param tcpConnectionsCache
	 * @param portNum Port on which to listen for incoming connections
	 * @throws IOException when an I/O error occurs when waiting for a connection
	 */
	public TCPServerThread(TCPConnectionsCache tcpConnectionsCache, int listenPort) throws IOException, IllegalArgumentException {
		this.serverSocket = new ServerSocket(listenPort);
		this.tcpConnectionsCache = tcpConnectionsCache;		
	}
	
	@Override
	/**
	 * Listens for connections from Nodes
	 */
	public void run() {
		acceptingConnections = true;
		
		while(acceptingConnections) {
			try {
				Socket socket = serverSocket.accept();
				tcpConnectionsCache.addConnection(new TCPConnection(socket));
				
			} catch (IOException ioe) { // TODO something here
				System.out.println("An I/O error occured while waiting for a connection");
			}
		}
		try { 
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	// called from a Node subclass
	public void stopListening() {
		acceptingConnections = false;
		if(DEBUG) 
			System.out.println("set \"acceptingConnections\" to false");
	}
	
	public int getListeningPort() {
		return serverSocket.getLocalPort();
	}
	
	
}
