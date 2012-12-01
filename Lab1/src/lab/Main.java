package lab;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		WebServer server = new WebServer();
        try {
            Logger.info("Starting server...");
            server.start();
        } catch (IOException e) {
            Logger.critical("Error starting server! Exception: " + e.getStackTrace());
        }

    }

}
