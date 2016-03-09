package cs455.overlay.transport;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Michael Miller
 * TCPConnectionsCache is a wrapper around an ArrayList of TCPConnections. Connections are added via the TCPServerThread. 
 *
 */
public class TCPConnectionsCache {
	private static volatile ArrayList<TCPConnection> tcpConnections;
	
	public TCPConnectionsCache () {
		tcpConnections = new ArrayList<TCPConnection>();
	}
	
	public synchronized void addConnection(TCPConnection tcpConnection) {
		tcpConnections.add(tcpConnection);	
	}
	
	public synchronized TCPConnection getConnection(Socket socket) {
		for(TCPConnection tcpConnection : tcpConnections) {
			if(tcpConnection.getSocket() == socket)
				return tcpConnection;
		}
		return null;
	}
	
	public synchronized boolean containsConnection(Socket socket) {
		for(TCPConnection tcpConnection : tcpConnections) {
			if(tcpConnection.getSocket().equals(socket)) {
				return true;
			}
		}
		return false;
	}
	
	public TCPConnection remove(Socket socket) {
		TCPConnection returnedConnection = null;
		if(containsConnection(socket)) {
			returnedConnection = getConnection(socket);
		}
		tcpConnections.remove(returnedConnection);
		return returnedConnection;
	}
	
	public String printConnections() {
		String returnString = "";
		for(TCPConnection tcpConnection : tcpConnections) {
			returnString += tcpConnection.toString();
		}
		return returnString;
	}
	
	/**
	 * Check if all connections in cache are still alive, and removes one that is dead (to be executed often, i.e. 500millis).
	 * @return null if all TCPConnections are still alive, otherwise returns the TCPConnection that has gone dead.
	 */
	public TCPConnection areAlive() {
		for(TCPConnection tcpConnection : tcpConnections) {
			if(tcpConnection.getSocket().isClosed()) {
				tcpConnections.remove(tcpConnection);
				return tcpConnection;
			}
		}
		
		return null;
	}
}
