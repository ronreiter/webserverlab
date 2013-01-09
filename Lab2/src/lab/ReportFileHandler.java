package lab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
//import java.util.logging.LoggingMXBean;

public class ReportFileHandler extends FileRequestHandler {
	public void get() {
        if (request.getHeaders().get("referer") == null) {
            response.setStatus(403);
            return;
        }
        if (!request.getHeaders().get("referer").endsWith("/completed")) {
            response.setStatus(403);
            return;
        }

        super.get();
	}


}
