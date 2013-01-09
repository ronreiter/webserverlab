package lab;

/**
 * Created with IntelliJ IDEA.
 * User: Tamir
 * Date: 1/6/13
 * Time: 12:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class Mutex {
    private int counter = 0;

    public synchronized int register(String whatAreYouDoing)
    {
        Logger.debug(whatAreYouDoing + " started got: " + (counter + 1) + " crawls running");
        return ++counter;
    }
    public synchronized int unregister(String whatAreYouDoing)
    {
        Logger.debug(whatAreYouDoing + " finished, got: " + (counter - 1) + " crawls running");
        return --counter;
    }
    public int count()
    {
        return counter;
    }

}
