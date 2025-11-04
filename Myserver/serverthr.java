import java.io.*;
import java.net.*;

public class serverthr {
    public static void main(String[] args) throws InterruptedException {
        try (ServerSocket sock = new ServerSocket(6100)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                // รอรับการเชื่อมต่อจาก client ใหม่
                Socket client = sock.accept();
                    System.out.println("New client connected: " + client.getInetAddress().getHostAddress());
                    MyThread t = new MyThread(client);
                    t.start(); // เริ่ม thread
                

        } }catch (IOException ioe) {
            System.err.println("Server error: " + ioe.getMessage());
        }
    }
}

