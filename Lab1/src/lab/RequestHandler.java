package lab;

public class RequestHandler {
    HttpRequest request;
    HttpResponse response;
    public RequestHandler() {

    }

	public HttpResponse handleRequest(HttpRequest request) {
        this.request = request;
        this.response = new HttpResponse(200);

        this.response.setVersion(this.request.getVersion());

        try {
            if (request.getMethod().equals("GET")) {
                this.get();
            }
            if (request.getMethod().equals("POST")) {
                this.post();
            }
            if (request.getMethod().equals("HEAD")) {
                this.head();
            }
            if (request.getMethod().equals("TRACE")) {
                this.trace();
            }

        } catch (Exception e) {
            Logger.error("Exception on server! " + e.getMessage());
            this.response.setStatus(500);
        }

        return this.response;

    }

    public void get() {

    }

    public void post() {

    }

    public void head() {

    }

    public void trace() {
        this.response.setBody(this.request.getRequestBody());
    }

}
