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
	
	public static void main(String[] args) {
		do {
			dataService.loadData();
		} while (!dataService.loadData());
		
		ExecutorService executor = Executors.newCachedThreadPool();
		ServerSocket socket = new ServerSocket(6789);
		System.out.println("Running Server");
		
		while(true) {
			Socket connectionSocket = socket.accept();
			
		}

	}
	
	public static class SendMessageTask implements Runnable{

		private Message msg;
		private ObjectOutputStream out;
		
		SendMessageTask(Message msg, ObjectOutputStream out){
			this.msg = msg;
			this.out = out;
		}
		
		@Override
		public void run() {
			try {
				out.writeObject(msg);
			} catch (IOException ex) {
				log.log(ex.getMessage());
			}
		}
	}
	
	class ClientHandler extends Thread {
		final ObjectInputStream inStream;
		final ObjectOutputStream outStream;
		final Socket connectionSocket;
		
		public ClientHandler(ObjectInputStream in, ObjectOutputStream out, Socket connection) {
			inStream = in;
			outStream = out;
			connectionSocket = connection;
		}
		
		@Override
		public void run() {
			
			Actions actionType;
			ArrayList<Event> events;
			
			while(true) {
				Message clientMessage = (Message) inStream.readObject();
				actionType = clientMessage.getAction();
				events = clientMessage.getItems();
				
				switch (actionType) {
				case ADD_EVENT:
					if (events.size() == 0) break;
					dataService.addEvent(events.get(0));
					log.log("RequestType: ADD_EVENT Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
					break;
				case DELETE_EVENT:
					if (events.size() == 0) break;
					dataService.deleteEvent(events.get(0));
					log.log("RequestType: DELETE_EVENT Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
					break;
				case DONATE:
					if (events.size() == 0) break;
					if (!(events.get(0) instanceof CurrentEvent)) break;
					CurrentEvent e = (CurrentEvent) events.get(0);
					dataService.changeEvent(e);
					log.log("RequestType: DONATE Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
					break;
				case GET_ALL_EVENTS:
					outStream.writeObject(new Message(dataService.getInMemoryData(), Actions.GET_ALL_EVENTS));
					log.log("RequestType: GET_ALL_EVENTS Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
					break;
				case GET_CURRENT_EVENTS:
					outStream.writeObject(new Message(dataService.getCurrentEvents(), Actions.GET_CURRENT_EVENTS));
					log.log("RequestType: GET_CURRENT_EVENTS Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
					break;
				case GET_OLD_EVENTS:
					outStream.writeObject(new Message(dataService.getInMemoryData(), Actions.GET_OLD_EVENTS));
					log.log("RequestType: GET_OLD_EVENTS Origin: IP[" + connectionSocket.getInetAddress() + "] PORT[" + connectionSocket.getPort() + "]");
					break;
				case MARK_COMPLETED:
					if (events.size() == 0) break;
					// TODO Finish Case
					break;
				case TERMINATE_CONNECTION:
					//TODO Finish Case
					break;
				default:
					break;
				}
			}
		}
		
		
	}
	
	
}
