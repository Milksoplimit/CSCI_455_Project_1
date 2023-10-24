package Client;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import SharedResources.*;

public class Client {

	public static void main(String[] args) throws Exception {
		
		Scanner input = new Scanner(System.in);
		char selection = 0;
		int index = -1;
		
		do {
			System.out.println("Start Client? [Y/N]");
			selection = input.nextLine().toLowerCase().charAt(0);
			if (selection == 'n') System.exit(0);
		} while (selection != 'y');
		
		System.out.println("Server IP (Blank for localhost):");
		String ip = input.nextLine().trim();
		if (ip.isEmpty()) ip = "localhost";
		Socket clientSocket = new Socket(ip, 6789);
		
		ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

		Message messageFromServer = (Message) in.readObject();
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
				out.writeObject(new Message(null, Actions.TERMINATE_CONNECTION));
				exit = true;
				break;
				
			case 'd':
				for(int i = 0; i < events.size(); i++) {
					System.out.println("Index: " + (i+1) + "\n" + events.get(i).display());
				}
				break;
				
			case 'c':
				out.writeObject(new Message(null, Actions.GET_CURRENT_EVENTS));
				messageFromServer = (Message) in.readObject();
				events = messageFromServer.getItems();
				Event tempC;
				for(int i = 0; i < events.size(); i++) {
					for(int j = 0; j < events.size()-i; j++) {
						if(events.get(j).getDeadline().after(events.get(j+1).getDeadline())) {
							tempC = events.get(j);
							events.set(j, events.get(j+1));
							events.set(j+1, tempC);
						}
					}
				}
				for(int i = 0; i < events.size(); i++) {
					System.out.println("Index: " + (i+1) + "\n" + events.get(i).display());
				}
				break;
				
			case 'o':
				out.writeObject(new Message(null, Actions.GET_OLD_EVENTS));
				messageFromServer = (Message) in.readObject();
				events = messageFromServer.getItems();
				Event tempO;
				for(int i = 0; i < events.size(); i++) {
					for(int j = 0; j < events.size()-i; j++) {
						if(events.get(j).getDeadline().after(events.get(j+1).getDeadline())) {
							tempO = events.get(j);
							events.set(j, events.get(j+1));
							events.set(j+1, tempO);
						}
					}
				}
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
					out.writeObject(new Message(messageItems, Actions.ADD_EVENT));
				}
				break;
				
				//TODO Add Cases for Deleting Events and Donating
				
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
			Date deadline = null;
			
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
				System.out.print("Event Deadline: ");
				DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy");
				try{
					LocalDate tempDate = LocalDate.parse(input.nextLine(), format);
					deadline = new Date(tempDate.getYear(), tempDate.getMonthValue(), tempDate.getDayOfMonth());
					if(deadline.after(new Date())) valid = true;
				} catch(Exception ex) {}
			}
			
			return new CurrentEvent(name, goal, donations, deadline);
		
		case 'd':
			return new CurrentEvent("Default Name", 1000, 0, new Date(System.currentTimeMillis()));
			
		case 'e':
			return null;
			
		default:
			return null;
		}
	}

}
