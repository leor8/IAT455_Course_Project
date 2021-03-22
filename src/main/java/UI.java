package main.java;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

public class UI extends JFrame implements Runnable {
    // **************** Variable declarations *********************//
    int width, height, padding;
    BufferedImage testImage;
    BasicStroke stroke;

//    private MarvinVideoInterface videoAdapter;
//    private MarvinImage imageIn, imageOut, imageBuffer;
//    private MarvinImagePanel videoPanelLeft, videoPanelRight;
    private VideoCapture inputVideo;
    private Mat frames;
    private static FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);

    // **************** End of Variable declarations **************//

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    //  Constructor for user interface
    public UI() {
        // Set the basic configurations of the frame
        this.setTitle("IAT455 Course Project Group 1");
        this.setVisible(true);

        // Load test image and video
        try{
            // Setting the attributes of test image
            testImage = ImageIO.read(new File("testImage.jpg"));
            width = testImage.getWidth()/2;
            height = testImage.getHeight()/2;

            // Setting the attributes of test video
            inputVideo = new VideoCapture("testVideo.wmv");
            frames = new Mat();

            // Start the thread for requesting the video frames
            new Thread(this).start();

            setSize(1400,1000);
            setVisible(true);
        } catch (Exception e) {
            System.out.println("Error loading the images: " + e);
        }

        padding = 50;
        stroke = new BasicStroke(0);

        //Anonymous inner-class listener to terminate program
        this.addWindowListener(
                new WindowAdapter() {//anonymous class definition
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);//terminate the program
                    }//end windowClosing()
                }//end WindowAdapter
        );//end addWindowListener
    }

    // Compute convolve and return color variable
    private Color computeConvolve(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9) {
        int[] reds = new int[]{getRed(p1), getRed(p2), getRed(p3), getRed(p4), getRed(p5), getRed(p6), getRed(p7), getRed(p8), getRed(p9)};
        int[] greens = new int[]{getGreen(p1), getGreen(p2), getGreen(p3), getGreen(p4), getGreen(p5), getGreen(p6), getGreen(p7), getGreen(p8), getGreen(p9)};
        int[] blues = new int[]{getBlue(p1), getBlue(p2), getBlue(p3), getBlue(p4), getBlue(p5), getBlue(p6), getBlue(p7), getBlue(p8), getBlue(p9)};

        int red = computeConvolveChannel(reds);
        int green = computeConvolveChannel(greens);
        int blue = computeConvolveChannel(blues);

        Color c = new Color(red, green, blue);
        return c;
//        return new Color(red, green, blue).getRGB();
    }

    // Convolve kernel - calculate the average color within kernel
    private int computeConvolveChannel(int[] values) {
        int result = 0;
        for(int i = 0; i < values.length; i++) {
            result += values[i];
        }
        result /= 9;

        if(result < 0)
            return 0;
        if(result > 255)
            return 255;
        return result;
    }

    // Generate the Dots
    public void genDots(BufferedImage src, int dotRadius, Graphics g) {
//        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Ellipse2D.Double dot;
        Graphics2D g2 = (Graphics2D)g;

        for (int i = 1; i < width; i++) {
            for (int j = 1; j < height; j++) {
                // Dot position perturbations
                int perturbX = (int)(Math.random() * 6) - 3;
                int perturbY = (int)(Math.random() * 6) - 3;
                dot = new Ellipse2D.Double(i+padding+perturbX, j+padding+perturbY, dotRadius, dotRadius);

                //Fill dot with averaged color in the original image where the dot will cover
                Color new_rgb;
                new_rgb = computeConvolve(
                        src.getRGB(i - 1, j - 1), src.getRGB(i, j - 1), src.getRGB(i + 1, j - 1),
                        src.getRGB(i - 1, j), src.getRGB(i, j), src.getRGB(i + 1, j),
                        src.getRGB(i - 1, j + 1), src.getRGB(i, j + 1), src.getRGB(i + 1, j + 1)
                );
                g2.setColor(new_rgb);
                g2.setStroke(stroke);
                // Draw the dot
                g2.draw(dot);
                g2.fill(dot);
            }
        }

    }

    //Getters for Color
    private int getRed(int rgb) {
        return (new Color(rgb)).getRed();
    }

    private int getGreen(int rgb) {
        return (new Color(rgb)).getGreen();
    }

    private int getBlue(int rgb) {
        return (new Color(rgb)).getBlue();
    }

    // Clip color values
    private int clip(int v) {
        v = v > 255 ? 255 : v;
        v = v < 0 ? 0 : v;
        return v;
    }

    // The render method for the user interface
    public void paint(Graphics g) {
        // Display test Image
        g.drawImage(testImage, 800, 0+padding, width, height,this);

        genDots(testImage,15,g);

//        while (inputVideo != null) {
//            if (inputVideo.read(frames)) {
//                // Getting a bufferedimage object from mat frame
//                BufferedImage image = Mat2BufferedImage(frames);
//
//                // Draw the original image  on the left
//                g.drawImage(image, 50, 200, width, height, this);
//
//                // Draw the updated image on the right
//                BufferedImage modifiedImage = Mat2BufferedImage(frames);
//                g.drawImage(modifiedImage, 600, 200, width, height, this);
//                g.drawOval(600, 200,10,10);
//                genDots(modifiedImage,10,g);
//            }
//
//            //  If last frame detected, set the video to loop by reassigning the video file
//            if(!inputVideo.grab()) {
//                inputVideo = new VideoCapture("testVideo.wmv");
//                print("new video loop");
//            }
//        }
    }

    //  Main method required to initialize the app
    public static void main(String[] args) {
        UI UI = new UI();// instantiate the class
        UI.repaint(); // paint the frame

    }

    @Override
    public void run() {

    }

    // Helper function to print messages (omit typing System.out.println everytime)
    private void print(String message) {
        System.out.println(message);
    }

    public BufferedImage Mat2BufferedImage(Mat m){
        //source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
        //Fastest code
        //The output can be assigned either to a BufferedImage or to an Image

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
}
