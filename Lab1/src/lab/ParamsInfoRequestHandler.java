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
	Boolean bNoParamInfoTemplate = true;
	
	private String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }

	    return stringBuilder.toString();
	}
	
	public ParamsInfoRequestHandler() {
		// TODO Auto-generated constructor stub
		File serverRoot;
		serverRoot = new File(ConfigManager.getInstance().getRoot());
		try
		{
			File paramsFileHeader = new File(serverRoot, paramsInfoHeaderFileName);
			File paramsFileFooter = new File(serverRoot, paramsInfoFooterFileName);
			paramsInfoHeader = readFile(paramsFileHeader.toString());
			paramsInfoFooter = readFile(paramsFileFooter.toString());
			Logger.log("Loaded params_info template successfuly", Logger.LOG_LEVEL_INFO);
			bNoParamInfoTemplate = false;
		}
		catch (IOException e)
		{
			Logger.log("Failed loading params_info template", Logger.LOG_LEVEL_ERROR);
			bNoParamInfoTemplate = true;
		}
	}
	
	
	public void post()
	{
		// If we couldn't find the params info template
		if (bNoParamInfoTemplate)
		{
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
