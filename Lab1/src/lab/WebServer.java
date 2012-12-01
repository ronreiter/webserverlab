package lab;
import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{

	public void start() throws IOException
	{
		int port = ConfigManager.getInstance().getPort();
		
		// Establish the listen socket.
		ServerSocket socket = new ServerSocket(port);

        ConnectionPool pool = new ConnectionPool();
        pool.start();
		
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
	
	public void stop() {
		
	}
}


