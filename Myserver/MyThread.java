
import java.net.ServerSocket;



class MyThread extends Thread{
    ServerSocket Sock ;

    MyThread(ServerSocket Sock_n){
        this.Sock =Sock_n;
    }

    public void run(){
        Dataserver proc =new Dataserver();
        System.out.println("Myserver Thread Running");
        System.out.println("Current thread name: " + Thread.currentThread().getName());

        proc.opensock(Sock);
    }
    
}

