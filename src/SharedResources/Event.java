package SharedResources;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

// Base class for events
// Used for shared functionality and to have a generic type for transit to and from the server
public abstract class Event implements Displayable {
	
	String name = "";
	double goal = 0.0;
	double donations = 0.0;
	Date deadline = null;
	EventStatus status = null;
	int index;
	
	private static int count = 0;
	
	public Event() {
		name = "";
		goal = 0.0;
		donations = 0.0;
		deadline = new Date(0l);
		status = EventStatus.COMPLETED;
		index = count++;
	}
	
	public String getName() {
		return name;
	}
	
	public double getGoal() {
		return goal;
	}
	
	public double getDonations() {
		return donations;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	
	public EventStatus getStatus() {
		return status;
	}
	
	// This method is used for determining if an event has reached it's goal
	public boolean isGoalMet() {
		return donations > goal;
	}
	
	// Method to represent deleting an event
	public void delete() {
		deadline = new Date(0l);
		status = EventStatus.COMPLETED;
		count--;
	}
	
	// This method implements the interface Displayable
	// It creates a string Card representation of an event with all information included
	public String display() {
		String goalString = "GOAL: " + NumberFormat.getCurrencyInstance().format(goal);
		String donationsString = "DONATIONS: " + NumberFormat.getCurrencyInstance().format(donations);
		String deadlineString = "DEADLINE: " + simpleDateString(deadline);
		
		// determining the width of the card
		int width = Math.max(
						name.length(),
						Math.max(
								Math.max(donationsString.length(), goalString.length()), 
								Math.max(deadlineString.length(), status.toString().length())
						) 
					) + 2;
		
		StringBuilder strBuild = new StringBuilder();
		strBuild.append('+');
		char[] longLine = new char[width + 2];
		Arrays.fill(longLine, '-');
		strBuild.append(longLine);
		strBuild.append('+');
		strBuild.append("\n|  " + String.format("%-" + (width) + "s", name) + "|");
		strBuild.append("\n|  " + String.format("%-" + (width) + "s", goalString) + "|");
		strBuild.append("\n|  " + String.format("%-" + (width) + "s", donationsString) + "|");
		strBuild.append("\n|  " + String.format("%-" + (width) + "s", deadlineString) + "|");
		strBuild.append("\n|  " + String.format("%-" + (width) + "s", status.toString()) + "|");
		strBuild.append("\n+");
		strBuild.append(longLine);
		strBuild.append("+");
		
		return strBuild.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Event) {
			Event e = (Event) o;
			return this.name.equals(e.getName()) 
					&& simpleDateString(this.deadline).equals(simpleDateString(e.getDeadline()))
					&& this.goal - e.getGoal() < 0.0001;
		}
		return false;
	}
	
	// private helper method to get a simple date format string
	private String simpleDateString(Date d) {
		return d.getMonth() + "-" + d.getDate() + "-" + d.getYear();
	}
}
