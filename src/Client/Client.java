package Client;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import SharedResources.*;

public class Client {

	public static void main(String[] args) throws Exception {
		
		Scanner input = new Scanner(System.in);
		char selection = 0;
		int index = -1;
		double ammount = -1.0;
		
		do {
			System.out.println("Start Client? [Y/N]");
			selection = input.nextLine().toLowerCase().charAt(0);
			if (selection == 'n') System.exit(0);
		} while (selection != 'y');
		
		System.out.println("Server IP (Blank for localhost):");
		String ip = input.nextLine().trim();
		if (ip.isEmpty()) ip = "localhost";
		InetAddress ipAddress = InetAddress.getByName(ip);
		
		DatagramSocket clientSocket = new DatagramSocket();
		
		sendMessage(new Message(null, Actions.GET_ALL_EVENTS), clientSocket, ipAddress);
		
		Message messageFromServer = recieveMessage(clientSocket);
		
		ArrayList<Event> events = messageFromServer.getItems();

		while(true) {
			
			boolean exit = false;
			
			System.out.println("[D]isplay All Events\n"
					+ "[E]xit\n"
					+ "Display [C]urrent Events\n"
					+ "Display C[o]mpleted Events\n"
					+ "[A]dd New Event\n"
					+ "Dele[t]e Event\n"
					+ "Do[n]ate");
			
			selection = input.nextLine().trim().toLowerCase().charAt(0);
			
			switch (selection) {
			case 'e':
				sendMessage(new Message(null, Actions.TERMINATE_CONNECTION), clientSocket, ipAddress);
				exit = true;
				break;
				
			case 'd':
				organizeEvents(events);
				for(int i = 0; i < events.size(); i++) {
					System.out.println("Index: " + (i+1) + "\n" + events.get(i).display());
				}
				break;
				
			case 'c':
				sendMessage(new Message(null, Actions.GET_CURRENT_EVENTS), clientSocket, ipAddress);
				messageFromServer = recieveMessage(clientSocket);
				events = messageFromServer.getItems();
				inPlaceSort(events);
				for(int i = 0; i < events.size(); i++) {
					System.out.println("Index: " + (i+1) + "\n" + events.get(i).display());
				}
				break;
				
			case 'o':
				sendMessage(new Message(null, Actions.GET_OLD_EVENTS), clientSocket, ipAddress);
				messageFromServer = recieveMessage(clientSocket);
				events = messageFromServer.getItems();
				inPlaceSort(events);
				for(int i = 0; i < events.size(); i++) {
					System.out.println("Index: " + (i+1) + "\n" + events.get(i).display());
				}
				break;
			
			case 'a':
				Event toAdd = promptForEvent(input);
				if(toAdd != null) {
					events.add(toAdd);
					ArrayList<Event> messageItems = new ArrayList<Event>();
					messageItems.add(toAdd);
					sendMessage(new Message(messageItems, Actions.ADD_EVENT), clientSocket, ipAddress);
				}
				break;
				
			case 't':
				try {
					System.out.print("Index of Event to Delete: ");
					index = Integer.parseInt(input.nextLine());
					if (index < 1 || index > events.size()) throw new Exception();
				} catch (Exception ex) {
					System.out.println("Invalid Index. Please try again.");
					break;
				}
				Event toDelete = events.get(index - 1);
				ArrayList<Event> deleteItems = new ArrayList<Event>();
				deleteItems.add(toDelete);
				sendMessage(new Message(deleteItems, Actions.DELETE_EVENT), clientSocket, ipAddress);
				events.remove(toDelete);
				toDelete.delete();
				toDelete = null;
				index = -1;
				break;
				
			case 'n':
				sendMessage(new Message(null, Actions.GET_CURRENT_EVENTS), clientSocket, ipAddress);
				messageFromServer = recieveMessage(clientSocket);
				events = messageFromServer.getItems();
				inPlaceSort(events);
				for(int i = 0; i < events.size(); i++) {
					System.out.println("Index: " + (i+1) + "\n" + events.get(i).display());
				}
				
				boolean valid = false;
				while(!valid) {
					System.out.print("Index of Event to Donate to: ");
					try {
						index = Integer.parseInt(input.nextLine());
						if (index < 1 || index > events.size()) throw new Exception();
						valid = true;
					} catch (Exception ex) {
						System.out.println("Invalid Index");
						valid = false;
						index = -1;
					}
				}
				
				CurrentEvent toDonateTo = (CurrentEvent) events.get(index - 1);
				
				valid = false;
				index = -1;
				
				while(!valid) {
					System.out.print("Ammount to Donate: ");
					try {
						ammount = Double.parseDouble(input.nextLine());
						if (ammount < 0) throw new Exception();
						valid = true;
					} catch (Exception ex) {
						System.out.println("Invalid Donation");
						valid = false;
						ammount = -1.0;
					}
				}
				
				toDonateTo.donate(ammount);
				ammount = -1.0;
				
				ArrayList<Event> messageItems = new ArrayList<Event>();
				messageItems.add(toDonateTo);
				sendMessage(new Message(messageItems, Actions.DONATE), clientSocket, ipAddress);
				
				break;
								
			default:
				break;
			}
			
			if(exit) break;
		} 
		
		clientSocket.close();
		input.close();
	}
	
