package lab;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer implements Runnable {
    public static final String[] IMAGE_EXTENSIONS = {"bmp", "jpg", "png", "gif", "ico"};
    public static final String[] VIDEO_EXTENSIONS = {"avi", "mpg", "mp4", "wmv", "mov", "flv", "swf"};
    public static final String[] DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"};

    private ResourceQueue queue;
    private CrawlTask parent;
    public Analyzer(ResourceQueue queue, CrawlTask parent) {
        this.queue = queue;
        this.parent = parent;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // dequeue a task and start working on it
                Resource toAnalyze = queue.dequeueToAnalyze();

                // if shutting down, we get null.
                if (toAnalyze == null) {
                    Logger.info("Analyzer shutting down.");
                    return;
                }

                // parse the URL and get the type (can also be done through the headers of the request
                toAnalyze.type = getURLType(toAnalyze.url);

                // done analyzing stuff which isn't a page
                if (toAnalyze.type != Resource.TYPE_PAGE) {
                    continue;
                }

                if (toAnalyze.body == null) {
                    continue;
                }

                // get the links to parse from the body
                Map<URL, String> urls = parseLinks(toAnalyze.url, new String(toAnalyze.body, "UTF-8"));

                if (urls == null) {
                    continue;
                }
                Logger.info("Number of links parsed from url " + urls.toString() + " is " + urls.size());

                // for each link, add it to the download queue if needed
                for (URL url : urls.keySet()) {
                    if (url == null) {
                        continue;
                    }

                    // only download links from the current domain
                    if (!url.getHost().equals(toAnalyze.url.getHost())) {
                        Logger.debug("Not downloading from " + url.getHost() + " because it is not in " + toAnalyze.url.getHost());
                        parent.currentRequest.domainsConnected.add(url.getHost());
                        continue;
                    }

                    // check if we're allowed to download this file according to our policy and the robots.txt file
                    if (parent != null && !parent.robot.checkURLAllowed(url)) {
                        Logger.debug("Not downloading URL " + url.toString() + " because it's disallowed by robots.txt");
                        continue;
                    }

                    // don't redownload files
                    if (parent.alreadyDownloaded(url)) {
                        Logger.debug("Not downloading URL " + url.toString() + " because it's already been downloaded.");
                        continue;
                    }

                    // mark we downloaded the URL, mark it as downloaded so we won't download it again
                    parent.markAsDownloaded(url);

                    // create a new resource and fill it with the details of the link we found
                    Resource resource = new Resource();
                    resource.url = url;
                    resource.type = getURLType(url);
                    resource.tag = urls.get(url);

                    // add the request to the crawl statistics page
                    parent.currentRequest.addStat(resource);

                    Logger.info("Adding URL to download queue: " + resource.url + " type: " + resource.type + " tag: " + resource.tag);

                    // enqueue it to the download queue (this is done only to pages)
                    queue.enqueueToDownload(resource);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (UnsupportedEncodingException e) {
                Logger.error("Error formatting body.");
                e.printStackTrace();
            } finally {
                queue.releaseResource();
            }
        }
    }

    public Map<URL, String> parseLinks(URL baseUrl, String html) {
        Map<URL, String> links = new HashMap<URL, String>();

        // simple regular expressions to match a tags and img tags
        Pattern linkParser = Pattern.compile("<a.*?href\\s?=\\s?['\"](.*?)['\"].*?>", Pattern.CASE_INSENSITIVE);
        Pattern imgParser = Pattern.compile("<img.*?src\\s?=\\s?['\"](.*?)['\"].*?>", Pattern.CASE_INSENSITIVE);

        // insert image tags to the result list
        Matcher imgMatcher = imgParser.matcher(html);
        while (imgMatcher.find()) {
            try {
                links.put(relativeToAbsoluteLink(baseUrl, imgMatcher.group(1)), "img");
            } catch (MalformedURLException e) {
                Logger.debug("malformed image: " + imgMatcher.group(1));
            }
        }

        // insert all other links to the result list
        Matcher linkMatcher = linkParser.matcher(html);
        while (linkMatcher.find()) {
            try {
                links.put(relativeToAbsoluteLink(baseUrl, linkMatcher.group(1)), "a");
            } catch (MalformedURLException e) {
                Logger.debug("malformed URL: " + linkMatcher.group(1));
            }
        }

        return links;
    }

    public URL relativeToAbsoluteLink(URL baseUrl, String relativeLink) throws MalformedURLException {
        // turn relative paths with a base URL to absolute, but also knows to accept absolute links
        if (relativeLink.startsWith("http")) {
            return new URL(relativeLink);
        }

        return new URL(baseUrl, relativeLink);
    }

    public int getURLType(URL url) {
        // returns the type of URL according to the suffix (extension)
        for (String extension : IMAGE_EXTENSIONS) {
            if (url.toString().endsWith(extension)) {
                return Resource.TYPE_IMAGE;
            }
        }
        for (String extension : VIDEO_EXTENSIONS) {
            if (url.toString().endsWith(extension)) {
                return Resource.TYPE_VIDEO;
            }
        }
        for (String extension : DOCUMENT_EXTENSIONS) {
            if (url.toString().endsWith(extension)) {
                return Resource.TYPE_DOCUMENT;
            }
        }
        return Resource.TYPE_PAGE;
    }

    // unit test
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        ResourceQueue queue = new ResourceQueue();
        Resource res = new Resource();
        res.body = "<a href='bar.html'>blat</a> blat <img src='http://www.google.com/image.png'/>".getBytes();
        res.url = new URL("http://www.example.com/foo");
        queue.enqueueToAnalyze(res);


        Analyzer analyzer = new Analyzer(queue, null);
        analyzer.run();

        assert queue.dequeueToDownload().url.toString().equals("http://www.example.com/foo/bar.html");
        assert queue.dequeueToDownload().url.toString().equals("http://www.google.com/image.png");

    }


}
