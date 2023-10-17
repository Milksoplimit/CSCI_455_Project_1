package SharedResources;

import java.util.Date;

public class CurrentEvent extends Event {

	public CurrentEvent() {
		super();
		super.deadline = new Date(Long.MAX_VALUE);
		super.status = EventStatus.CURRENT;
	}


}
