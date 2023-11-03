package SharedResources;

import java.time.LocalDate;

// This class represents an ongoing event that has not expired or met its funding goal
public class CurrentEvent extends Event {

	public CurrentEvent() {
		super();
		super.deadline = LocalDate.ofEpochDay(Long.MAX_VALUE);
		super.status = EventStatus.CURRENT;
	}
	
	public CurrentEvent(String name, double goal, double donations, LocalDate deadline) {
		super();
		super.name = name;
		super.goal = goal;
		super.donations = donations;
		super.deadline = deadline;
		super.status = EventStatus.CURRENT;
	}
	
	// This method allows for the donations to be updated and does nothing if the value entered is negative
	public void donate(double donation) {
		lock.lock();
		try {
			if (donation < 0) donation = 0;
			donations += donation;
		} finally {
			lock.unlock();
		}
	}

}
