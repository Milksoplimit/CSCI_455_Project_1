package Server;


// Basic logging service that prints messages to the console
public class LoggingService {
	
	private String source;
	
	public LoggingService() {}
	
	public LoggingService(String source) {
		this.source = source;
	}
	
	public void log(String message) {
		System.out.println(source + ": " + message);
	}

}
