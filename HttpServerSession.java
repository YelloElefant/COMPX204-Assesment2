// threaded http server seassion

import java.io.*;
import java.net.*;
import java.nio.file.NoSuchFileException;

public class HttpServerSession extends Thread {
    private Socket socket;

    public HttpServerSession(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());

            // read http request
            String line = in.readLine();
            String[] request = line.split(" ");
            String method = request[0];
            String fileRequested = request[1];

            // get host request
            String host = in.readLine().split(" ")[1].split(":")[0];

            // get client ip address
            String clientIpAddress = socket.getInetAddress().getHostAddress();

            // change this to a switch statement
            if (fileRequested.equals("/")) {
                fileRequested = "/index.html";
            }

            System.out.println(clientIpAddress + " Requested: " + fileRequested);

            // get file extension from fileRequested
            String fileExtension = fileRequested.substring(fileRequested.lastIndexOf(".") + 1);

            // get content type
            String contentType = getContentType(fileExtension);

            // read in file
            File file = new File(host + fileRequested);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            // send http headers
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println("Content-Length: " + data.length);
            out.println();

            // send http body
            out.write(data);
            out.flush();

            socket.close();
        } catch (FileNotFoundException e) {
            try {
                PrintStream out = new PrintStream(socket.getOutputStream());
                out.println("HTTP/1.1 404 Not Found");
                out.println();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (NoSuchFileException e) {
            try {
                PrintStream out = new PrintStream(socket.getOutputStream());
                out.println("HTTP/1.1 415 Unsupported Media Type");
                out.println();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}
