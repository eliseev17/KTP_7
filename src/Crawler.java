import java.net.*;
import java.util.*;
import java.io.*;

public class Crawler {
    public static final String URL_PREFIX = "<a href=\"http";
    static LinkedList<URLDepthPair> findLink = new LinkedList<>();
    static LinkedList<URLDepthPair> resultLink = new LinkedList<>();

    public static void showResult(LinkedList<URLDepthPair> resultLink) {
        for (URLDepthPair c : resultLink)
            System.out.println("Depth : " + c.getDepth() + "\tLink : " + c.toString());
    }

    public static boolean check(LinkedList<URLDepthPair> resultLink, URLDepthPair pair) {
        boolean isAlready = true;
        for (URLDepthPair c : resultLink)
            if (c.toString().equals(pair.toString()))
                isAlready = false;
        return isAlready;
    }

    public static void request(PrintWriter out, URLDepthPair pair) {
        out.println("GET " + pair.getPath() + " HTTP/1.1");
        out.println("Host: " + pair.getHost());
        out.println("Connection: close");
        out.println();
        out.flush();
    }

    public static void searchURLs(String urlString, int maxDepth) {
        URLDepthPair urlPair = new URLDepthPair(urlString, 0);
        try {
            findLink.add(urlPair);
            while (!findLink.isEmpty()) {
                URLDepthPair currentPair = findLink.removeFirst();
                int depth = currentPair.getDepth();
                try {
                    Socket s = new Socket(currentPair.getHost(), 80);
                    s.setSoTimeout(1000);
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    request(out, currentPair);
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.indexOf(URL_PREFIX) > 0 && depth < maxDepth) {
                            boolean isLinkFound = false;
                            StringBuilder currentLink = new StringBuilder();
                            char c = line.charAt(line.indexOf(URL_PREFIX) + 9);
                            currentLink.append(c);
                            for (int i = line.indexOf(URL_PREFIX) + 10; c != '"' && i < line.length() - 1; i++) {
                                c = line.charAt(i);
                                if (c == '"') isLinkFound = true;
                                else currentLink.append(c);
                            }
                            if (isLinkFound) {
                                URLDepthPair newPair = new URLDepthPair(currentLink.toString(), depth + 1);
                                if (check(findLink, newPair)) {
                                    findLink.add(newPair);
                                }
                            }
                        }
                    }
                    s.close();

                    if (check(resultLink, currentPair)) resultLink.add(currentPair);
                } catch (IOException e) {
                }
            }
            showResult(resultLink);
        } catch (NullPointerException e) {
            System.out.println("Not Link");
        }
    }

    public static void main(String[] args) {
        args = new String[]{"http://government.ru/", "1"};
        if (args.length != 2) {
            System.out.println("usage: java Crawler <URL><depth>");
            System.exit(1);
        } else {
            try {
                // второй аргумент долржен быть целым числом, выполняем конвертацию
                Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                // если конвертация в int не удалась, печатаем ошибку
                System.out.println("usage: java Crawler <URL> <depth>");
                System.exit(1);
            }
        }
        searchURLs(args[0], Integer.parseInt(args[1]));
    }


