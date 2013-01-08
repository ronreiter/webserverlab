package lab;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tamir
 * Date: 1/6/13
 * Time: 1:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrawlRequest {
    public static int PROGRESS_WAITING_TO_START = 1;
    public static int PROGRESS_WORKING = 2;
    public static int PROGRESS_FINISHED = 3;

    URL urlToCrawl;
    boolean ignoreRobots;
    int progress = PROGRESS_WAITING_TO_START;
    long totalImages = 0;
    long totalVideos = 0;
    long totalDocuments = 0;
    long totalImagesBytes = 0;
    long totalVideosBytes = 0;
    long totalDocumentsBytes = 0;
    long totalPages = 0;
    long totalPagesBytes = 0;
    long totalLinks = 0;
    List<String> domainsConnected;
    long averageRTT = 0;

    public CrawlRequest(URL urlToCrawl, boolean ignoreRobots) {
        this.urlToCrawl = urlToCrawl;
        this.ignoreRobots = ignoreRobots;
        this.domainsConnected = new LinkedList<String>();
    }
}
