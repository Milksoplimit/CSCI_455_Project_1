package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import SharedResources.*;

public class Server {

	private static DataService dataService = new DataService();
	private static LoggingService log = new LoggingService("Server");
	
	public static void main(String[] args) throws Exception{
		do {
			dataService.loadData();
		} while (!dataService.loadData());
		
		ExecutorService executor = Executors.newCachedThreadPool();
		ServerSocket socket = new ServerSocket(6789);
		System.out.println("Running Server");
		
		while(true) {
			Socket connectionSocket = socket.accept();
			Message seed = new Message(dataService.getInMemoryData(), Actions.GET_ALL_EVENTS);
			ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
			out.writeObject(seed);
			log.log("RequestType: GET_ALL_EVENTS Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
			Thread thread = new Thread(new ClientHandler(in, out, connectionSocket, dataService, log));
			executor.execute(thread);
		}

	}
	
}

class ClientHandler implements Runnable {
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	private Socket connectionSocket;
	private DataService dataService;
	private LoggingService log;
	
	public ClientHandler(ObjectInputStream in, ObjectOutputStream out, Socket connection, DataService data, LoggingService log) {
		inStream = in;
		outStream = out;
		connectionSocket = connection;
		dataService = data;
		this.log = log;
	}
	
	@Override
	public void run() {
		
		Actions actionType;
		ArrayList<Event> events;
		
		while(true) {
			Message clientMessage = null;
			while(clientMessage == null) {
				try {
					clientMessage = (Message) inStream.readObject();
				} catch (Exception e) {
					
				}
			}
			actionType = clientMessage.getAction();
			events = clientMessage.getItems();
			
			switch (actionType) {
			case ADD_EVENT:
				if (events.size() != 1) break;
				dataService.addEvent(events.get(0));
				log.log("RequestType: ADD_EVENT Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
				break;
				
			case DELETE_EVENT:
				if (events.size() != 1) break;
				dataService.deleteEvent(events.get(0));
				log.log("RequestType: DELETE_EVENT Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
				break;
				
			case DONATE:
				if (events.size() != 1) break;
				if (!(events.get(0) instanceof CurrentEvent)) break;
				CurrentEvent e = (CurrentEvent) events.get(0);
				dataService.changeEvent(e);
				log.log("RequestType: DONATE Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
				break;
				
			case GET_ALL_EVENTS:
				try {
					outStream.writeObject(new Message(dataService.getInMemoryData(), Actions.GET_ALL_EVENTS));
					log.log("RequestType: GET_ALL_EVENTS Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case GET_CURRENT_EVENTS:
				try {
					outStream.writeObject(new Message(dataService.getCurrentEvents(), Actions.GET_CURRENT_EVENTS));
					log.log("RequestType: GET_CURRENT_EVENTS Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case GET_OLD_EVENTS:
				try {
					outStream.writeObject(new Message(dataService.getInMemoryData(), Actions.GET_OLD_EVENTS));
					log.log("RequestType: GET_OLD_EVENTS Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case MARK_COMPLETED:
				if (events.size() != 1) break;
				Event event = events.get(0);
				dataService.changeEvent(event);
				log.log("RequestType: MARK_COMPLETED Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
				break;
				
			case TERMINATE_CONNECTION:
				log.log("RequestType: TERMINATE_CONNECTION Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
				try {
					connectionSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
				
			default:
				break;
			}
		}
	}
}