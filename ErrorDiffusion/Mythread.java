import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.awt.image.WritableRaster;
public class Mythread extends Thread {
    private Semaphore[] mutex;
    BufferedImage outputImage;
    private byte[][] error;
    private int Width;
    public int row;
    private int height;
    String threadName;

    public Mythread(Semaphore[] mutex,BufferedImage outputImage,byte[][] error, int startrow,int height, int width,String threadName) {
        super(threadName);
        this.threadName = threadName;
        this.mutex= mutex;
        this.outputImage = outputImage;
        this.height = height;
        this.error = error;
        this.row = startrow;
        this.Width = width;
    }

    public void run() {

        for (int row_index = row; row_index < height; row_index++) {
        try{
            mutex[row_index].acquire();
        }catch(InterruptedException e){
            System.out.println("Thread " + threadName + " interrupted.");
        }
        //System.out.println("Running " + threadName);
        for (int width  = 0;width  < Width ;width ++){
            if(width==0){
					if(width >= Width-1){
                        continue;
					}else{
						if(row_index >= height-1){
							outputImage.getRaster().setSample(width,row_index, 0, error[row_index][width+1]*7/16 );
						}else{
                			outputImage.getRaster().setSample(width,row_index, 0, error[row_index][width+1]*7/16 );
							outputImage.getRaster().setSample(width,row_index, 0, error[row_index+1][width]*7/16 );
							outputImage.getRaster().setSample(width,row_index, 0, error[row_index+1][width+1]*7/16 );
					}

				}
				}else{

					if(width >=Width-1){
						if(row_index >= height -1){
							continue;
						}
						outputImage.getRaster().setSample(width,row_index, 0, error[row_index+1][width-1]*7/16 );
						outputImage.getRaster().setSample(width,row_index, 0, error[row_index+1][width]*7/16 );
					}else{
						if(row_index >= height-1){
							outputImage.getRaster().setSample(width,row_index, 0, error[row_index][width+1]*7/16 );
						}else{
                			outputImage.getRaster().setSample(width,row_index, 0, error[row_index][width+1]*7/16 );
							outputImage.getRaster().setSample(width,row_index, 0, error[row_index+1][width-1]*7/16 );
							outputImage.getRaster().setSample(width,row_index, 0, error[row_index+1][width]*7/16 );
							outputImage.getRaster().setSample(width,row_index, 0, error[row_index+1][width+1]*7/16 );
					}

				}
				}
        }
        //System.out.println("Thread " + threadName + " exiting.");
        mutex[row_index].release();       
    }

    }
    
}
