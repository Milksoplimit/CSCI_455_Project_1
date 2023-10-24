package Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import SharedResources.*;

public class Client {

	public static void main(String[] args) throws Exception {
		
		Socket clientSocket = new Socket("localhost", 6789);
		
		ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

		Message messageFromServer = (Message) in.readObject();
		ArrayList<Event> events = messageFromServer.getItems();
		
		
		
		clientSocket.close();
		
	}

}
