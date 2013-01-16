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
        boolean shouldParse = true;

        // parse robots file:
        for (String line : robotsFile.split("\n")) {
            // use the robot data for all robots and for our robot specifically
            if (line.startsWith("User-agent:")) {
                if (line.split(":")[1].trim().compareTo("*") == 0 ||
                    line.split(":")[1].trim().compareTo(ConfigManager.getInstance().getRobotName()) == 0) {
                    Logger.debug("Robot found matching User-Agent: " + line.split(":")[1].trim());
                    shouldParse = true;
                } else
                {
                    Logger.debug("Robot found un-matching User-Agent: " + line.split(":")[1].trim() + ". Which is not " + ConfigManager.getInstance().getRobotName());
                    shouldParse = false;
                }
            }
            if (shouldParse && line.startsWith("Disallow:")) {
                Logger.debug("ROBOT: Adding " + line.trim() + " to disallowed list");
                disallowedPaths.add(line.split(":")[1].trim());
            } else if (shouldParse && line.startsWith("Allow:")) {
                Logger.debug("ROBOT: Adding " + line + " to allowed list");
                allowedPaths.add(line.split(":")[1].trim());
            }
        }
    }

    public boolean checkURLAllowed(URL url) {

        String urlPath = url.getPath();
        // handle root directory disallowed:
        if (urlPath == "") urlPath = "/";

        // First check if the URL has an "Allow:" entry - if so then permit
        for (String path: allowedPaths) {
            if (urlPath.startsWith(path)) {
                return true;
            }
        }

        // If it's not in the Allowed then check if it's Disallowed. if so block.
        for (String path : disallowedPaths) {
            if (urlPath.startsWith(path)) {
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
