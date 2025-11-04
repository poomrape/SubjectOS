import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) {
        try (Socket sock = new Socket("127.0.0.1", 6100)) {
            System.out.println("Connected to server.");

            // เตรียมอ่านข้อมูลจาก server
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            String line;
			while(true){
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
		}
		}catch (IOException ioe) {
            System.err.println("Client error: " + ioe.getMessage());
        }
    }
}


