package cs455.overlay.util;

import java.util.ArrayList;

import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;

public class StatisticsCollectorAndDisplay {
	
	ArrayList<InfoTuple> infoTuples;
	int totalNumNodes;
	int totalPacketsSent;
	int totalPacketsReceived;
	int totalPacketsRelayed;
	long totalSumValuesSent;
	long totalSumValuesReceived;
	
	public StatisticsCollectorAndDisplay(int numNodes) {
		this.infoTuples = new ArrayList<InfoTuple>(numNodes);
		this.totalNumNodes = numNodes;
	}
	
	public void addStatistics(OverlayNodeReportsTrafficSummary event) {
		infoTuples.add(new InfoTuple(event.getNodeID(), event.getNumPacketsSent(), event.getNumPacketsReceived(), event.getNumPacketsRelayed(), event.getSumPacketsSent(), event.getSumPacketsReceived()));
		if(infoTuples.size() == totalNumNodes) {
			System.out.println("     Packets | Packets  | Packets | Sum Values    | Sum Values");
			System.out.println("     Sent    | Received | Relayed | Sent          | Received");
			System.out.println("--------------------------------------------------------------");
			for(InfoTuple o : infoTuples) {
				System.out.format(" %3s|%8s|%10s|%9s|%15s|%15s%n", o.nodeID, o.packetsSent, o.packetsReceived, o.packetsRelayed, o.sumPacketsSent, o.sumPacketsReceived);
				System.out.println("--------------------------------------------------------------");
				totalPacketsSent += o.packetsSent;
				totalPacketsReceived += o.packetsReceived;
				totalPacketsRelayed += o.packetsRelayed;
				totalSumValuesSent += o.sumPacketsSent;
				totalSumValuesReceived += o.sumPacketsReceived;	
			}
			System.out.format("Sum |%8s|%10s|%9s|%15s|%15s%n", totalPacketsSent, totalPacketsReceived, totalPacketsRelayed, totalSumValuesSent, totalSumValuesReceived);
			
			
		}
	}
	
	public void clear() {
		infoTuples.clear();
		totalPacketsSent = 0;
		totalPacketsReceived = 0;
		totalPacketsRelayed = 0;
		totalSumValuesSent = 0L;
		totalSumValuesReceived = 0L;
	}
	
	
	private class InfoTuple {
		public InfoTuple(int nodeID, int packetsSent, int packetsReceived, int packetsRelayed, long sumPacketsSent, long sumPacketsReceived) {
			this.nodeID = nodeID;
			this.packetsSent = packetsSent;
			this.packetsReceived = packetsReceived;
			this.packetsRelayed = packetsRelayed;
			this.sumPacketsSent = sumPacketsSent;
			this.sumPacketsReceived = sumPacketsReceived;
		}
		public int nodeID;
		public int packetsSent;
		public int packetsReceived;
		public int packetsRelayed;
		public long sumPacketsSent;
		public long sumPacketsReceived;
	}	
	
}

