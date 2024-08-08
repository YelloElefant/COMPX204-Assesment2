// http server java using HttpServerSession class

import java.io.*;
import java.net.*;

public class HttpServer {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Listening for connection on port 8080 ....");
            while (true) {
                Socket socket = server.accept();
                HttpServerSession session = new HttpServerSession(socket);
                session.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
