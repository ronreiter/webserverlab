package lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpRequest {
	private static String GET_METHOD = "GET";
	private static String POST_METHOD = "POST";
	private static String TRACE_METHOD = "TRACE";
	String method;
	String version;
	String host;
	String path;
	String body;
	Map<String,String> headers;
	Map<String,String> parameters;
	
	public class HttpRequestException extends RuntimeException
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	};
		
	private static void HandlePath(String requestPath)
	{
		
	}
	public static HttpRequest parse(InputStream data) throws IOException {
		
		HttpRequest newInstance = new HttpRequest();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(data));
		
		
		//Handle the METHOD line
		String requestLine = reader.readLine();

		if (requestLine == null) throw new RuntimeException("Bad Request - zero length");
		
		//Verify format Integrity: "METHOD PATH&PARAM HTTP\VER\r\n" 
		String[] requestParts = requestLine.split(" ");
		if (requestParts.length != 3) throw new RuntimeException("Bad Request - bad format");
		
		// Handle the version portion (HTTP/1.X)
		String [] HTTPVerList = requestParts[2].split("//");
		if (HTTPVerList.length != 2) throw new RuntimeException("Bad Request - version format error");
		if (HTTPVerList[0] != "HTTP") throw new RuntimeException("Bad Request - version format error - NOT HTTP");
		if (HTTPVerList[1] == "1.0" ||  HTTPVerList[1] == "1.1") newInstance.version = HTTPVerList[1]; 
		else throw new RuntimeException("Bad Request - version - version: " + HTTPVerList[1] + " Not supported");

		// Handle the method portion (GET/TRACE/POST..)
		if (requestParts[0] == GET_METHOD) newInstance.method = GET_METHOD;		
		else if (requestParts[0] == POST_METHOD) newInstance.method = POST_METHOD;
		else if (requestParts[0] == TRACE_METHOD) newInstance.method = TRACE_METHOD;
		else throw new RuntimeException("Bad Request - Method not supported"); 
	
		// Handle the path portion
		String [] requestPathParams = requestParts[1].split("?");
		if (requestPathParams.length == 0 || requestPathParams.length > 2) throw new RuntimeException("Bad Request - Path/Params error");
		
		//Handle Path
		
		//Handle Params
		if (requestPathParams.length == 2)
		{
			String [] requestParams = requestPathParams[1].split("&");
			for (String s : requestParams)
			{
				String [] varValue = s.split("=");
				if (varValue.length != 2) throw new RuntimeException("Ba Request - Bad parameters format: " + s);
				newInstance.parameters.put(varValue[0], varValue[1]);
			}
		} 
		else newInstance.parameters = null;
		
		
		//Handle Headers
		requestLine = reader.readLine();
		newInstance.headers = null;
		
		while (requestLine != "")
			
			
			// TODO: parse body if needed (POST and content-length)
		
		return null;
	}
	
	public String getType() {
		return "GET";
	}
	
	public String getVersion() {
		return "HTTP/1.0";
	}
	
	public String getHost() {
		return "www.example.com";
	}
	
	public String getPath() {
		return "/";
	}
	
	public String getRequestBody() {
		return null;
	}

	public Map<String,String> getHeaders() {
		return null;
	}
	
	public Map<String,String> getParamters() {
		return null;
	}
	
	public Map<String,String> getGETParamters() {
		return null;
	}
	
	public Map<String,String> getPOSTParamters() {
		return null;
	}
}
