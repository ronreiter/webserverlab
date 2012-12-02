package lab;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
	private String body;
    private InputStream streamBody;
	private String method;
	private String version;
	private int status;
	private Map<String,String> headers;
    private Map<Integer,String> statusMessage;

    public static int CHUNK_SIZE = 100;

    public HttpResponse(int status) {
        this.status = status;

		this.headers = new HashMap<String, String>();
        this.headers.put("content-type", "text/html");

        this.statusMessage = new HashMap<Integer, String>();

        this.statusMessage.put(200, "OK");
        this.statusMessage.put(400, "Bad Request");
        this.statusMessage.put(404, "Not Found");
        this.statusMessage.put(500, "Internal Server Error");
        this.statusMessage.put(501, "Not Implemented");
    }

    public void setChunkedTransferEncoding(boolean chunkedTransferEncoding) {
        if (chunkedTransferEncoding) {
            this.headers.put("transfer-encoding", "chunked");
        } else {
            this.headers.remove("transfer-encoding");
        }
    }

	public void setHeader(String headerName, String headerValue) {
		headers.put(headerName, headerValue);
	}
	
	public void setBody(String body) {
		this.body = body;
	}

    public void setStreamBody(InputStream streamBody) {
        this.streamBody = streamBody;
    }
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public void serialize(OutputStream output) throws IOException {
        String responseLine = "HTTP/" + this.version + " " + Integer.toString(this.status) + " " + statusMessage.get(this.status) + "\r\n";
        output.write(responseLine.getBytes());

        if (this.body != null) {
            this.headers.put("content-length", Integer.toString(this.body.length()));
        }

        // dump headers
        for (String headerKey : headers.keySet()) {
            String headerLine;
            headerLine = headerKey + ": " + headers.get(headerKey) + "\r\n";
            output.write(headerLine.getBytes());
        }

        // finish the header part with an empty line
        output.write("\r\n".getBytes());

        if (this.headers.containsKey("transfer-encoding") && this.headers.get("transfer-encoding").equals("chunked")) {
            while (true) {
                byte[] chunk = new byte[CHUNK_SIZE];
                int bytesRead = this.streamBody.read(chunk);

                if (bytesRead == -1) {
                    output.write("0\r\n".getBytes());
                    break;
                } else {
                    String chunkSize = Integer.toHexString(bytesRead) + "\r\n";
                    output.write(chunkSize.getBytes());
                    output.write(chunk, 0, bytesRead);
                    output.write("\r\n".getBytes());
                }

            }
        } else {
            if (this.body != null) {
                output.write(this.body.getBytes());
            } else if (this.streamBody != null) {
                while (true) {
                    byte[] chunk = new byte[CHUNK_SIZE];
                    int bytesRead = this.streamBody.read(chunk);

                    if (bytesRead == -1) {
                        break;
                    } else {
                        output.write(chunk, 0, bytesRead);
                    }

                }

            }
        }

	}
	
}
