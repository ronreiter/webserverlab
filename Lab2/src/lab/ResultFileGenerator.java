package lab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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



    public void writeFile(CrawlRequest crawlRequest) throws IOException {
        String renderedTemplate = CrawlerRequestHandler.generateTemplateWithResults(crawlRequest, "templates/results.html");
        FileWriter writer = new FileWriter(outputFile);
        writer.write(renderedTemplate);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        CrawlRequest crawlRequest = new CrawlRequest(new URL("http://www.google.com"), true);
        crawlRequest.domainsConnected = new LinkedHashSet<String>();
        crawlRequest.domainsConnected.add("www.facebook.com");
        crawlRequest.domainsConnected.add("www.gmail.com");
        crawlRequest.averageRTT = 100;
        crawlRequest.totalLinks = 100;
        crawlRequest.totalDocuments = 100;
        crawlRequest.totalImages = 100;
        crawlRequest.totalPages = 100;
        crawlRequest.totalVideos = 100;
        crawlRequest.totalDocumentsBytes = 100000;
        crawlRequest.totalImagesBytes = 100000;
        crawlRequest.totalPagesBytes = 100000;
        crawlRequest.totalVideosBytes = 100000;
        crawlRequest.progress = CrawlRequest.PROGRESS_WORKING;


        ResultFileGenerator results = new ResultFileGenerator(new URL("http://www.google.com"));
        results.writeFile(crawlRequest);

    }
}
