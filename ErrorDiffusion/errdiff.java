import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.Graphics ;
class effdiff{
 
	public static void main(String[] args){
		BufferedImage inputImage =null;
		File inputfFile = new File("grayscale2.png");
		try{

			inputImage =ImageIO.read(inputfFile);

			if(inputImage ==null){
				System.out.print("Could not load Image");
				return ;
			}

			if(inputImage.getType() == BufferedImage.TYPE_BYTE_GRAY  ){
			    System.out.println("Image is already grayscale format \n");
				System.out.println("Image load success");
			}else{
				System.out.println("Image is not alraedy grayscale format \n");
				System.out.println(inputImage.getType());
				BufferedImage image = new BufferedImage(inputImage.getWidth(),inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);  
				Graphics g = image.getGraphics();  
				g.drawImage(inputImage, 0, 0, null);  
				g.dispose();  
				
				inputImage = image;
				System.out.println(inputImage.getType());

			}

		}catch(IOException e) {
			 System.err.println("Error loading image: " + e.getMessage());
		}
		WritableRaster wr = inputImage.getRaster();
		int width=inputImage.getWidth();
		int height=inputImage.getHeight(); 
		System.out.println("Scale dimension "+width+"X"+height);
		byte[][] image = new byte[height][width] ;
		byte[][] error =new byte[height][width];
		for( int i=0;i< height;i++){
			for(int j=0;j< width;j++){
				if(wr.getSample(j,i,0) >128 ){
					image[i][j]	= (byte)1;
					//System.out.println("white");
					
				}else{
					image[i][j] =(byte)0;
					//System.out.println("Black");
				}
				error[i][j] =(byte) ( image[i][j] == (byte)1 ? wr.getSample(j, i, 0)-255 : wr.getSample(j, i, 0)-0 ); 
				//System.out.println(error[i][j]);
			}
		}
		BufferedImage newImage = new BufferedImage(width, height,BufferedImage.TYPE_BYTE_GRAY);
		for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                newImage.getRaster().setSample(x, y, 0, image[y][x]*255);
            }
        }
		try {
            inputfFile = new File( "GFG.png");
            ImageIO.write(newImage, "png", inputfFile);
        } catch (IOException e) {
            System.out.println(e);
		}
	Semaphore[] rowLock = new Semaphore[height];
    for (int i = 0; i < height; i++) {
        rowLock[i] = new Semaphore(1);  // only one thread may work on this row at a time
    }

	BufferedImage outputImage =new BufferedImage(width, height,BufferedImage.TYPE_BYTE_GRAY);
		Mythread t1 = new Mythread(rowLock,outputImage,error,0,height,width,"Thread-1");
		Mythread t2 = new Mythread(rowLock,outputImage,error,0,height,width,"Thread-2");
		Mythread t3 = new Mythread(rowLock,outputImage,error,0,height,width,"Thread-3");
		Mythread t4 = new Mythread(rowLock,outputImage,error,0,height,width,"Thread-4");
		Mythread t5 = new Mythread(rowLock,outputImage,error,0,height,width,"Thread-5");
		long startTime = System.currentTimeMillis();
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		try{
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
			}catch(InterruptedException e){
				System.out.println("Thread interrupted.");
	}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total execution time: " + totalTime + " milliseconds");
		try {
			inputfFile = new File( "output.png");
			ImageIO.write(outputImage, "png", inputfFile);
		} catch (IOException e) {
			System.out.println(e);
		}
		System.out.println("Finished");


	}
}

