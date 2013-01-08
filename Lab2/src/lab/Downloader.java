package lab;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: ron
 * Date: 1/5/13
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Downloader implements Runnable {

    private ResourceQueue analyzeQueue;
    private ResourceQueue downloadQueue;
    private boolean shutdown = false;

    public Downloader(ResourceQueue analyzeQueue, ResourceQueue downloadQueue) {
        this.analyzeQueue = analyzeQueue;
        this.downloadQueue = downloadQueue;
    }

    public void shutdown() {
        shutdown = true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Resource toDownload = downloadQueue.dequeue();

                byte[] data = downloadUrl(toDownload.url);

                Resource resource = new Resource();
                resource.url = toDownload.url;
                resource.body = data;

                Logger.info("Downloaded resource " + resource.url + " total bytes: " + resource.body.length);

                if (shutdown) {
                    return;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private byte[] downloadUrl(URL toDownload) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = toDownload.openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("Can't download URL: " + toDownload.toString());
            return null;
        }

        return outputStream.toByteArray();
    }

    // unit test
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        ResourceQueue toAnalyze = new ResourceQueue(1);
        ResourceQueue toDownload = new ResourceQueue(0);

        Resource res = new Resource();
        res.url = new URL("http://www.ynet.co.il");
        toDownload.enqueue(res);

        Downloader downloader = new Downloader(toAnalyze, toDownload);
        downloader.shutdown();
        downloader.run();

        assert toAnalyze.dequeue().body != null;

    }
}
