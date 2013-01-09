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
        routes.add(new Route("/reports/.*", null, new ReportFileHandler()));
        routes.add(new Route("/progress", null, new CrawlerRequestsJSONHandler()));
        routes.add(new Route("/completed", null, new CompletedJobsRequestHandler()));
        routes.add(new Route("/", null, new CrawlerRequestHandler()));
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
