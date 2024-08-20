// threaded http server seassion

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class is a thread that will handle the http request and send the
 * response
 * 
 * @see java.io
 * @see java.net
 * @version 1.0
 * @author YelloElefant
 */
public class HttpServerSession extends Thread {
    private Socket socket;
    private PrintStream out;
    private BufferedReader in;

    private Map<String, String> requestHeaders = new HashMap<String, String>();

    /**
     * Constructor for HttpServerSession
     * this sets the context for the thread
     * 
     * @param socket socket for connection to client (http request)
     */
    public HttpServerSession(Socket socket) {
        this.socket = socket;
    }

    /**
     * Run method for thread
     * this method will handle the http request and send the response
     * 
     */
    @Override
    public void run() {
        try {
            // create input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            String responseCode = "200 OK";

            // parse request headers
            parseRequestHeaders();

            // get file requested using the request map
            String line = requestHeaders.get("METHOD");
            String fileRequested = line.split(" ")[0];

            // get host request
            String host = requestHeaders.get("Host").split(":")[0];

            // get client ip address
            String clientIpAddress = socket.getInetAddress().getHostAddress();

            // check for specific files and re direct fileRequested
            if (fileRequested.equals("/")) {
                fileRequested = "/index.html";
            } else if (fileRequested.equals("/favicon.ico")) {
                host = ".";
                fileRequested = "/picture.jpg";
            }

            // get file extension from fileRequested
            String fileExtension = fileRequested.substring(fileRequested.lastIndexOf(".") + 1);

            // get content type
            String contentType = getContentType(fileExtension);

            // set up file input stream
            File file = new File(host + fileRequested);
            FileInputStream fis = null;
            byte[] data = new byte[(int) file.length()];

            // read in file and change response code if error
            try {
                data = readFile(file);
            } catch (Exception e) {
                responseCode = "404 Not Found";
                fileRequested = "/404.html";
                file = new File(host + fileRequested);
                data = readFile(file);
            }

            // respond to client
            respond(responseCode, contentType, data);

            // print request to console
            System.out.println("Request from " + getHostName(clientIpAddress) + " for " + host + fileRequested + " - "
                    + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] readFile(File file) throws Exception {
        byte[] data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();
        return data;
    }

    /**
     * Get content type based on file extension
     * 
     * @param fileExtension file extension
     * @return content type
     */
    private String getContentType(String fileExtension) {
        return switch (fileExtension) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "text/javascript";
            case "png" -> "image/png";
            case "jpg" -> "image/jpg";
            case "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "ico" -> "image/x-icon";
            default -> "error";
        };
    }

    /**
     * Get host name from ip address if possible
     * if not possible will return ip address given
     * if ip address is a loopback/localhost will return "localhost"
     * 
     * 
     * @param ip ip address
     * @return host name
     */
    private String getHostName(String ip) {
        // check for localhost or loopback
        if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1")) {
            return "localhost";
        }

        // get host name from ip address if possible
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return inetAddress.getHostName().split("\\.")[0];
        } catch (UnknownHostException e) {
            return ip;
        }
    }

    private void respond(String responseCode, String contentType, byte[] data) {
        try {
            out.println("HTTP/1.1 " + responseCode);
            out.println("Content-Type: " + contentType);
            out.println("Content-Length: " + data.length);
            out.println("Server: YelloElefant-HttpServer");
            out.println();
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseRequestHeaders() {
        try {
            String line;

            while ((line = in.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                if (line.contains("GET")) {
                    requestHeaders.put("METHOD", line.split("GET")[1].trim());
                    continue;
                }

                String[] header = line.split(": ");
                requestHeaders.put(header[0], header[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
