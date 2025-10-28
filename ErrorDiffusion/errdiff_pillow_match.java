
import java.awt.image.*;
import javax.imageio.ImageIO;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.io.IOException;


public class errdiff_pillow_match { 
    static ArrayList<Long> sequential_times = new ArrayList<>();
    static ArrayList<Long> parallel_times = new ArrayList<>();
    public static  byte[]   ErrordiffuseSequential(BufferedImage grayImage, BufferedImage outputImage, int width, int height) {
        int[] pixelBuffer = new int[width * height];
        byte[] outputPixels = new byte[width * height];
        byte[] buffer = ((DataBufferByte) grayImage.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelBuffer[y * width + x] = buffer[y * width + x] & 0xFF;
            }
        }
        long start_time =  System.currentTimeMillis();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int bufferIndex = y * width + x;
                int oldPixel = pixelBuffer[bufferIndex]; // Get unsigned value from byte

                // Determine new pixel value (0 or 255) based on threshold
                int newPixel = (oldPixel <= 128) ? 0 : 255;

                // Set the output pixel directly. For TYPE_BYTE_BINARY, 0 is black, 255 (or any non-zero) is white.
                outputPixels[y * width + x] = (byte) newPixel;

                int err = oldPixel - newPixel;

                // Diffuse error to neighbors
                if (x + 1 < width)
                    pixelBuffer[bufferIndex + 1] += Math.floorDiv(err * 7 , 16);

                if (y + 1 < height && x > 0)
                    pixelBuffer[bufferIndex -1 + width] += Math.floorDiv(err * 3 , 16);

                if (y + 1 < height)
                    pixelBuffer[bufferIndex +width] += Math.floorDiv(err * 5 , 16);

