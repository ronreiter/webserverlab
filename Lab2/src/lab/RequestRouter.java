package lab;

import java.util.ArrayList;
import java.util.List;

public class RequestRouter {	
	public class Route {
        public Route(String regexp, String domain, RequestHandler handler) {
            this.regexp = regexp;
            this.domain = domain;
            this.handler = handler;
        }

		String domain;
		String regexp;
		RequestHandler handler;
	}
	
	private List<Route> routes;
	
	public RequestRouter() {
        routes = new ArrayList<Route>();

        // TODO: routes.add(new Route(".*", null, FileRequestHandler));
        // TODO: handle paramsinfo?
        routes.add(new Route("/serverConfig", null, new CrawlerRequestHandler()));
        routes.add(new Route("/params_info.html", null, new ParamsInfoRequestHandler()));
        routes.add(new Route(".*", null, new FileRequestHandler()));
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
