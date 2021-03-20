package main.java;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

public class UI extends JFrame implements Runnable {
    // **************** Variable declarations *********************//
    int width, height;
    BufferedImage testImage;

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
            width = testImage.getWidth()/3;
            height = testImage.getHeight()/3;

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
    }

    // The render method for the user interface
    public void paint(Graphics g) {
//        this.setSize(1400, 1000);
//
//        // Display test Image
//        g.drawImage(testImage, 0, 0, width, height,this);
        while (inputVideo != null) {
            if (inputVideo.read(frames)) {
                BufferedImage image = Mat2BufferedImage(frames);
                g.drawImage(image, 200, 200, width, height, this);
            }

            //  If last frame detected, set the video to loop by reassigning the video file
            if(!inputVideo.grab()) {
                inputVideo = new VideoCapture("testVideo.wmv");
                print("new video loop");
            }
        }
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
