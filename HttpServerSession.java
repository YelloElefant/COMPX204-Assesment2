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
    /**
     * Socket for connection to client (http request)
     */
    private Socket socket;
    private PrintStream out;
    private BufferedReader in;
    private String host;

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

            // // parse request headers
            // parseRequestHeaders();
            HttpServerRequest httpServerRequest = new HttpServerRequest();

            do {
                String line = in.readLine();
                httpServerRequest.process(line);

                if (line.isEmpty()) {
                    break;
                }
                // System.out.println(line);
            } while (httpServerRequest.isComplete() == false);

            // get file requested using the request map
            String fileRequested = httpServerRequest.getPath();

            // get host request
            host = httpServerRequest.getHost();

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

            // get file extension from fileRequested
            String fileExtension = fileRequested.substring(fileRequested.lastIndexOf(".") + 1);
            // get content type
            String contentType = getContentType(fileExtension);

            // check for specific files and re direct fileRequested
            if (fileRequested.equals("/")) {
                fileRequested = "/index.html";
            } else if (fileRequested.equals("/favicon.ico")) {
                host = ".";
                fileRequested = "/picture.jpg";
            }

            // set up file input stream
            File file = new File(host + fileRequested);
            byte[] data = new byte[(int) file.length()];

            // check for php file
            if (fileExtension.equals("php")) {
                String param = getParameters;
                String scriptName = host + fileRequested;
                data = execPHP(scriptName, param).getBytes();
                if (data.length == 0) {
                    throw new FileNotFoundException();
                }
            } else {
                // read in file and change response code if error
                data = readFile(file);
            }

            // respond to client
            respond(responseCode, contentType, data);

            // print request to console
            String message = "Request from " + getHostName(clientIpAddress) + " for " + host + fileRequested + " - "
                    + responseCode + " - " + new Date() + " - " + httpServerRequest.getMethod();
            System.out.println(message + " - " + getParameters);
            log(message);

        } catch (FileNotFoundException e) {
            try {
                File file = new File(host + "/404.html");
                byte[] data = readFile(file);
                respond("404 Not Found", "text/html", data);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * Executes a php script
     * using the php command line interface
     * 
     * @param scriptName php script name
     * @param param      GET parameters
     * @return output from php script
     */
    private String execPHP(String scriptName, String param) {
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

    /**
     * Respond to http request
     * 
     * @param responseCode response code
     * @param contentType  content type
     * @param data         data to send
     */
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

    private void log(String message) {
        try {
            FileWriter fileWriter = new FileWriter(HttpServer.logFilePath, true);
            fileWriter.write(message + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
