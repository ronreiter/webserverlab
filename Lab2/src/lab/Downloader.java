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

    private ResourceQueue queue;
    private boolean shutdown = false;

    public Downloader(ResourceQueue queue) {
        this.queue = queue;
    }

    public void shutdown() {
        shutdown = true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Resource toDownload = queue.dequeueToDownload();

                if (toDownload == null) {
                    Logger.info("Downloader shutting down.");
                    return;
                }

                byte[] data = downloadUrl(toDownload.url);

                if (data == null) {
                    Logger.error("Couldn't download resource " + toDownload.url);
                    continue;
                }

                Resource resource = new Resource();
                resource.url = toDownload.url;


                resource.body = data;
                resource.length = data.length;


                Logger.info("Downloaded resource " + resource.url + " total bytes: " + resource.body.length);

                queue.enqueueToAnalyze(resource);

                if (shutdown) {
                    Logger.info("Downloader shutting down.");
                    return;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static byte[] downloadUrl(URL toDownload) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Logger.info("Downloading url " + toDownload.toString());

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

        Logger.info("Finished downloading url " + toDownload.toString());
        return outputStream.toByteArray();
    }

    // unit test
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        ResourceQueue queue = new ResourceQueue(1);

        Resource res = new Resource();
        res.url = new URL("http://www.ynet.co.il");
        queue.enqueueToDownload(res);

        Downloader downloader = new Downloader(queue);
        downloader.shutdown();
        downloader.run();

        assert queue.dequeueToAnalyze().body != null;

    }
}