                if (y + 1 < height && x + 1 < width)
                    pixelBuffer[bufferIndex+1+width] += Math.floorDiv(err * 1 , 16);
            }
        }
        long end_time =  System.currentTimeMillis();
           // System.out.println("Sequential time : " + (end_time - start_time) + " ms");
            sequential_times.add(end_time - start_time);
        return outputPixels;
    }


    public static byte[] ErrordiffuseParallel(BufferedImage grayImage, BufferedImage outputImage, int width, int height) throws InterruptedException {
        int[] pixelBuffer = new int[width * height];
        final byte[] outputPixels = new byte[width * height];
        final AtomicIntegerArray rowcomplete = new AtomicIntegerArray(height);
        byte[] buffer = ((DataBufferByte) grayImage.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelBuffer[y * width + x] = buffer[y * width + x] & 0xFF;
            }
        }
        int numThreads =  6;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        final CountDownLatch latch = new CountDownLatch(numThreads);
        int rowsPerThread = (height + numThreads - 1) / numThreads; // Ceiling division
        long start_time =  System.currentTimeMillis();
        for (int t = 0; t < numThreads; t++) {
            final int startrow = t * rowsPerThread;
            final int endrow = Math.min(startrow + rowsPerThread, height);
            if(startrow >= endrow){
                latch.countDown();
                continue;
            }
            executor.submit(() -> {
                for (int y = startrow; y < endrow; y++) {
                    if(y>0){
                        while(rowcomplete.get(y-1) ==0){
                            //busy wait
                            Thread.onSpinWait();
                        }
                    }  
                    for (int x = 0; x < width; x++) {
                        int bufferIndex = y * width + x;
                        int oldPixel = pixelBuffer[bufferIndex]; // Get unsigned value from byte

                        // Determine new pixel value (0 or 255) based on threshold
                        int newPixel = (oldPixel <= 128) ? 0 : 255;

                        // Set the output pixel directly. For TYPE_BYTE_BINARY, 0 is black, 255 (or any non-zero) is white.
                        outputPixels[y * width + x] = (byte) newPixel;

                        int err = oldPixel - newPixel;

                        // Diffuse error to neighbors
                        if (x + 1 < width)
                            pixelBuffer[bufferIndex + 1] += Math.floorDiv(err * 7 , 16);

                        if (y + 1 < height && x > 0)
                            pixelBuffer[bufferIndex -1 + width] += Math.floorDiv(err * 3 , 16);

                        if (y + 1 < height)
                            pixelBuffer[bufferIndex +width] += Math.floorDiv(err * 5 , 16);

                        if (y + 1 < height && x + 1 < width)
                            pixelBuffer[bufferIndex+1+width] += Math.floorDiv(err * 1 , 16);
                    }
                    rowcomplete.set(y, 1); // Mark this row as complete
                } latch.countDown();
                
            });
        }
        latch.await();  
        
        executor.shutdown();
        long end_time =  System.currentTimeMillis();
            //System.out.println("Parallel time : " + (end_time - start_time) + " ms");
            parallel_times.add(end_time - start_time);

            return outputPixels;
    }

    public static BufferedImage convertgrayscale(String filepath) throws IOException {
        try{
        BufferedImage originalImage = ImageIO.read(new File(filepath)); // Assume this could be RGB or Grayscale

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // Create a buffer to hold grayscale int values, explicitly converting if needed
            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            byte[]buffergraypixel = ((DataBufferByte) grayImage.getRaster().getDataBuffer()).getData();
            
            // If src is already grayscale, this effectively just copies the values.
            // If src is RGB, it converts using a common luminance formula.
            if(originalImage.getType() != BufferedImage.TYPE_BYTE_GRAY) {

                int pixelcount =0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = originalImage.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF; // Red component
                    int g = (rgb >> 8) & 0xFF;  // Green component
                    int b = rgb & 0xFF;   // Blue component
                    // Luminance formula (commonly used by Pillow for 'L' mode)
                    int grayValue = (r*19595 + g*38470 + b*7471 +32768 ) / 65536; // Equivalent to 0.299*r + 0.587*g + 0.114*b
                    buffergraypixel[pixelcount] = (byte) grayValue;
                    pixelcount++;
                }
            }
            return grayImage;
            }else{
                return originalImage;
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException, IOException {

            BufferedImage imagepro = convertgrayscale("pngegg.png");
            int width = imagepro.getWidth();
            int height =imagepro.getHeight();
            // saved Sequential Version
            /*byte[] outputPixels = ErrordiffuseSequential(imagepro, null, width, height);// Call the sequential version
            BufferedImage ditheredImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            byte[] ditheredData = ((DataBufferByte) ditheredImage.getRaster().getDataBuffer()).getData();
            System.arraycopy(outputPixels, 0, ditheredData, 0, outputPixels.length);
            File outputfile = new File("outputsequential.png");
            ImageIO.write(ditheredImage, "png", outputfile);
            System.out.println("Dithering complete. Output saved to outputsequential.png");

            //Saved Parallel Version
            byte[] outputPixelsParallel = ErrordiffuseParallel(imagepro, null, width, height);// Call the parallel version
            BufferedImage ditheredImageParallel = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            byte[] ditheredDataParallel = ((DataBufferByte) ditheredImageParallel.getRaster().getDataBuffer()).getData();
            System.arraycopy(outputPixelsParallel, 0, ditheredDataParallel, 0, outputPixelsParallel.length);
            File outputfileParallel = new File("outputparallel.png");
            ImageIO.write(ditheredImageParallel, "png", outputfileParallel);
            System.out.println("Dithering complete. Output saved to outputparallel.png");*/
            

            for  (int i=0;i<70;i++){
                ErrordiffuseSequential(imagepro, null, width, height);// Call the sequential version
            }
            for  (int i=0;i<70;i++){
                ErrordiffuseParallel(imagepro, null, width, height);// Call the parallel version
            }

            long sum_sequential_times = 0;
            long sum_parallel_times = 0;
            for (int i=2;i < sequential_times.size();i++){
                sum_sequential_times+=sequential_times.get(i);
                sum_parallel_times+=parallel_times.get(i);
                
            }
            System.out.println("Mean sequential time:"+sum_sequential_times/sequential_times.size());
            System.out.println("Mean parallel time:"+sum_parallel_times/parallel_times.size());      
            // Plotting the times
            XYChart chart = new XYChartBuilder()
                .width(800)
                .height(500)
                .title("Error Diffusion Execution Time Comparison")
                .xAxisTitle("Run")
                .yAxisTitle("Time (ms)")
                .build();
        ArrayList<Integer> runIndices = new ArrayList<>();
        for (int i = 2; i < sequential_times.size(); i++) {
            runIndices.add(i + 1);
        }

        chart.addSeries("Sequential", runIndices, sequential_times.subList(2,sequential_times.size()));
        chart.addSeries("Parallel", runIndices, parallel_times.subList(2,parallel_times.size()));

        // แสดงกราฟ
        new SwingWrapper<>(chart).displayChart();

        


    }

}

