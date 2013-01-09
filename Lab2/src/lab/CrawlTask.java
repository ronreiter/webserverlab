package lab;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CrawlTask implements Runnable {

    CrawlTaskPool parent;
    public RobotsParser robot;
    List<Thread> analyzers;
    List<Thread> downloaders;
    public CrawlRequest currentRequest;
    Set<String> downloaded;

    // create the resource queue with a barrier - when everyone waits for something to do, we know we need
    // to stop (which is why we limit the resource queue to the number of total threads)
    ResourceQueue queue = new ResourceQueue(
            ConfigManager.getInstance().getMaxAnalyzers() + ConfigManager.getInstance().getMaxDownloaders());

    public CrawlTask(CrawlTaskPool parent) {
        this.parent = parent;
        this.analyzers = new LinkedList<Thread>();
        this.downloaders = new LinkedList<Thread>();
        this.downloaded = new HashSet<String>();

        // create the analyzers thread pool
        for (int i = 0; i < ConfigManager.getInstance().getMaxAnalyzers(); i++) {
            Thread thread = new Thread(new Analyzer(queue, this));
            thread.setName("Analyzer-" + (i+1));
            thread.start();
            this.analyzers.add(thread);
        }

        // create the downloaders thread pool
        for (int i = 0; i < ConfigManager.getInstance().getMaxDownloaders(); i++) {
            Thread thread = new Thread(new Downloader(queue));
            thread.setName("Downloader-" + (i+1));
            thread.start();
            this.downloaders.add(thread);
        }
    }

    private void processTask(CrawlRequest task)
    {
        if (task.ignoreRobots)
        {
            robot = new RobotsParser("");
        } else {
            try
            {
                URL robotsURL = new URL(task.urlToCrawl.toString() + "/robots.txt");
                byte [] robotsTxt = Downloader.downloadUrl(robotsURL);
                if (null == robotsTxt) robot = new RobotsParser("");
                else robot = new RobotsParser(new String(robotsTxt));
            } catch (MalformedURLException e) {
                Logger.error("Could not create robots.txt URL");
                robot = new RobotsParser("");
            }
        }

        Resource crawlUrlAsResource = new Resource();
        crawlUrlAsResource.url = task.urlToCrawl;

        if (robot.checkURLAllowed(task.urlToCrawl))
            queue.enqueueToDownload(crawlUrlAsResource);

        queue.waitUntilFinished();

        Logger.debug("Tasked finished, releasing: " + task.urlToCrawl);
        task.progress = CrawlRequest.PROGRESS_FINISHED;
        parent.taskMutex.unregister();
    }

    @Override
    public void run() {
        while (true) {
            try {
                currentRequest = parent.dequeue();
                processTask(currentRequest);

            } catch (InterruptedException e) {
                Logger.error("Thread interrupted. Stopping");
                break;
            } catch (Exception e) {
                Logger.error("Got exception while handling task");
                e.printStackTrace();
            }

        }
    }

    public boolean alreadyDownloaded(URL url) {
        return downloaded.contains(url.toString());
    }

    public void markAsDownloaded(URL url) {
        downloaded.add(url.toString());
    }
}
