package lab;

/**
 * Created with IntelliJ IDEA.
 * User: Tamir
 * Date: 1/6/13
 * Time: 1:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrawlRequest {

    String URLToCrawl;
    boolean ignoreRobots;

    public CrawlRequest(String URLToCrawl, boolean ignoreRobots) {
        this.URLToCrawl = URLToCrawl;
        this.ignoreRobots = ignoreRobots;
    }
}
