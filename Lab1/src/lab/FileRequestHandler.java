package lab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LoggingMXBean;

public class FileRequestHandler extends RequestHandler {
	public Map<String,String> contentTypes;
	public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	
	public FileRequestHandler() {
		contentTypes = new HashMap<String,String>();
		contentTypes.put("png", "image");
		contentTypes.put("bmp", "image");
		contentTypes.put("jpg", "image");
		contentTypes.put("gif", "image");
		contentTypes.put("ico", "icon");
		contentTypes.put("html", "text/html");
		
	}
	
	public void get() {
        File serverRoot;

        if (request.getHost() == null) {
            serverRoot = new File(ConfigManager.getInstance().getRoot());
        } else {
            serverRoot = new File(ConfigManager.getInstance().getSitesRoot());
            serverRoot = new File(serverRoot, request.getHost());
        }

        File file;

        if (request.getPath().equals("/")) {
            file = new File(serverRoot, ConfigManager.getInstance().getDefaultPage());
        } else {
            file = new File(serverRoot, request.getPath());
        }

        // check that the file is in the server root
        if (!file.getAbsolutePath().startsWith(serverRoot.getAbsolutePath())) {
            Logger.error("Attempted to read a file which is out of the serer root! File: " + file.getAbsolutePath());
            this.response.setStatus(404);
            return;
        }

        if (!file.exists()) {
            Logger.debug("File doesn't exist! File: " + file.getAbsolutePath());
            this.response.setStatus(404);
            return;
        }

        if (request.getHeaders().containsKey("chunked") && request.getHeaders().get("chunked").toLowerCase().equals("yes")) {
            Logger.debug("Setting chunked transfer encoding");
            this.response.setChunkedTransferEncoding(true);
        }

        try {
            // get the content type according to the file extension
            String extension = file.getName().substring(file.getName().lastIndexOf('.')+1);
            String contentType = contentTypes.containsKey(extension) ? contentTypes.get(extension) : DEFAULT_CONTENT_TYPE;

            this.response.setHeader("content-type", contentType);
            this.response.setStreamBody(new FileInputStream(file));

        } catch (FileNotFoundException e) {
            Logger.error("File not found - " + file.getAbsolutePath());
            this.response.setStatus(500);
            e.printStackTrace();
        }



	}


}
