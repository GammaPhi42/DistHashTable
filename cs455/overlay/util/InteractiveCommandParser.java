package cs455.overlay.util;

import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;

public class InteractiveCommandParser implements Runnable {

	private Registry registry;
	private MessagingNode messagingNode;
	private volatile LinkedBlockingQueue<String> commandQueue;
	
	/**
	 * 
	 * @param node The node for which to parse commands
	 * @param commandQueue The queue of commands inputted at command line
	 */
	public InteractiveCommandParser(Node node, LinkedBlockingQueue<String> commandQueue) {
		if(node instanceof Registry) {
			this.registry = (Registry) node;
		}
		else if(node instanceof MessagingNode) {
			this.messagingNode = (MessagingNode) node;
		}
		else System.out.println("InteractiveCommandParser received a Node that was not a Registry nor a MessagingNode!");
		
		this.commandQueue = commandQueue;
	}

	@Override
	public void run() {
		if(registry != null && messagingNode == null) {
			acceptRegistryCommands();
		}
		else if(registry == null && messagingNode != null) {
			acceptMessagingNodeCommands();
		}
		else System.out.println("InteractiveCommandParser: Both registry and messagingNode were instantiated. This should not happen!");
		
	}


	private void acceptMessagingNodeCommands() {
		Scanner scanner = new Scanner(System.in);
		boolean acceptingCommands = true;
		String parsableCommand;
		
		System.out.println("You may now enter commands for the MessagingNode");
		
		while(acceptingCommands) {
			parsableCommand = scanner.next();
			if(parsableCommand.equals("print-counters-and-diagnostics")) {
				commandQueue.add(parsableCommand);
			}
			else if(parsableCommand.equals("exit-overlay")) {
				commandQueue.add(parsableCommand);
			} else if(parsableCommand.equals("quit")) {
				acceptingCommands = false;
			}
		}
		
		scanner.close();

	}

	private void acceptRegistryCommands() {
		Scanner scanner = new Scanner(System.in);
		boolean acceptingCommands = true;
		String parsableCommand;
		
		System.out.println("You may now enter commands for the Registry");
		
		while(acceptingCommands) {
			parsableCommand = scanner.next();
			if(parsableCommand.equals("list-messaging-nodes")
			|| parsableCommand.equals("list-routing-tables")
			|| parsableCommand.equals("list-routing-tables")) {
				commandQueue.add(parsableCommand);
			}
			else if(parsableCommand.equals("setup-overlay")
			|| 		parsableCommand.equals("start")) {
				if(scanner.hasNext()) {
					String arg = scanner.next();
					commandQueue.add(parsableCommand);
					commandQueue.add(arg);
				}
			}
			else if(parsableCommand.equals("start")) {
				if(scanner.hasNext()) {
					String numPacketsToSend = scanner.next();
					commandQueue.add(parsableCommand);
					commandQueue.add(numPacketsToSend);
				}
				else
					System.out.println("Please enter an argument for the number of packets to send!");
			}
			else if(parsableCommand.equals("quit")) {
				acceptingCommands = false;
			}
			else {
				System.out.println("You did not enter a valid command. Try again.");
			}
			
		}
		scanner.close();

	}

}
