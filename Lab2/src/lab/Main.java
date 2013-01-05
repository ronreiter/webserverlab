package lab;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		
        try {
        	WebServer server = new WebServer();
        	Logger.info("Starting server...");
            server.start();
        } 
        
        catch (IOException e) {
            Logger.critical("Error starting server! Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
