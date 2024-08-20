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

    private static String hostName = "";
    private static String hostIp = "";

    public static void main(String[] args) {
        try {
            System.out.println("Initializing server ....");
            initialize();

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
        } finally {
            System.out.println("Server shutting down ....");
        }

    }

    private static void initialize() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            hostIp = host.toString().split("/")[1];
            System.out.println("Host IP: " + hostIp);
            hostName = host.getHostName();
            System.out.println("Host Name: " + hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        File[] folders = new File[] { new File("127.0.0.1"), new File("localhost"), new File(hostName),
                new File(hostIp) };

        System.out.println("Checking for folders ....");

        for (File folder : folders) {
            if (!folder.exists()) {
                System.out.println("Creating folder: " + folder.getName());
                folder.mkdir();

                // create index.html file
                System.out.println("Creating index.html and 404.html files in " + folder.getName());
                File indexFile = new File(folder, "index.html");
                try {
                    indexFile.createNewFile();
                    FileWriter writer = new FileWriter(indexFile);
                    writer.write("<html><head><title>Index</title></head><body><h1>Welcome to " + folder.getName()
                            + "</h1></body></html>");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // create 404.html file
                File notFoundFile = new File(folder, "404.html");
                try {
                    notFoundFile.createNewFile();
                    FileWriter writer = new FileWriter(notFoundFile);
                    writer.write(
                            "<html><head><title>404 Not Found</title></head><body><h1>404 Not Found</h1></body></html>");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Initialization complete ....");
    }
}
