package lab;

import java.io.InputStream;
import java.util.Map;

public class HttpRequest {
	public static HttpRequest parse(InputStream data) {
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
