package lab;


/**
 * Created with IntelliJ IDEA.
 * User: ron
 * Date: 1/5/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrawlTask implements Runnable {

    CrawlerTaskPool parent;
    CrawlerTaskMutex taskMutex;

    public CrawlTask(CrawlerTaskPool parent, CrawlerTaskMutex taskMutex) {
        this.parent = parent;
        this.taskMutex = taskMutex;
    }

    private void processTask(String task)
    {
        taskMutex.register();
        taskMutex.unregister();
    }

    @Override
    public void run() {
        while (true) {
            String task;

            try {
                task = parent.dequeue();
                taskMutex.register();

            } catch (InterruptedException e) {
                Logger.error("Thread interrupted. Stopping");
                break;
            } catch (Exception e) {
                Logger.error("Got exception while handling task");
                e.printStackTrace();
                continue;
            }

            try {
                processTask(task);

            } catch (Exception e) {

                Logger.error("Got exception while handling task");
                e.printStackTrace();
            } finally {
                taskMutex.unregister();
            }
        }
    }

}
