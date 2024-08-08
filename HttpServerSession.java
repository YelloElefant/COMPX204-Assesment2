// threaded http server seassion

import java.io.*;
import java.net.*;

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

            String contentType = "text/html";

            if (fileRequested.equals("/")) {
                fileRequested = "/index.html"; // default file
            } else if (fileRequested.equals("/favicon.ico")) {
                // return 404 for favicon
                out.println("HTTP/1.1 404 Not Found");
                out.println();
                socket.close();
                return;
            } else if (fileRequested.endsWith(".css")) {
                contentType = "text/css";
            } else if (fileRequested.endsWith(".js")) {
                contentType = "text/javascript";
            } else if (fileRequested.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileRequested.endsWith(".gif")) {
                contentType = "image/gif";
            }

            // read in html file
            File file = new File(fileRequested.substring(1));
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            // send http response
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println("Content-Length: " + data.length);
            out.println();
            out.write(data);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
