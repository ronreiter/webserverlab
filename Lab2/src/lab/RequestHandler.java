package lab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

public class RequestHandler {
    HttpRequest request;
    HttpResponse response;
    public RequestHandler() {

    }

	public HttpResponse handleRequest(HttpRequest request) {
        this.request = request;
        this.response = new HttpResponse(200);

        this.response.setVersion(this.request.getVersion());

        if (request.getHeaders().containsKey("chunked") && request.getHeaders().get("chunked").toLowerCase().equals("yes")) {
            Logger.debug("Setting chunked transfer encoding");
            this.response.setChunkedTransferEncoding(true);
        }

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
            e.printStackTrace();
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

    public static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }

    public static String renderString(String format, Map<String, Object> values) {
        for (String key : values.keySet()) {
            format = format.replace("{{ " + key + " }}", values.get(key).toString());
        }
        return format;
    }

    public void renderTemplate(String path, Map<String, Object> values) {
        String format = null;
        try {
            format = readFile(path);
        } catch (IOException e) {
            Logger.critical("Cannot find template at " + path);
            e.printStackTrace();
            return;
        }

        response.setBody(renderString(format, values));
    }
}
