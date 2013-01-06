package lab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ron
 * Date: 1/5/13
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResultFileGenerator {
    String domain = null;
    Date date = null;
    File outputFile = null;
    public ResultFileGenerator(URL crawlURL) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        date = new Date();
        outputFile = new File("root", crawlURL.getHost() + "_" + dateFormat.format(date) + ".html");
    }

    public void writeFile() throws IOException {
        Map<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put("domain", "http://www.example.com");
        templateValues.put("robots", "Yes");
        templateValues.put("num_images", 10);
        templateValues.put("total_size_images", 100000);
        templateValues.put("num_videos", 20);
        templateValues.put("total_size_videos", 200000);
        templateValues.put("num_documents", 30);
        templateValues.put("total_size_documents", 300000);
        templateValues.put("num_pages", 40);
        templateValues.put("total_size_pages", 400000);
        templateValues.put("num_links", 100);
        templateValues.put("num_domains", 200);
        templateValues.put("connected_domains", "<li><a href='http://www.example.com'>http://www.example.com</a></li>");
        templateValues.put("average_rtt", 300);

        String resultsTemplate = RequestHandler.readFile("templates/results.html");
        String renderedTemplate = RequestHandler.renderString(resultsTemplate, templateValues);
        FileWriter writer = new FileWriter(outputFile);
        writer.write(renderedTemplate);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        ResultFileGenerator results = new ResultFileGenerator(new URL("http://www.google.com"));
        results.writeFile();

    }
}
