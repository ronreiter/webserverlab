package lab;


import java.util.LinkedList;
import java.util.List;

public class CrawlTask implements Runnable {

    CrawlTaskPool parent;
    List<Thread> analyzers;
    List<Thread> downloaders;

    // create the resource queue with a barrier - once all analyzers are waiting then release all of them
    ResourceQueue queue = new ResourceQueue(ConfigManager.getInstance().getMaxAnalyzers());

    public CrawlTask(CrawlTaskPool parent) {
        this.parent = parent;
        this.analyzers = new LinkedList<Thread>();
        this.downloaders = new LinkedList<Thread>();

        // create the analyzers thread pool
        for (int i = 0; i < ConfigManager.getInstance().getMaxCrawlerThreads(); i++) {
            Thread thread = new Thread(new Analyzer(queue));
            thread.run();
            this.analyzers.add(thread);
        }

        // create the downloaders thread pool
        for (int i = 0; i < ConfigManager.getInstance().getMaxCrawlerThreads(); i++) {
            Thread thread = new Thread(new Downloader(queue));
            thread.run();
            this.downloaders.add(thread);
        }
    }

    private void processTask(CrawlRequest task)
    {

    }

    @Override
    public void run() {
        while (true) {
            try {
                CrawlRequest task = parent.dequeue();
                processTask(task);

            } catch (InterruptedException e) {
                Logger.error("Thread interrupted. Stopping");
                break;
            } catch (Exception e) {
                Logger.error("Got exception while handling task");
                e.printStackTrace();
            }

        }
    }

}
