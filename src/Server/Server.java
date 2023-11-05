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
		
		while (!dataService.loadData());
		
		DatagramSocket clientSocket = new DatagramSocket(6789);
		int count = 0;
		System.out.println("Running Server");
		
		while(true) {
			byte[] byteIn = new byte[1024 * 64];
			DatagramPacket recieve = new DatagramPacket(byteIn, byteIn.length);
			clientSocket.receive(recieve);
			Thread thread = new Thread(new UDPClientHandler(recieve, dataService, log));
			thread.start();
			
			count++;
			if(count == 5) {
				count = 0;
				dataService.saveChanges();
				log.log("AUTO SAVE");
			}
		}

	}
	
}

class UDPClientHandler implements Runnable{
	private DatagramPacket packetIn;
	private ByteArrayInputStream bis;
	private ObjectInputStream ois;
	private byte[] byteIn;
	private DataService dataService;
	private LoggingService log;
	private DatagramSocket socketOut;
	private ByteArrayOutputStream bos;
	private ObjectOutputStream oos;
	
	public UDPClientHandler(DatagramPacket pIn, DataService dat, LoggingService ls) throws IOException {
		packetIn = pIn;
		byteIn = pIn.getData();
		bis = new ByteArrayInputStream(byteIn);
		ois = new ObjectInputStream(bis);
		dataService = dat;
		log = ls;
		socketOut = new DatagramSocket();
		bos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(bos);
	}
	
	
	@Override
	public void run() {
		
		Actions actionType;
		ArrayList<Event> events;
		
			Message clientMessage = null;
			while(clientMessage == null) {
				try {
					clientMessage = (Message) ois.readObject();
				} catch (Exception e) {
					
				}
			}
			actionType = clientMessage.getAction();
			events = clientMessage.getItems();
			
			switch (actionType) {
			case ADD_EVENT:
				if (events.size() != 1) break;
				dataService.addEvent(events.get(0));
				log.log("RequestType: ADD_EVENT Origin: IP[" + packetIn.getAddress().toString() + "] PORT[" + packetIn.getPort() + "]");
				break;
				
			case DELETE_EVENT:
				if (events.size() != 1) break;
				dataService.deleteEvent(events.get(0));
				log.log("RequestType: DELETE_EVENT Origin: IP[" + packetIn.getAddress().toString() + "] PORT[" + packetIn.getPort() + "]");
				break;
				
			case DONATE:
				if (events.size() != 1) break;
				if (!(events.get(0) instanceof CurrentEvent)) break;
				CurrentEvent e = (CurrentEvent) events.get(0);
				dataService.donateToEvent(e);
				log.log("RequestType: DONATE Origin: IP[" + packetIn.getAddress().toString() + "] PORT[" + packetIn.getPort() + "]");
				break;
				
			case GET_ALL_EVENTS:
				try {
					oos.writeObject(new Message(dataService.getInMemoryData(), Actions.GET_ALL_EVENTS));
					sendPacket();
					log.log("RequestType: GET_ALL_EVENTS Origin: IP[" + packetIn.getAddress().toString() + "] PORT[" + packetIn.getPort() + "]");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case GET_CURRENT_EVENTS:
				try {
					oos.writeObject(new Message(dataService.getCurrentEvents(), Actions.GET_CURRENT_EVENTS));
					sendPacket();
					log.log("RequestType: GET_CURRENT_EVENTS Origin: IP[" + packetIn.getAddress().toString() + "] PORT[" + packetIn.getPort() + "]");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case GET_OLD_EVENTS:
				try {
					oos.writeObject(new Message(dataService.getCompletedEvents(), Actions.GET_OLD_EVENTS));
					sendPacket();
					log.log("RequestType: GET_OLD_EVENTS Origin: IP[" + packetIn.getAddress().toString() + "] PORT[" + packetIn.getPort() + "]");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				
			case MARK_COMPLETED:
				if (events.size() != 1) break;
				Event event = events.get(0);
				dataService.changeEvent(event);
				log.log("RequestType: MARK_COMPLETED Origin: IP[" + packetIn.getAddress().toString() + "] PORT[" + packetIn.getPort() + "]");
				break;
				
			case TERMINATE_CONNECTION:
				log.log("RequestType: TERMINATE_CONNECTION Origin: IP[" + packetIn.getAddress().toString() + "] PORT[" + packetIn.getPort() + "]");
				dataService.saveChanges();
				return;
				
			default:
				break;
			}
			
	}
	
	// Method to send a packet back to the client
	private void sendPacket() throws IOException {
		byte[] data = bos.toByteArray();
		DatagramPacket send = new DatagramPacket(data, data.length, packetIn.getSocketAddress());
		socketOut.send(send);
	}
}