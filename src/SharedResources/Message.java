package SharedResources;

import java.io.Serializable;
import java.util.ArrayList;

// Class representing the contents of a message between client and server
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Actions action;
	private ArrayList<Event> items;
	
	public Message() {
		items = new ArrayList<Event>();
		action = Actions.GET_ALL_EVENTS;
	}
	
	public Message(ArrayList<Event> items, Actions action) {
		this.items = items;
		this.action = action;
	}
	
	public ArrayList<Event> getItems(){
		return items;
	}

	public Actions getAction() {
		return action;
	}
	
}
