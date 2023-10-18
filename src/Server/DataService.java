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
	
	private static Lock lock = new ReentrantLock();
	private static Condition modification = lock.newCondition();
	
	public DataService() {
		this.path = "./EventFile.ser"; 
		this.inMemoryData = new ArrayList<Event>();
	}
	
	public boolean loadData() {
		File f = new File(path);
		try {
			FileInputStream inFile = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(inFile);
			inMemoryData = (ArrayList<Event>) in.readObject();
			in.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			initialize();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}catch (IOException e) {
			e.printStackTrace();
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
		return e;
	}
	
	public ArrayList<Event> getCompletedEvents(){
		ArrayList<Event> e = new ArrayList<Event>();
		for (Event elem : inMemoryData) {
			if(elem.getStatus() == EventStatus.COMPLETED) {
				e.add(elem);
			}
		}
		return e;
	}
	
	public void deleteEvent(Event e) {
		lock.lock();
		try {
			modification.wait();
			inMemoryData.remove(e);
			e.delete();
			modification.notify();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
