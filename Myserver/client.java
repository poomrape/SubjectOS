import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit;


public class client{
	public static void main(String[] args){
		while(true){
		try{
			Socket sock =new Socket("127.0.0.1",6100);


			InputStream in =sock.getInputStream();
			BufferedReader bin =new BufferedReader(new InputStreamReader(in));

			String line;
			while((line =bin.readLine()) != null){
				System.out.println(line);

			}
			sock.close();

		}
		catch(IOException ioe){
			System.err.println(ioe);
		}
		 try {
 		   TimeUnit.SECONDS.sleep(1); // Pause for 5 seconds
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		} 
		}
	}
}
