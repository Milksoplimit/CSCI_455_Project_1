package SharedResources;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

public abstract class Event implements Displayable {
	String name = "";
	Double goal = 0.0;
	Double donations = 0.0;
	Date deadline = null;
	EventStatus status = null;
	
	public Event() {
		name = "";
		goal = 0.0;
		donations = 0.0;
		deadline = new Date(0l);
		status = EventStatus.COMPLETED;
	}
	
	public String getName() {
		return name;
	}
	
	public Double getGoal() {
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
	
	public String display() {
		String goalString = "GOAL: " + NumberFormat.getCurrencyInstance().format(goal);
		String donationsString = "DONATIONS: " + NumberFormat.getCurrencyInstance().format(donations);
		String deadlineString = "DEADLINE: " + deadline.toString();
		int width = Math.max(name.length(),
						Math.max(
								Math.max(donationsString.length(), goalString.length()), 
						Math.max(deadlineString.length(), status.toString().length())
						) 
					) + 6;
		StringBuilder strBuild = new StringBuilder();
		strBuild.append('+');
		char[] longLine = new char[width - 2];
		Arrays.fill(longLine, '-');
		strBuild.append(longLine);
		strBuild.append('+');
		strBuild.append("\n|  " + String.format("%-" + (width-4) + "s", name) + "|");
		strBuild.append("\n|  " + String.format("%-" + (width-4) + "s", goalString) + "|");
		strBuild.append("\n|  " + String.format("%-" + (width-4) + "s", donationsString) + "|");
		strBuild.append("\n|  " + String.format("%-" + (width-4) + "s", deadlineString) + "|");
		strBuild.append("\n|  " + String.format("%-" + (width-4) + "s", status.toString()) + "|");
		strBuild.append("\n+");
		strBuild.append(longLine);
		strBuild.append("+");
		return strBuild.toString();
	}
}
