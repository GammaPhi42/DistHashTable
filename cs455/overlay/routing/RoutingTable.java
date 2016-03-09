package cs455.overlay.routing;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import cs455.overlay.wireformats.OverlayNodeSendsData;

public class RoutingTable {
	private static final boolean DEBUG = true;
	private int capacity;
	private int size;
	ArrayList<RoutingEntry> routingEntries;
	boolean sorted;
	
	
	public RoutingTable(int capacity) {
		this.capacity = capacity;
		size = 0;
		routingEntries = new ArrayList<RoutingEntry>(capacity);
		sorted = false;
	}
	public int getCapacity() {
		return capacity;
	}
	
	public int getSize() {
		return routingEntries.size();
	}
	
	public RoutingEntry getFarthestNode(int[] passthruNodeIDs) {
		RoutingEntry currentFarthestNode = routingEntries.get(0);
		for(RoutingEntry r : routingEntries) {
			if(r.getWeight() > currentFarthestNode.getWeight() && ! Arrays.asList(passthruNodeIDs).contains(r.getNodeID()) )
				currentFarthestNode = r;
		}
		
		return currentFarthestNode;
	}
	
	public boolean containsID(int id) {
		for(RoutingEntry r : routingEntries) {
			if(r.getNodeID() == id) {
				return true;
			}
		}
		return false;
	}
	
	public void add(RoutingEntry routingEntry) {
		routingEntries.add(routingEntry);
	}
	
	public RoutingEntry getIndex(int index) {
		return routingEntries.get(index);
	}
	/**
	 * 
	 */
	public void sort() {
		Collections.sort(routingEntries, new Comparator<RoutingEntry>() {

			@Override
			public int compare(RoutingEntry arg0, RoutingEntry arg1) {
				if(arg0.getNodeID() < arg1.getNodeID()) return -1;
				else return 1;
			}
			
		});	
		
		sorted = true;
	}
	/**
	 * @return
	 */
	public boolean isSorted() {
		// TODO Auto-generated method stub
		return sorted;
	}
	/**
	 * @param message
	 * @param assignedID 
	 * @return
	 */
	public int findAppropriateNode(OverlayNodeSendsData message, int assignedID, int[] nodeIDs) {
		int destID = message.getDestinationID();
		int numHopsAway;
		Arrays.sort(nodeIDs);
		int assignedIDIndex = Arrays.binarySearch(nodeIDs, assignedID);
		int destIDIndex = Arrays.binarySearch(nodeIDs, destID);
		int candidateWeight = -1;
		
		if(destIDIndex < assignedIDIndex) {
			numHopsAway = nodeIDs.length - assignedIDIndex + destIDIndex;
		}
		else { // destIDIndex > assignedIDIndex
			numHopsAway = destIDIndex - assignedIDIndex;
		}
				
		for(RoutingEntry r : routingEntries) {
			if(r.getWeight() < numHopsAway && r.getWeight() > candidateWeight) {
				candidateWeight = r.getWeight();
			}
		}
		
		if(candidateWeight == -1) {
			System.out.println("ASSUMPTION VIOLATED!!!");
			return -1;
		}
		else {
			int candidateIndex = -1;
			for(RoutingEntry r : routingEntries) {
				if(r.getWeight() == candidateWeight) {
					candidateIndex = Arrays.binarySearch(nodeIDs, r.getNodeID());
					break;
				}
			}
			return nodeIDs[candidateIndex];
			
		}
	}
	
	public String toString() {
		String returnString = "";
		for(RoutingEntry r : routingEntries) {
			returnString += r.toString();
		}
		returnString += "\n\n\n";
		return returnString;
	}
}
