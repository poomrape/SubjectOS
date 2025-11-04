import java.io.*;
import java.net.*;

class MyThread extends Thread {
    private Socket client;


    public MyThread(Socket client) {
        this.client = client;
        }

    @Override
    public void run() {
        System.out.println("Thread started "+ Thread.currentThread().getName());
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        ) {
            while(true){
            out.println("Server time: " + new java.util.Date().toString());
            try {
               Thread.sleep(1000); 
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            }
            }

        } catch (IOException e) {
            System.err.println("Error with Client #"  + ": " + e.getMessage());
        }
    }
}
