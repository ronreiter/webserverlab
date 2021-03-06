package lab;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ron
 * Date: 1/5/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Crawler {
    private static Crawler instance = null;
    public static final int STATUS_READY = 1;
    public static final int STATUS_BUSY = 2;
    public static final int ADD_STATUS_SUCCESS = 1;
    public static final int ADD_STATUS_RUNNING = 2;
    public static final int STATUS_ERROR_BAD_URL = 3;
    public static final int STATUS_ERROR_UNKNOWN_HOST = 4;
    public static final int STATUS_ERROR_UNKNOWN = 5;

    List<CrawlRequest> requests;

    CrawlTaskPool crawlTaskPool;

    public Crawler() {

        requests = new LinkedList<CrawlRequest>();
        crawlTaskPool = new CrawlTaskPool();

        crawlTaskPool.start();
    }

    private int createTask(String urlToAdd, boolean ignoreRobots)
    {
        Logger.info("Creating new task, url: " + urlToAdd.toString() + ", ignore robots: " + ignoreRobots);

        try {
            URL url;
            // Check that the URL is valid
            if (!urlToAdd.contains("://")) {
                url = new URL("http://" + urlToAdd);
            } else {
                url = new URL(urlToAdd);
            }

            // make sure the domain is valid
            Logger.debug("Resolving " + url.getHost());
            InetAddress.getByName(url.getHost());

            // Create the new Task
            if (crawlTaskPool.taskMutex.count() == ConfigManager.getInstance().getMaxCrawlerThreads())
            {
                Logger.debug("rejecting crawl request due to server too busy: " + url.toString());
                return STATUS_BUSY;
            }  else if (crawlTaskPool.taskMutex.count() < ConfigManager.getInstance().getMaxCrawlerThreads())
            {
                Logger.debug("Adding crawl request number: " + crawlTaskPool.taskMutex.count() + " URL: " + url.toString());
                CrawlRequest request = new CrawlRequest(url, ignoreRobots);
                crawlTaskPool.enqueue(request);
                requests.add(request);

                // wait 1 second so we'll get the updated status
                Thread.sleep(100);

            } else {
                Logger.critical("More crawler tasks than allowed!");
                return STATUS_ERROR_UNKNOWN;
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return STATUS_ERROR_UNKNOWN_HOST;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return STATUS_ERROR_BAD_URL;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return STATUS_ERROR_UNKNOWN;
        }

        return STATUS_READY;
    }

    public int add(String URLToAdd, boolean ignoreRobots)
    {
        try {
            return createTask(URLToAdd, ignoreRobots);
        }
        catch (RuntimeException e)
        {
            return STATUS_ERROR_UNKNOWN;
        }

    }

    public int getStatus()
    {
        if (crawlTaskPool.taskMutex.count() >= ConfigManager.getInstance().getMaxCrawlerThreads())
        {
            return STATUS_BUSY;
        }

        return STATUS_READY;
    }

    public static Crawler getInstance() {
        if (instance == null) {
            instance = new Crawler();
        }

        return instance;
    }

    public List<CrawlRequest> getRequests() {
        return requests;
    }
}