//    public static void main(String[] args) {
//        args = new String[]{"http://government.ru/","2"};
//        int depth = 0;
//        // если в аргументах не два параметра, печатаем сообщение об ошибке
//        if (args.length != 2) {
//            System.out.println("j: java Crawler <URL> <depth>");
//            System.exit(1);
//        } else {
//            try {
//                // второй аргумент долржен быть целым числом, выполняем конвертацию
//                depth = Integer.parseInt(args[1]);
//            } catch (NumberFormatException nfe) {
//                // если конвертация в int не удалась, печатаем ошибку
//                System.out.println("j: java Crawler <URL> <depth>");
//                System.exit(1);
//            }
//        }
//        // список необработанных URLs.
//        LinkedList<URLDepthPair> pendingURLs = new LinkedList<>();
//        // список обработанных URLs.
//        LinkedList<URLDepthPair> processedURLs = new LinkedList<>();
//
//        URLDepthPair currentDepthPair = new URLDepthPair(args[0], 0);
//        pendingURLs.add(currentDepthPair);
//
//        ArrayList<String> seenURLs = new ArrayList<>();
//        seenURLs.add(currentDepthPair.getURL());
//
//        // пока список необработанных сайтов не пуст
//        // проходим по всем сайтам и добавляем все ссылки
//        while (pendingURLs.size() != 0) {
//            URLDepthPair depthPair = pendingURLs.pop();
//            processedURLs.add(depthPair);
//            int myDepth = depthPair.getDepth();
//            LinkedList<String> linksList = Crawler.getAllLinks(depthPair);
//            if (myDepth < depth) {
//                for (int i = 0; i < linksList.size(); i++) {
//                    String newURL = linksList.get(i);
//                    if (seenURLs.contains(newURL)) {
//                        continue;
//                    }
//                    else {
//                        URLDepthPair newDepthPair = new URLDepthPair(newURL, myDepth + 1);
//                        pendingURLs.add(newDepthPair);
//                        seenURLs.add(newURL);
//                    }
//                }
//            }
//        }
//        // печатаем все обработанные url
//        Iterator<URLDepthPair> iter = processedURLs.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next());
//        }
//    }
//
    private static LinkedList<String> getAllLinks(URLDepthPair myDepthPair) {
        LinkedList<String> URLs = new LinkedList<>();
        Socket sock;
        // пытаемся создать новый сокет
        try {
            sock = new Socket(myDepthPair.getHost(), 80);
        }
        catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
            return URLs;
        }
        catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
            return URLs;
        }
        try {
            sock.setSoTimeout(3000);
        }
        catch (SocketException exc) {
            System.err.println("SocketException: " + exc.getMessage());
            return URLs;
        }
        String docPath = myDepthPair.getPath();
        String webHost = myDepthPair.getHost();
        OutputStream outStream;

        // пытаемся получить выходной поток от сокета
        try {
            outStream = sock.getOutputStream();
        }
        catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return URLs;
        }

        // Initializes a PrintWriter. True means PrintWriter will flush after
        // every output.
        PrintWriter myWriter = new PrintWriter(outStream, true);

        // Send request to server.
        myWriter.println("GET " + docPath + " HTTP/1.1");
        myWriter.println("Host: " + webHost);
        myWriter.println("Connection: close");
        myWriter.println();

        // Initialize the InputStream.
        InputStream inStream;

        // Try to getInputStream from socket.
        try {
            inStream = sock.getInputStream();
        }
        // Catch IOException and return blank list.
        catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return URLs;
        }
        // Create a new InputStreamReader and BufferedReader to read lines
        // from the server.
        InputStreamReader inStreamReader = new InputStreamReader(inStream);
        BufferedReader BuffReader = new BufferedReader(inStreamReader);

        // Try to read line from Buffered reader.
        while (true) {
            String line;
            try {
                line = BuffReader.readLine();
            }
            // Catch IOException and return blank list.
            catch (IOException except) {
                System.err.println("IOException: " + except.getMessage());
                return URLs;
            }
            // Done reading document!
            if (line == null)
                break;

            // Variables to represent indices where the links begin and end as
            // well as current index.
            int beginIndex = 0;
            int endIndex = 0;
            int index = 0;

            while (true) {
                String URL_INDICATOR = "a href=\"";
                String END_URL = "\"";

                // Search for our start in the current line.
                index = line.indexOf(URL_INDICATOR, index);
                if (index == -1) // No more copies of start in this line
                    break;

                // Advance the current index and set to beginIndex.
                index += URL_INDICATOR.length();
                beginIndex = index;

                // Search for our end in the current line and set to endIndex.
                endIndex = line.indexOf(END_URL, index);
                index = endIndex;

                // Set the link to the substring between the begin index
                // and end index.  Add to our URLs list.
                String newLink = line.substring(beginIndex, endIndex);
                URLs.add(newLink);
            }
        }
        return URLs;
    }
}