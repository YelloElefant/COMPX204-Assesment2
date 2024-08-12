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
    private List<String> requestHeaders = new ArrayList<String>();

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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());
            String responseCode = "200 OK";

            // read http request headers
            String line = in.readLine();
            while (line != null && !line.isEmpty()) {
                requestHeaders.add(line);
                line = in.readLine();
            }

            // read http request
            String[] request = requestHeaders.get(0).split(" ");
            // String method = request[0];
            String fileRequested = request[1];
            // get file extension from fileRequested
            String fileExtension = fileRequested.substring(fileRequested.lastIndexOf(".") + 1);
            // get content type
            String contentType = getContentType(fileExtension);
            // get host request
            String host = requestHeaders.get(1).split(" ")[1].split(":")[0];
            // get client ip address
            String clientIpAddress = socket.getInetAddress().getHostAddress();

            // get get parameters
            String getParameters = "";
            if (fileRequested.contains("?")) {
                getParameters = fileRequested.substring(fileRequested.indexOf("?") + 1);
                fileRequested = fileRequested.substring(0, fileRequested.indexOf("?"));
            }
            if (getParameters.equals("")) {
                getParameters = "none";
            }
            System.out.println(getParameters);

            // check for specific files and re direct fileRequested
            if (fileRequested.equals("/")) {
                fileRequested = "/index.html";
            } else if (fileRequested.equals("/favicon.ico")) {
                host = ".";
                fileRequested = "/picture.jpg";
            }

            // check for php file
            if (fileExtension.equals("php")) {
                String param = getParameters;
                String scriptName = host + fileRequested;
                String phpOutput = execPHP(scriptName, param);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html");
                out.println("Content-Length: " + phpOutput.length());
                out.println("Server: YelloElefant-HttpServer");
                out.println();
                out.println(phpOutput);
                out.flush();
                System.out
                        .println("Request from " + getHostName(clientIpAddress) + " for " + host + fileRequested + " - "
                                + responseCode);
                return;
            }

            // set up file input stream
            File file = new File(host + fileRequested);
            FileInputStream fis = null;
            byte[] data = new byte[(int) file.length()];

            // read in file and change response code if error
            try {
                fis = new FileInputStream(file);
                fis.read(data);
                fis.close();
            } catch (FileNotFoundException e) {
                responseCode = "404 Not Found";
            }

            // send http headers
            out.println("HTTP/1.1 " + responseCode);
            out.println("Content-Type: " + contentType);
            out.println("Content-Length: " + data.length);
            out.println("Server: YelloElefant-HttpServer");
            out.println();

            // send http body
            out.write(data);
            out.flush();
            // print request to console

            System.out
                    .println("Request from " + getHostName(clientIpAddress) + " for " + host + fileRequested + " - "
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
            case "json" -> "application/json";
            case "pdf" -> "application/pdf";
            case "xml" -> "application/xml";
            case "zip" -> "application/zip";
            case "mp3" -> "audio/mpeg";
            case "mp4" -> "video/mp4";
            case "php" -> "text/html";
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

    public String execPHP(String scriptName, String param) {
        StringBuilder output = new StringBuilder(); // Declare and initialize the output variable
        try {
            String line;
            ProcessBuilder processBuilder = new ProcessBuilder("php", scriptName, param);
            Process p = processBuilder.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                output.append(line);
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return output.toString();
    }
}
