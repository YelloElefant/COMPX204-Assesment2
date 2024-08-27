import java.util.HashMap;
import java.util.Map;

public class HttpServerRequest {
   private String method;
   private String path;
   private String version;
   private Map<String, String> headers = new HashMap<>();
   private int check = 0;

   public void process(String request) {
      if (request.equals("") || request == null) {
         return;
      }
      if (request.contains("GET")) {
         String[] lineSplit = request.split(" ");
         this.method = lineSplit[0];
         this.path = lineSplit[1];
         this.version = lineSplit[2];
         return;
      }

      String[] header = request.split(": ");
      if (header.length == 2) {
         headers.put(header[0], header[1]);
         check++;
      }
   }

   public boolean isComplete() {
      return check == 8;
   }

   public String getMethod() {
      return method;
   }

   public String getPath() {
      return path;
   }

   public String getVersion() {
      return version;
   }

   public String getHeader(String headerName) {
      return headers.get(headerName);
   }

   public String getHost() {
      return headers.get("Host").split(":")[0];
   }

   public String getUserAgent() {
      return headers.get("User-Agent");
   }

   public String getAccept() {
      return headers.get("Accept");
   }

   public String getAcceptLanguage() {
      return headers.get("Accept-Language");
   }

   public String getAcceptEncoding() {
      return headers.get("Accept-Encoding");
   }

   public String getConnection() {
      return headers.get("Connection");
   }

   public String getUpgradeInsecureRequests() {
      return headers.get("Upgrade-Insecure-Requests");
   }

   public String getContentType() {
      return headers.get("Content-Type");
   }

   public String getContentLength() {
      return headers.get("Content-Length");
   }
}