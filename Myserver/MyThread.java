import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.ServerSocket;



class MyThread1 extends Thread{
    ServerSocket Sock ;

    MyThread1(ServerSocket Sock_n){
        this.Sock =Sock_n;
    }

    public void run(){
        Dataserver proc =new Dataserver();
        System.out.println("Myserver Thread 1 Running");
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        System.out.println("JVM Name = " + jvmName);
        long pid = Long.valueOf(jvmName.split("@")[0]);
        System.out.println("JVM PID  = " + pid);
        proc.opensock(Sock);

    }
    
}
class MyThread2 extends Thread{
    ServerSocket Sock ;

    MyThread2(ServerSocket Sock_n){
        this.Sock =Sock_n;
    }

    public void run(){
        Dataserver proc =new Dataserver();
        System.out.println("Myserver Thread 2 Running");
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        System.out.println("JVM Name = " + jvmName);
        long pid = Long.valueOf(jvmName.split("@")[0]);
        System.out.println("JVM PID  = " + pid);
        proc.opensock(Sock);

    }
    
}
