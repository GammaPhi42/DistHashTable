package cs455.overlay.wireformats;

public class Protocol {
	public static final byte        OVERLAY_NODE_SENDS_REGISTRATION = (byte) 2;
	public static final byte REGISTRY_REPORTS_REGISTRATION_STATUS = (byte) 3;
	public static final byte      OVERLAY_NODE_SENDS_DEREGISTRATION = (byte) 4;
	public static final byte REGISTRY_REPORTS_DEREGISTRATION_STATUS = (byte) 5;
	public static final byte           REGISTRY_SENDS_NODE_MANIFEST = (byte) 6;
	public static final byte      NODE_REPORTS_OVERLAY_SETUP_STATUS = (byte) 7;
	public static final byte        REGISTRY_REQUESTS_TASK_INITIATE = (byte) 8;
	public static final byte                OVERLAY_NODE_SENDS_DATA = (byte) 9;
	public static final byte     OVERLAY_NODE_REPORTS_TASK_FINISHED = (byte) 10;
	public static final byte      REGISTRY_REQUESTS_TRAFFIC_SUMMARY = (byte) 11;
	public static final byte   OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = (byte) 12;
	public static String getType(int messageType) {
		switch(messageType) {
		case 2:
			return "OVERLAY_NODE_SENDS_REGISTRATION";
		case 3:
			return "REGISTRY_REPORTS_REGISTRATION_STATUS";
		case 4:
			return "OVERLAY_NODE_SENDS_DEREGISTRATION";
		case 5:
			return "REGISTRY_REPORTS_DEREGISTRATION_STATUS";
		case 6:
			return "REGISTRY_SENDS_NODE_MANIFEST";
		case 7:
			return "NODE_REPORTS_OVERLAY_SETUP_STATUS";
		case 8:
			return "REGISTRY_REQUESTS_TASK_INITIATE";
		case 9:
			return "OVERLAY_NODE_SENDS_DATA";
		case 10:
			return "OVERLAY_NODE_REPORTS_TASK_FINISHED";
		case 11:
			return "REGISTRY_REQUESTS_TRAFFIC_SUMMARY";
		case 12: 
			return "OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY";
		default:
			return "No such message type";
		}
	}
}
