import java.net.*;
import java.io.*;

public class Dataserver{



	public void opensock(){
		try{
			ServerSocket sock =new ServerSocket(6100);

			while(true){
				Socket client =sock.accept();

				PrintWriter pout =new PrintWriter(client.getOutputStream(),true);
				pout.println(new java.util.Date().toString());

				client.close();

			}
		}
		catch(IOException ioe){
			System.err.println(ioe);
		}
	}
	
}


