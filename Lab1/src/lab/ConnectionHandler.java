package lab;
//import java.io.* ;
import java.net.* ;
//import java.util.* ;
//import java.util.logging.LoggingMXBean;

final class ConnectionHandler implements Runnable
{
    ConnectionPool parent;
    RequestRouter router;
    
    // Constructor
    public ConnectionHandler(ConnectionPool parent)
	{
        this.parent = parent;
        this.router = new RequestRouter();
    }
    
    // Implement the run() method of the Runnable interface.
    public void run()
	{
        while (true) {
            try {
                Socket socket = parent.dequeue();
                processRequest(socket);

            } catch (InterruptedException e) {
                Logger.error("Thread interrupted. Stopping");
                break;
            } catch (Exception e) {
                Logger.error("Got exception while handling user connection");
                e.printStackTrace();
            }
        }
	}

	private void processRequest(Socket socket) throws Exception
	{
        HttpRequest request;
        HttpResponse response;
        while (true) {
            try {
                request = HttpRequest.parse(socket.getInputStream());
                response = router.handleRequest(request);
                Logger.info(request.getMethod() + " " + request.getPath() + " " + response.getStatus());
                response.serialize(socket.getOutputStream());

                // check for connection: keep-alive
                if (!(request.getHeaders().containsKey("connection") && request.getHeaders().get("connection").equals("keep-alive"))) {
                    Logger.debug("No keep-alive, closing connection");
                    break;
                }

            } catch (Exception e) {
                response = new HttpResponse(500);
                response.serialize(socket.getOutputStream());
                socket.close();
                return;
            }

        }

		socket.close();
    }

}


