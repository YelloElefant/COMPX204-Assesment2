import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
// import sleep 
import static java.lang.Thread.sleep;

public class PhpFpmRequest {

   public static void main(String[] args) {
      try (Socket socket = new Socket("192.168.1.32", 4545)) {
         OutputStream outputStream = socket.getOutputStream();
         InputStream inputStream = socket.getInputStream();

         // Step 1: Send FCGI_BEGIN_REQUEST
         sendBeginRequest(outputStream);

         // Step 2: Send FCGI_PARAMS
         sendFcgiParams(outputStream, "SCRIPT_FILENAME", "/var/www/html/gay.php");
         sendFcgiParams(outputStream, "REQUEST_METHOD", "GET");
         sendFcgiParams(outputStream, "QUERY_STRING", ""); // For GET, this can be empty or the actual query string
         sendFcgiParams(outputStream, "CONTENT_LENGTH", "0");
         sendFcgiParams(outputStream, "", ""); // Terminator for FCGI_PARAMS

         // Step 3: Send FCGI_STDIN (empty for GET requests)
         sendFcgiStdin(outputStream, "");
         sleep(5000);
         // Step 4: Read the response
         byte[] buffer = new byte[4096];
         int bytesRead;
         StringBuilder response = new StringBuilder();
         while ((bytesRead = inputStream.read(buffer)) != -1) {
            response.append(new String(buffer, 0, bytesRead));
         }
         System.out.println("Response:\n" + response.toString());

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static void sendBeginRequest(OutputStream outputStream) throws Exception {
      ByteBuffer beginRequest = ByteBuffer.allocate(16);
      beginRequest.put((byte) 1); // FCGI version
      beginRequest.put((byte) 1); // FCGI_BEGIN_REQUEST
      beginRequest.putShort((short) 1); // Request ID
      beginRequest.putShort((short) 8); // Content length
      beginRequest.put((byte) 0); // Padding length
      beginRequest.put((byte) 0); // Reserved
      beginRequest.putShort((short) 1); // Role: FCGI_RESPONDER
      beginRequest.put((byte) 0); // Flags
      beginRequest.put(new byte[5]); // Reserved bytes
      outputStream.write(beginRequest.array());
   }

   private static void sendFcgiParams(OutputStream outputStream, String name, String value) throws Exception {
      int nameLength = name.length();
      int valueLength = value.length();

      ByteBuffer params = ByteBuffer.allocate(8 + nameLength + valueLength);
      params.put((byte) 1); // FCGI version
      params.put((byte) 4); // FCGI_PARAMS
      params.putShort((short) 1); // Request ID
      params.putShort((short) (nameLength + valueLength)); // Content length
      params.put((byte) 0); // Padding length
      params.put((byte) 0); // Reserved
      params.put(name.getBytes());
      params.put(value.getBytes());
      outputStream.write(params.array());
   }

   private static void sendFcgiStdin(OutputStream outputStream, String content) throws Exception {
      ByteBuffer stdin = ByteBuffer.allocate(8 + content.length()); // Header + Content length
      stdin.put((byte) 1); // FCGI version
      stdin.put((byte) 5); // FCGI_STDIN
      stdin.putShort((short) 1); // Request ID
      stdin.putShort((short) content.length()); // Content length
      stdin.put((byte) 0); // Padding length
      stdin.put((byte) 0); // Reserved
      stdin.put(content.getBytes());
      outputStream.write(stdin.array());

      // Send empty FCGI_STDIN to signal end
      ByteBuffer stdinEnd = ByteBuffer.allocate(8);
      stdinEnd.put((byte) 1); // FCGI version
      stdinEnd.put((byte) 5); // FCGI_STDIN
      stdinEnd.putShort((short) 1); // Request ID
      stdinEnd.putShort((short) 0); // Content length
      stdinEnd.put((byte) 0); // Padding length
      stdinEnd.put((byte) 0); // Reserved
      outputStream.write(stdinEnd.array());
   }
}
