/**
 * 
 */
package cs455.overlay.util;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventSocket;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;

/**
 * 
 * @author Michael Miller
 * EventHandlerThread is a separate thread spawned by a Node. 
 * This class is responsible for executing actions related to the contents of the Event received, e.g. replying to a OverlayNodeSendsRegistration Event
 * Events received are put into a BlockingQueue via the TCPReceiverThread
 */
public class EventHandlerThread extends Thread {
	private static final boolean DEBUG = true;
	private static LinkedBlockingQueue<EventSocket> eventList;
	Node node;
	volatile boolean acceptingEvents;
	
	public EventHandlerThread(Node node) {
		eventList = new LinkedBlockingQueue<EventSocket>();
		this.node = node;
		acceptingEvents = true;

	}
	
	public static void queueEvent(Event event, Socket socket) {
		eventList.add(new EventSocket(event,socket));
	}
	
	public boolean isEmpty() {
		if(eventList.isEmpty())
			return true;
		return false;
	}
	
	public void stopAcceptingEvents() {
		acceptingEvents = false;
		if(DEBUG)System.out.println("Stopped accepting events");
	}
	
	public void run() {
		// TODO Auto-generated method stub
		while(acceptingEvents) {
			try {
				node.onEvent(eventList.take());
			} catch (InterruptedException ignored) {
				//ignore
				
			}
		}
		
		
	}
	
}
