import java.net.ServerSocket;

import java.io.*;

public class serverthr {

    public static void main(String[] args){
        try{
            ServerSocket sock =new ServerSocket(6100);
            MyThread t1 =new MyThread(sock);
            MyThread t2 =new MyThread(sock);
            MyThread t3 =new MyThread(sock);
            MyThread t4 =new MyThread(sock);

            t1.start();
            t2.start();
            t3.start();
            t4.start();
        }
        catch(IOException ioe){
			System.err.println(ioe);
		}
    }
    
}
