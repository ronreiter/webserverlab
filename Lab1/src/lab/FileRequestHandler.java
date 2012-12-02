package lab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

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

        File file = new File(serverRoot, request.getPath());

        if (!file.exists()) {
            this.response.setStatus(404);
            return;
        }

        this.response.setChunkedTransferEncoding(true);

        try {
            this.response.setStreamBody(new FileInputStream(file));

        } catch (FileNotFoundException e) {
            Logger.error("File not found - " + file.getAbsolutePath());
            e.printStackTrace();
        }



	}


}
