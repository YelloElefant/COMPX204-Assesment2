// http server java using HttpServerSession class

import java.io.*;
import java.net.*;

/**
 * This class is the main class for the http server
 * it will listen for connection on port 8080 and create a new thread for each
 * connection/request
 * 
 * @see java.io
 * @see java.net
 * @see HttpServerSession
 * @version 1.0
 * @author YelloElefant
 */
public class HttpServer {
    /**
     * Main method for http server
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            // create server socket on port 8080
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Listening for connection on port 8080 ....");

            // listen for connection and create a new thread for each connection (accept all
            // connections)
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
