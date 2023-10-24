package SharedResources;

// Enum representing the status of an event
public enum EventStatus {
	CURRENT,
	COMPLETED;
	
	public String toString() {
		if(this == EventStatus.CURRENT) return "CURRENT";
		return "COMPLETED";
	}
}
