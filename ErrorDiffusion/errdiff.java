import java.awt.image.BufferedImage;
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
					
				}else{
					image[i][j] =(byte)0;
				}
		/* 	error[i][j] = wr.getSample(i, j, 0) - (255*image[i][j]); */
		}



		}





		
			

	}





}
