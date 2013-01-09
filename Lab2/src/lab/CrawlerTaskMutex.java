package lab;

/**
 * Created with IntelliJ IDEA.
 * User: Tamir
 * Date: 1/6/13
 * Time: 12:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrawlerTaskMutex {
    private int counter = 0;

    public synchronized int register()
    {
        Logger.debug("Crawler Task started got: " + (counter + 1) + " crawls running");
        return ++counter;
    }
    public synchronized int unregister()
    {
        Logger.debug("Crawler Task finished, got: " + (counter - 1) + " crawls running");
        return --counter;
    }
    public int count()
    {
        return counter;
    }

}
