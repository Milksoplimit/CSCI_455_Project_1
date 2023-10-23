package Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import SharedResources.*;

public class Client {

	public static void main(String[] args) throws Exception {
		
		String wait = new Scanner(System.in).nextLine();
		
		Socket clientSocket = new Socket("localhost", 6789);
		
		ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

		Message messageFromServer = (Message) in.readObject();
		ArrayList<Event> events = messageFromServer.getItems();
		
		
		for(Event e : events) {
			System.out.println(e.display());
		}
		
		events = new ArrayList<Event>();
		events.add(new CompletedEvent());
		
		for(Event e : events) {
			System.out.println(e.display());
		}
		
		out.writeObject(new Message(events, Actions.ADD_EVENT));
		
		clientSocket.close();
		
		wait = new Scanner(System.in).nextLine();
	}

}
