package lab;


import java.io.IOException;
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

    ResourceQueue queue = new ResourceQueue();

    public CrawlTask(CrawlTaskPool parent) {
        this.parent = parent;
        this.analyzers = new LinkedList<Thread>();
        this.downloaders = new LinkedList<Thread>();

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
        this.downloaded = new HashSet<String>();

        if (task.ignoreRobots)
        {
            robot = new RobotsParser("");
        } else {
            try
            {
                URL robotsURL;
                if (task.urlToCrawl.getPort() == -1) {
                    robotsURL = new URL(task.urlToCrawl.getProtocol() + "://" + task.urlToCrawl.getHost() + "/robots.txt");
                } else {
                    robotsURL = new URL(task.urlToCrawl.getProtocol() + "://" + task.urlToCrawl.getHost() + ":" + task.urlToCrawl.getPort() + "/robots.txt");
                }

                byte [] robotsTxt = Downloader.downloadUrl(robotsURL);
                if (null == robotsTxt) robot = new RobotsParser("");
                else robot = new RobotsParser(new String(robotsTxt));
            } catch (MalformedURLException e) {
                Logger.error("Could not create robots.txt URL");
                robot = new RobotsParser("");
            }
        }

        Resource crawlUrlAsResource = new Resource();
        try {
            if (task.urlToCrawl.getPort() == -1) {
                crawlUrlAsResource.url = new URL(task.urlToCrawl.getProtocol() + "://" + task.urlToCrawl.getHost());
            } else {
                crawlUrlAsResource.url = new URL(task.urlToCrawl.getProtocol() + "://" + task.urlToCrawl.getHost() + ":" + task.urlToCrawl.getPort());
            }

        } catch (MalformedURLException e) {
            Logger.error("Can't get the root URL link of URL " + task.urlToCrawl);
            e.printStackTrace();
            return;
        }

        if (robot.checkURLAllowed(task.urlToCrawl))
            queue.enqueueToDownload(crawlUrlAsResource);

        queue.waitUntilFinished();

        Logger.debug("Tasked finished, releasing: " + task.urlToCrawl);
        task.progress = CrawlRequest.PROGRESS_FINISHED;
        parent.taskMutex.unregister("Crawler Task");

        ResultFileGenerator resultFileGenerator = new ResultFileGenerator(task.urlToCrawl);
        try {
            resultFileGenerator.writeFile(currentRequest);
        } catch (IOException e) {
            Logger.error("Couldn't write result file for url: " + task.urlToCrawl.toString());
            e.printStackTrace();
        }
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
