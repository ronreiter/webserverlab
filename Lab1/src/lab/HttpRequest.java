package lab;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	private static String GET_METHOD = "GET";
	private static String POST_METHOD = "POST";
	private static String TRACE_METHOD = "TRACE";
	private static String HEAD_METHOD = "HEAD";
	private static String CONTENT_LENGTH_HEADER = "content-length";
	String method;
	String version;
	String host;
	String path;
	String body;
	Map<String,String> headers;
	Map<String,String> parameters;
	
	public class HttpRequestException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
	}

    public HttpRequest() {
        headers = new HashMap<String, String>();
        parameters = new HashMap<String, String>();
    }
		
	public static HttpRequest parse(InputStream data) throws IOException {
		HttpRequest newInstance = new HttpRequest();

		BufferedReader reader = new BufferedReader(new InputStreamReader(data));

		//Handle the METHOD line
		String requestLine = reader.readLine();

		if (requestLine == null) {
            throw new RuntimeException("Bad request - zero length");
        }
		
		//Verify format Integrity: "METHOD PATH&PARAM HTTP\VER\r\n" 
		String[] requestParts = requestLine.split(" ");
		if (requestParts.length != 3) {
            throw new RuntimeException("Bad request - bad format");
        }
		
		// Handle the version portion (HTTP/1.X)
		String [] HTTPVerList = requestParts[2].split("/");
		if (HTTPVerList.length != 2) {
            throw new RuntimeException("Bad request - version format error");
        }

		if (!HTTPVerList[0].equals("HTTP")) {
            throw new RuntimeException("Bad request - version format error - NOT HTTP");
        }

		if (HTTPVerList[1].equals("1.0") || HTTPVerList[1].equals("1.1")) {
            newInstance.version = HTTPVerList[1];
        } else {
            throw new RuntimeException("Bad request - version - version: " + HTTPVerList[1] + " not supported");
        }

		// Handle the method portion (GET/TRACE/POST..)
		if (requestParts[0].equals(GET_METHOD)) {
            newInstance.method = GET_METHOD;
        } else if (requestParts[0].equals(POST_METHOD)) {
            newInstance.method = POST_METHOD;
        } else if (requestParts[0].equals(TRACE_METHOD)) {
            newInstance.method = TRACE_METHOD;
        } else if (requestParts[0].equals(HEAD_METHOD)) {
            newInstance.method = HEAD_METHOD;
        } else {
            throw new RuntimeException("Bad request - method not supported");
        }

		// Handle the path portion
		String [] requestPathParams = requestParts[1].split("\\?");
		if (requestPathParams.length == 0 || requestPathParams.length > 2) {
            throw new RuntimeException("Bad request - path/params error");
        }
		
		//Updated path		
		newInstance.path = requestPathParams[0];
		
		//Handle Params
		if (requestPathParams.length == 2)
		{
			String [] requestParams = requestPathParams[1].split("&");
			for (String s : requestParams)
			{
				String [] varValue = s.split("=");
				if (varValue.length != 2) throw new RuntimeException("Bad request - Bad method parameters format: " + s);
				newInstance.parameters.put(varValue[0], varValue[1]);
			}
		} 
		//else newInstance.parameters = null;

		//Handle Headers
		requestLine = reader.readLine();

		while (!requestLine.equals(""))
		{
			String [] headerKey = requestLine.split(":",2);
			if (headerKey.length != 2) throw new RuntimeException("Bad Request - bad headers: " + requestLine);
			newInstance.headers.put(headerKey[0].toLowerCase(), headerKey[1]);
			requestLine = reader.readLine();
		}
		
		if (newInstance.headers.containsKey(CONTENT_LENGTH_HEADER))
		{		
			Integer contentLength = Integer.parseInt(newInstance.headers.get(CONTENT_LENGTH_HEADER).trim());
			
			if (newInstance.method.equals(POST_METHOD))
			{
				CharBuffer postBody = CharBuffer.allocate(contentLength);
				//requestLine = reader.readLine();
				reader.read(postBody);
				postBody.position(0);
				
				newInstance.body += postBody.toString();
				
				String [] requestParams = postBody.toString().split("&");
				for (String line: requestParams)
				{
					String [] varValue = line.split("=");
					if (varValue.length != 2) throw new RuntimeException("Ba Request - Bad BODY parameters format: " + requestLine);
					newInstance.parameters.put(varValue[0], varValue[1]);
				}
			} else {
				
				byte [] byteArray = new byte[contentLength];
				
				data.read(byteArray);
				newInstance.body = byteArray.toString();
			}
		
		}
				
		return newInstance;
	}
	
	public String getMethod() {
		return this.method;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getRequestBody() {
		return this.body;
	}

	public Map<String,String> getHeaders() {
		return headers;
	}
	
	public Map<String,String> getParamters() {
		return parameters;
	}
}
