/**
 * 
 */
package cs455.overlay.util;

/**
 * @author Michael Miller
 *
 */
public class IDIPPort {
	int id;
	byte[] ip;
	int listeningPort;
	
	public IDIPPort(int id, byte[] ip, int listeningPort) {
		this.id = id;
		this.ip = ip;
		this.listeningPort = listeningPort;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getIP() {
		return ip;
	}
	
	public void setIP(byte[] ip) {
		this.ip = ip;
	}
	
	public int getListeningPort() {
		return listeningPort;
	}

	public void setListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}
	
}
