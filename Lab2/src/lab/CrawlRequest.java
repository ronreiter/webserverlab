package lab;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Tamir
 * Date: 1/6/13
 * Time: 1:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrawlRequest {

    URL urlToCrawl;
    boolean ignoreRobots;

    public CrawlRequest(URL urlToCrawl, boolean ignoreRobots) {
        this.urlToCrawl = urlToCrawl;
        this.ignoreRobots = ignoreRobots;
    }
}
