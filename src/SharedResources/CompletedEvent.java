package SharedResources;

import java.time.LocalDate;

// This class represents events that have either expired or have met their goal
// In either case the event is no longer active and cannot receive any more funding
public class CompletedEvent extends Event{

	public CompletedEvent() {
		super();
	}
	
	public CompletedEvent(String name, double goal, double donations, LocalDate deadline) {
		super();
		super.name = name;
		super.goal = goal;
		super.donations = donations;
		super.deadline = deadline;
		super.status = EventStatus.COMPLETED;
	}

}
