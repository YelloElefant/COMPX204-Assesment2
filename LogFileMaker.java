import java.io.*;

public class LogFileMaker {
   private String logFilePath = "";

   public LogFileMaker(String logFilePath) {
      this.logFilePath = HttpServer.logFilePath;
   }

   public void run() {
      try {
         File file = new File("logs/log.log");
         if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
         } else {
            System.out.println("File already exists.");
         }
      } catch (IOException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();

      }
   }
}
