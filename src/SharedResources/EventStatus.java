package SharedResources;

public enum EventStatus {
	CURRENT,
	COMPLETED;
	
	public String toString() {
		if(this == EventStatus.CURRENT) return "CURRENT";
		return "COMPLETED";
	}
}
