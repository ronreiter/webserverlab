package lab;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ron
 * Date: 1/5/13
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Analyzer implements Runnable {
    public static final int URL_TYPE_OTHER = 1;
    public static final int URL_TYPE_IMAGE = 2;
    public static final int URL_TYPE_VIDEO = 3;
    public static final int URL_TYPE_DOCUMENT = 4;

    public static final String[] IMAGE_EXTENSIONS = {"bmp", "jpg", "png", "gif", "ico"};
    public static final String[] VIDEO_EXTENSIONS = {"avi", "mpg", "mp4", "wmv", "mov", "flv", "swf"};
    public static final String[] DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"};

    public URL baseUrl = null;

    @Override
    public void run() {
        try {
            baseUrl = new URL("http://www.example.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public List<URL> parseLinks(String html) {
        List<URL> links = new LinkedList<URL>();
        Pattern linkParser = Pattern.compile("<a.*?href\\s?=\\s?['\"](.*?)['\"].*?>");
        Pattern imgParser = Pattern.compile("<img.*?src\\s?=\\s?['\"](.*?)['\"].*?>");

        Matcher linkMatcher = linkParser.matcher(html);
        if (linkMatcher.find()) {
            try {
                links.add(getURL(linkMatcher.group(0)));
            } catch (MalformedURLException e) {
                Logger.debug("malformed URL: " + linkMatcher.group(0));
            }
        }

        Matcher imgMatcher = imgParser.matcher(html);
        if (imgMatcher.find()) {
            try {
                links.add(getURL(imgMatcher.group(0)));
            } catch (MalformedURLException e) {
                Logger.debug("malformed image: " + imgMatcher.group(0));
            }
        }

        return null;
    }

    public URL getURL(String relativeLink) throws MalformedURLException {
        if (relativeLink.startsWith("http")) {
            return new URL(relativeLink);
        }

        return new URL(baseUrl, relativeLink);
    }

    public int getURLType(URL url) {
        for (String extension : IMAGE_EXTENSIONS) {
            if (url.toString().endsWith(extension)) {
                return URL_TYPE_IMAGE;
            }
        }
        for (String extension : VIDEO_EXTENSIONS) {
            if (url.toString().endsWith(extension)) {
                return URL_TYPE_VIDEO;
            }
        }
        for (String extension : DOCUMENT_EXTENSIONS) {
            if (url.toString().endsWith(extension)) {
                return URL_TYPE_DOCUMENT;
            }
        }
        return URL_TYPE_OTHER;
    }


}
