/**
 * 
 */
package cs455.overlay.wireformats;

import java.net.Socket;

/**
 * @author Michael Miller
 *
 */
public class EventSocket {
	private Event event;
	private Socket socket;
	
	public EventSocket(Event event, Socket socket) {
		this.event = event;
		this.socket = socket;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public Socket getSocket() {
		return socket;
	}
}
