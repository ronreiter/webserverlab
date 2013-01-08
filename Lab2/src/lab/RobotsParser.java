package lab;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ron
 * Date: 1/5/13
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RobotsParser {
    List<String> disallowedPaths;
    List<String> allowedPaths;

    public RobotsParser(String robotsFile) {
        disallowedPaths = new LinkedList<String>();
        allowedPaths = new LinkedList<String>();

        for (String line : robotsFile.split("\n")) {
            if (line.startsWith("Disallow:")) {
                disallowedPaths.add(line.split(":")[1].trim());
            } else if (line.startsWith("Allow:")) {
                allowedPaths.add(line.split(":")[1].trim());
            }
        }
    }

    public boolean checkURLAllowed(URL url) {
        // First check if the URL has an "Allow:" entry - if so then permit
        for (String path: allowedPaths) {
            if (url.getPath().startsWith(path)) {
                return true;
            }
        }

        // If it's not in the Allowed then check if it's Disallowed. if so block.
        for (String path : disallowedPaths) {
            if (url.getPath().startsWith(path)) {
                    return false;
            }
        }

        return true;
    }

    // unit test
    public static void main(String[] args) throws MalformedURLException {
        RobotsParser parser = new RobotsParser(
            "User-agent: *\n" +
            "Disallow: /cgi-bin/\n" +
            "Disallow: /tmp/\n" +
            "Disallow: /junk/\n" +
            "Allow: /cgi-bin/index.html");

        assert parser.checkURLAllowed(new URL("http://www.google.com/allowed"));
        assert !parser.checkURLAllowed(new URL("http://www.google.com/cgi-bin/disallowed"));
        assert parser.checkURLAllowed(new URL("http://www.google.com/cgi-bin/index.html"));

    }
}
