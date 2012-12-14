package lab;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ParamsInfoRequestHandler extends RequestHandler {
	
	String paramsInfoHeaderFileName= "params_info_header.html";
	String paramsInfoFooterFileName= "params_info_footer.html";
	
	String paramsInfoHeader;
	String paramsInfoFooter;
	
	String paramsInfoBody;
	
	private String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }
	    
	    reader.close();
	    return stringBuilder.toString();
	}
	
	public ParamsInfoRequestHandler() {		
	}
	
	
	public void post()
	{
		File serverRoot;
		
		// multiple domain support
        if (request.getHost() == null) {
            serverRoot = new File(ConfigManager.getInstance().getRoot());
        } else {
            serverRoot = new File(ConfigManager.getInstance().getSitesRoot());
            serverRoot = new File(serverRoot, request.getHost());
        }
        
		try
		{
			File paramsFileHeader = new File(serverRoot, paramsInfoHeaderFileName);
			File paramsFileFooter = new File(serverRoot, paramsInfoFooterFileName);
			paramsInfoHeader = readFile(paramsFileHeader.toString());
			paramsInfoFooter = readFile(paramsFileFooter.toString());
			Logger.log("Loaded params_info template successfuly", Logger.LOG_LEVEL_INFO);
		}
		catch (IOException e)
		{
			Logger.log("Failed loading params_info template", Logger.LOG_LEVEL_ERROR);
			this.response.setStatus(500);
			return;
		}
		
		
        if (request.getHeaders().containsKey("chunked") && request.getHeaders().get("chunked").toLowerCase().equals("yes")) {
            Logger.debug("Setting chunked transfer encoding");
            this.response.setChunkedTransferEncoding(true);
        }

        String contentType = "text/html";

        paramsInfoBody = paramsInfoHeader;
        
        for (String parameterKey : request.parameters.keySet())
        {
        	paramsInfoBody += "<p><b>" + parameterKey + "</b>: <b>" + request.parameters.get(parameterKey) + "</b></p>";
        }
        
        paramsInfoBody += paramsInfoFooter;
        
        this.response.setHeader("content-type", contentType);
        this.response.setStreamBody(new ByteArrayInputStream(paramsInfoBody.getBytes()));   
	}
	
	public void get()
	{
		this.post();
	}
}
