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
    public static final int STATUS_ERROR_UNKNOWN = 4;

    CrawlerRequestQueue requestQueue;
    List<CrawlRequest> requests;

    public Crawler() {
        requestQueue = new CrawlerRequestQueue();
        requests = new LinkedList<CrawlRequest>();

    }

    private int createTask(String urlToAdd, boolean ignoreRobots)
    {
        try {
            // Check that the URL is valid
            URL url = new URL(urlToAdd);

            // make sure the domain is valid
            InetAddress.getByName(url.getHost());

            // Create the new Task
            if (requests.size() == ConfigManager.getInstance().getMaxCrawlerThreads())
            {
                return STATUS_BUSY;
            }  else
            {
                CrawlRequest request = new CrawlRequest(url, ignoreRobots);

                requests.add(request);
                requestQueue.enqueue(request);
            }

        } catch (UnknownHostException e) {
            Logger.error("Failed parsing requested URL: " + urlToAdd);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return STATUS_ERROR_BAD_URL;
        } catch (MalformedURLException e) {
            return STATUS_ERROR_BAD_URL;
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
        if (requests.size() == ConfigManager.getInstance().getMaxCrawlerThreads())
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
