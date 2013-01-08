package lab;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer implements Runnable {
    public static final String[] IMAGE_EXTENSIONS = {"bmp", "jpg", "png", "gif", "ico"};
    public static final String[] VIDEO_EXTENSIONS = {"avi", "mpg", "mp4", "wmv", "mov", "flv", "swf"};
    public static final String[] DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"};

    private ResourceQueue queue;

    public Analyzer(ResourceQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Resource toAnalyze = queue.dequeueToAnalyze();
                if (toAnalyze == null) {
                    Logger.info("Closing analyzer.");
                    return;
                }

                List<URL> urls = null;
                try {
                    urls = parseLinks(toAnalyze.url, new String(toAnalyze.body, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Logger.error("Error formatting body.");
                    e.printStackTrace();
                    continue;
                }

                Logger.debug("Number of links parsed: " + urls.size());

                for (URL url : urls) {
                    Resource resource = new Resource();
                    resource.url = url;
                    resource.type = getURLType(url);

                    Logger.info("Adding URL to download queue: " + resource.url + " type: " + resource.type);

                    queue.enqueueToDownload(resource);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public List<URL> parseLinks(URL baseUrl, String html) {
        List<URL> links = new LinkedList<URL>();
        Pattern linkParser = Pattern.compile("<a.*?href\\s?=\\s?['\"](.*?)['\"].*?>");
        Pattern imgParser = Pattern.compile("<img.*?src\\s?=\\s?['\"](.*?)['\"].*?>");

        Matcher linkMatcher = linkParser.matcher(html);
        if (linkMatcher.find()) {
            try {
                links.add(relativeToAbsoluteLink(baseUrl, linkMatcher.group(1)));
            } catch (MalformedURLException e) {
                Logger.debug("malformed URL: " + linkMatcher.group(1));
            }
        }

        Matcher imgMatcher = imgParser.matcher(html);
        if (imgMatcher.find()) {
            try {
                links.add(relativeToAbsoluteLink(baseUrl, imgMatcher.group(1)));
            } catch (MalformedURLException e) {
                Logger.debug("malformed image: " + imgMatcher.group(1));
            }
        }

        return links;
    }

    public URL relativeToAbsoluteLink(URL baseUrl, String relativeLink) throws MalformedURLException {
        if (relativeLink.startsWith("http")) {
            return new URL(relativeLink);
        }

        String urlString = baseUrl.getProtocol() + "://" + baseUrl.getHost() + new File(baseUrl.getPath(), relativeLink).toString();
        return new URL(urlString);
    }

    public int getURLType(URL url) {
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
        return Resource.TYPE_OTHER;
    }

    // unit test
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        ResourceQueue queue = new ResourceQueue(1);
        Resource res = new Resource();
        res.body = "<a href='bar.html'>blat</a> blat <img src='http://www.google.com/image.png'/>".getBytes();
        res.url = new URL("http://www.example.com/foo");
        queue.enqueueToAnalyze(res);

        Analyzer analyzer = new Analyzer(queue);
        analyzer.run();

        assert queue.dequeueToDownload().url.toString().equals("http://www.example.com/foo/bar.html");
        assert queue.dequeueToDownload().url.toString().equals("http://www.google.com/image.png");

    }


}
