package Server;

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
