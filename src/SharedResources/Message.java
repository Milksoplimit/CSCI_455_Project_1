package SharedResources;

import java.util.ArrayList;

public class Message {
	
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
