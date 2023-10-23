package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import SharedResources.*;

public class DataService {

	private String path;
	private ArrayList<Event> inMemoryData;
	private static LoggingService log = new LoggingService("DataService");
	
	private static Lock lock = new ReentrantLock();
	private static Condition modification = lock.newCondition();
	
	public DataService() {
		this.path = "./EventFile.ser"; 
		this.inMemoryData = new ArrayList<Event>();
	}
	
	public boolean loadData() throws Exception {
		try {
			File f = new File(path);
			FileInputStream inFile = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(inFile);
			inMemoryData = (ArrayList<Event>) in.readObject();
			in.close();
			log.log("Data Loaded from File");
			return true;
		} catch (FileNotFoundException e) {
			log.log(e.getMessage());
			initialize();
			return false;
		} catch (ClassNotFoundException e) {
			log.log(e.getMessage());
			throw new Exception("Fatal Error");
		}catch (IOException e) {
			log.log(e.getMessage());
			initialize();
			return false;
		}
		
	}
	
	public ArrayList<Event> getInMemoryData(){
		return inMemoryData;
	}
	
	public ArrayList<Event> getCurrentEvents(){
		ArrayList<Event> e = new ArrayList<Event>();
		for (Event elem : inMemoryData) {
			if(elem.getStatus() == EventStatus.CURRENT) {
				e.add(elem);
			}
		}
		log.log("Current Events Filtered and Returned");
		return e;
	}
	
	public ArrayList<Event> getCompletedEvents(){
		ArrayList<Event> e = new ArrayList<Event>();
		for (Event elem : inMemoryData) {
			if(elem.getStatus() == EventStatus.COMPLETED) {
				e.add(elem);
			}
		}
		log.log("Completed Events Filtered and Returned");
		return e;
	}
	
	public void deleteEvent(Event e) {
		lock.lock();
		try {
			modification.await();
			inMemoryData.remove(e);
			e.delete();
			modification.signal();
			log.log("Event Deleted");
		} catch (InterruptedException ex) {
			log.log(ex.getMessage());
		} finally {
			lock.unlock();
		}	
	}
	
	// This is meant to only be called periodically and on close of server to save changes to the data
	public void saveChanges() {
		lock.lock();
		try {
			modification.await();
			FileOutputStream outFile = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(outFile);
			out.writeObject(inMemoryData);
			out.close();
			modification.signal();
			log.log("Changes Saved");
		} catch (FileNotFoundException e) {
			log.log(e.getMessage());
		} catch (IOException e) {
			log.log(e.getMessage());
		} catch (InterruptedException e) {
			log.log(e.getMessage());
		} finally {
			lock.unlock();
		}	
	}
	
	public void changeEvent(Event e) {
		lock.lock();
		try {
			modification.await();
			for (Event elem : inMemoryData) {
				if (elem.equals(e)) {
					inMemoryData.remove(elem);
					inMemoryData.add(e);
				}
			}
			modification.signal();
			log.log("Event Modified");
		} catch (InterruptedException ex) {
			log.log(ex.getMessage());
		} finally {
			lock.unlock();
		}
	}
	
	public void addEvent(Event e) {
		lock.lock();
		try {
			modification.await(); //TODO change how we are signaling so we don't get stuck eternally waiting
			boolean exists = false;
			for(Event elem : inMemoryData) {
				if (elem.equals(e)) exists = true;
			}
			if (!exists) inMemoryData.add(e);
			modification.signal();
			log.log("Event Added");
		} catch (InterruptedException ex) {
			log.log(ex.getMessage());
		} finally {
			lock.unlock();
		}
	}
	
	private void initialize() {
		try {
			FileOutputStream outFile = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(outFile);
			ArrayList<Event> e = new ArrayList<Event>();
			for(int i = 0; i < 20; i++) {
				e.add(generateEvent());
			}
			out.writeObject(e);
			out.close();
			log.log("Initialization of Events Completed");
		} catch (FileNotFoundException e) {
			log.log(e.getMessage());
		} catch (IOException e) {
			log.log(e.getMessage());
		}	
	}
	
	private Event generateEvent() {
		String[] firstPartNames = {"Super","Amazing","Wonderful","Connected","Distributed","Techno","Active"};
		String[] secondPartNames = {"Bouncer","Charity","Sport","Crafts","Development","Events","Bonds"};
		Date[] deadlines = {new Date(2023,1,1), new Date(2023,9,9), new Date(), new Date(2024,1,1), new Date(2024,5,5)};
		Random rand = new Random();
		double goal = rand.nextDouble() * rand.nextInt(20000);
		boolean flag = rand.nextBoolean();
		if(flag) {
			CurrentEvent c = new CurrentEvent(firstPartNames[rand.nextInt(firstPartNames.length)] + " " + secondPartNames[rand.nextInt(secondPartNames.length)],
					goal,
					goal / rand.nextInt(100),
					deadlines[rand.nextInt(3, deadlines.length)]);
			return c;
		}
		else {
			CompletedEvent c = new CompletedEvent(firstPartNames[rand.nextInt(firstPartNames.length)] + " " + secondPartNames[rand.nextInt(secondPartNames.length)],
					goal,
					goal / rand.nextInt(1,101),
					deadlines[rand.nextInt(3)]);
			return c;
		}
	}

}
