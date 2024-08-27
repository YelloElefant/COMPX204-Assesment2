// Alexander Trotter 1644272

import java.util.HashMap;
import java.util.Map;

/**
 * this class handles the http requests, will parse them into fields and has
 * getters for common headers used
 */
public class HttpServerRequest {
   /**
    * the method of the request
    */
   private String method;

   /**
    * the file path of the request
    */
   private String path;

   /**
    * the http version used by client
    */
   private String version;

   /**
    * hashmap of all other headers with key as the name of header and value being
    * the data in the header
    */
   private Map<String, String> headers = new HashMap<>();

   /**
    * counter to count how man headers have been parsed, is used to check if the
    * request is parsed enough to return
    */
   private int check = 0;

   /**
    * this is the main method for the class, it takes the request header line and
    * pareses the data out of it
    * 
    * @param request the request header line
    */
   public void process(String request) {
      // checks for a null line or empty line
      if (request == null || request.equals("")) {
         return;
      }

      // gets the mthod, path and version if it is the verb header
      if (request.contains("GET")) {
         String[] lineSplit = request.split(" ");
         this.method = lineSplit[0];
         this.path = lineSplit[1];
         this.version = lineSplit[2];
         return;
      }

      // splits the line at the seperator characters and puts into hashmap as long as
      // the header has 2 parts key and value
      String[] header = request.split(": ");
      if (header.length == 2) {
         headers.put(header[0], header[1]);
         check++;
      }
   }

   /**
    * finds out whether the request is parsed enough to finish and responsed to the
    * client
    * 
    * @return true if complete, false otherwise
    */
   public boolean isComplete() {
      return check == 8;
   }

   /**
    * gets the method of the request
    * 
    * @return the method of the request
    */
   public String getMethod() {
      return method;
   }

   /**
    * gets the file path requested
    * 
    * @return the file path requested
    */
   public String getPath() {
      return path;
   }

   /**
    * gets the http version of the client
    * 
    * @return the http version
    */
   public String getVersion() {
      return version;
   }

   /**
    * gets any other header that doesnt have a custom get method
    * 
    * @param headerName header to find in hashmap
    * @return the header if it exists
    */
   public String getHeader(String headerName) {
      return headers.get(headerName);
   }

   /**
    * gets the host of the request
    * 
    * @return the host of the request
    */
   public String getHost() {
      return headers.get("Host").split(":")[0];
   }

}