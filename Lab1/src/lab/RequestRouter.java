package lab;

import java.util.List;

public class RequestRouter {	
	public class Route {
		String domain;
		String regexp;
		RequestHandler handler;
	}
	
	private List<Route> routes;
	
	public RequestRouter() {
		
	}
	
	public HttpResponse handleRequest(HttpRequest request) {
		String path = request.getPath();
		
		for (Route route : routes) {
			if (
                    route.domain != null && request.getHost().equals(route.domain) && path.matches(route.regexp) ||
                    route.domain == null && path.matches(route.regexp)) {
				return route.handler.handleRequest(request);				
			}
		}
		return null;
	}
}
