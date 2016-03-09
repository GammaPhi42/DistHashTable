package cs455.overlay.node;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventSocket;

public interface Node {

	/**
	 * @param take
	 */
	public void onEvent(EventSocket eventSocket);
}
