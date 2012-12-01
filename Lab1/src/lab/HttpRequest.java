package lab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpRequest {
	String type;
	String version;
	String host;
	String path;
	String body;
	Map<String,String> headers;
	Map<String,String> parameters;
	
	public static HttpRequest parse(InputStream data) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(data));
		String requestLine = reader.readLine();
		
		// TODO: parse GET /path?a=1&b=2 HTTP/1.l
		
		// TODO: parse headers
		
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
