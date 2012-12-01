package lab;
import java.io.* ;
import java.net.* ;
import java.util.* ;

final class ConnectionHandler implements Runnable
{
    final static String CRLF = "\r\n";
    ConnectionPool parent;
    
    // Constructor
    public ConnectionHandler(ConnectionPool parent)
	{
        this.parent = parent;
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
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		//Get the source IP
		String sourceIP = socket.getInetAddress().getHostAddress();
		
		// Construct the response message.
		String statusLine = "HTTP/1.0 200 OK" + CRLF;
		String contentTypeLine = "Content-Type: text/html" + CRLF;
		String contentLength = "Content-Length: ";
		
		String entityBody = "<HTML>" + 
		"<HEAD><TITLE>"+ sourceIP +"</TITLE></HEAD>" +
		"<BODY><H1>"+ sourceIP +"</H1></BODY></HTML>";
		
		// Send the status line.
		os.writeBytes(statusLine);
		
		// Send the content type line.
		os.writeBytes(contentTypeLine);
		
		// Send content length.
		os.writeBytes(contentLength + entityBody.length() + CRLF);
		
		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);
		
		// Send the content of the HTTP.
		os.writeBytes(entityBody) ;
		
		// Close streams and socket.
		os.close();
		socket.close();
    }

}


