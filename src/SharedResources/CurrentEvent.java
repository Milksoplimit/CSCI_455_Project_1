package SharedResources;

import java.util.Date;

// This class represents an ongoing event that has not expired or met its funding goal
public class CurrentEvent extends Event {

	public CurrentEvent() {
		super();
		super.deadline = new Date(Long.MAX_VALUE);
		super.status = EventStatus.CURRENT;
	}
	
	public CurrentEvent(String name, double goal, double donations, Date deadline) {
		super();
		super.name = name;
		super.goal = goal;
		super.donations = donations;
		super.deadline = deadline;
		super.status = EventStatus.CURRENT;
	}
	
	// This method allows for the donations to be updated and does nothing of the value entered is negative
	public void donate(double donation) {
		if (donation < 0) donation = 0;
		donations += donation;
	}

}
