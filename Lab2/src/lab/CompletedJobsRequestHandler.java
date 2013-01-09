package lab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
//import java.util.logging.LoggingMXBean;

public class CompletedJobsRequestHandler extends RequestHandler {
	public void get() {
        Map<String, Object> templateValues = new HashMap<String, Object>();
        StringBuilder fileList = new StringBuilder();

        File folder = new File(new File("root"), "reports");

        if (!folder.exists()) {
            folder.mkdir();
        }

        for (File file : folder.listFiles()) {
            fileList.append("<tr><td><a href='/reports/" + file.getName() + "'>" + file.getName() + "</a></td></tr>");
        }

        templateValues.put("files", fileList.toString());
        renderTemplate("templates/completed.html", templateValues);
	}
}
