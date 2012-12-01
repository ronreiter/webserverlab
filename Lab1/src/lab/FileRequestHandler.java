package lab;

import java.util.HashMap;
import java.util.Map;

public class FileRequestHandler implements RequestHandler {
	public Map<String,String> contentTypes;
	
	public FileRequestHandler() {
		contentTypes = new HashMap<String,String>();
		contentTypes.put("png", "image");
		contentTypes.put("bmp", "image");
		contentTypes.put("jpg", "image");
		contentTypes.put("gif", "image");
		
		contentTypes.put("ico", "icon");
		
		contentTypes.put("html", "text/html");
		
	}
	
	public HttpResponse handleRequest(HttpRequest request) {
		return null;
	}

}
