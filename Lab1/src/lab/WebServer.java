package lab;
import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{

	public static void start() throws Exception
	{
		int port = 8080;
		
		// Establish the listen socket.
		ServerSocket socket = new ServerSocket(port);
		
		// Process HTTP service requests in an infinite loop.
		while (true)
		{
		    // Listen for a TCP connection request.
		    Socket connection = socket.accept();
		    
		    // Construct an object to process the HTTP request message.
		    ConnectionHandler request = new ConnectionHandler(connection);
		    
		    // Create a new thread to process the request.
		    Thread thread = new Thread(request);
		    
		    // Start the thread.
		    thread.start();
		}
    }
	
	public static void stop() {
		
	}
}