	// Private helper method for taking input for a new event
	private static Event promptForEvent(Scanner input) {
		char choice;
		System.out.println("Generate [D]efault Event\n"
				+ "Generate [C]ustom Event\n"
				+ "[E]xit");
		
		choice = input.nextLine().trim().toLowerCase().charAt(0);
		
		switch (choice) {
		case 'c':
			String name;
			double goal = -1;
			double donations = 0;
			LocalDate deadline = null;
			
			System.out.print("Event Name: ");
			name = input.nextLine().trim();
			
			boolean valid = false;
			while (!valid) {
				System.out.print("Event Goal: ");
				try {
					goal = Double.parseDouble(input.nextLine());
					if (goal > 0) valid = true;
				} catch (Exception ex) {}
			}
			
			valid = false;
			while(!valid) {
				System.out.print("Event Deadline (MM-DD-YYYY): ");
				DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy");
				try{
					deadline = LocalDate.parse(input.nextLine(), format);
					if(deadline.isAfter(LocalDate.now())) valid = true;
				} catch(Exception ex) {}
			}
			
			return new CurrentEvent(name, goal, donations, deadline);
		
		case 'd':
			return new CurrentEvent("Default Name", 1000, 0, LocalDate.now());
			
		default:
			return null;
		}
	}
	
	// Sorting method that uses bubble sort to sort events based on their deadline
	private static void inPlaceSort(ArrayList<Event> events) {
		Event temp;
		for(int i = 0; i < events.size(); i++) {
			for(int j = 0; j < events.size()-i-1; j++) {
				if(events.get(j).getDeadline().isAfter(events.get(j+1).getDeadline())) {
					temp = events.get(j);
					events.set(j, events.get(j+1));
					events.set(j+1, temp);
				}
			}
		}
	}
	
	// Method that breaks events into categories and sorts them in order by category
	private static void organizeEvents(ArrayList<Event> events) {
		ArrayList<Event> oldEvents = new ArrayList<Event>();
		ArrayList<Event> currentEvents = new ArrayList<Event>();
		for(Event elem : events) if(elem.getStatus() == EventStatus.COMPLETED) oldEvents.add(elem); else currentEvents.add(elem);
		inPlaceSort(oldEvents);
		inPlaceSort(currentEvents);
		events = oldEvents;
		events.addAll(currentEvents);
	}

	// Method for receiving a message from the server
	private static Message recieveMessage(DatagramSocket ds) throws Exception{
		byte[] byteIn = new byte[1024 * 64];
		DatagramPacket recieve = new DatagramPacket(byteIn, byteIn.length);
		ds.receive(recieve);
		ByteArrayInputStream bis = new ByteArrayInputStream(recieve.getData());
		ObjectInputStream in = new ObjectInputStream(bis);
		return (Message) in.readObject();
	}
	
	// Method for sending a message to the server
	private static void sendMessage(Message toSend, DatagramSocket ds, InetAddress ip) throws Exception{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(toSend);
		byte[] data = byteOut.toByteArray();
		DatagramPacket send = new DatagramPacket(data, data.length, ip, 6789);
		ds.send(send);
	}
}
