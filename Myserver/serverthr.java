import java.net.ServerSocket;

import java.io.*;

public class serverthr {

    public static void main(String[] args){
        try{
            ServerSocket sock =new ServerSocket(6100);
            MyThread1 t1 =new MyThread1(sock);
            MyThread2 t2 =new MyThread2(sock);
            t1.start();
            t2.start();
        }
        catch(IOException ioe){
			System.err.println(ioe);
		}
    }
    
}
