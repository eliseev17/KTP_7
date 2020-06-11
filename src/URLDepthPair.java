import java.net.*;

public class URLDepthPair {

    private int currentDepth;
    private String currentURL;

    public URLDepthPair(String URL, int depth) {
        currentDepth = depth;
        currentURL = URL;
    }

    public String getURL() {
        return currentURL;
    }

    public int getDepth() {
        return currentDepth;
    }

    public String toString() {
        String stringDepth = Integer.toString(currentDepth);
        return stringDepth + '\t' + currentURL;
    }

    // A method which returns the docPath of the current URL.
    public String getPath() {
        try {
            URL url = new URL(currentURL);
            return url.getPath();
        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }

    // A method which returns the webHost of the current URL.
    public String getHost() {
        try {
            URL url = new URL(currentURL);
            return url.getHost();
        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            return null;
        }
    }
}