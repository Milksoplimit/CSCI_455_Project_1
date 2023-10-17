package SharedResources;

import java.util.ArrayList;

public class Message {
	private ArrayList<Event> items;
	
	public Message() {
		items = new ArrayList<Event>();
	}
	
	public Message(ArrayList<Event> items) {
		this.items = items;
	}
	
	public ArrayList<Event> getItems(){
		return items;
	}
	
}
