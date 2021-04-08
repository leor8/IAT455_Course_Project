package main.java;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class UI extends JFrame implements Runnable {
    // **************** Constants declarations *********************//
    // **************** End of Constants declarations **************//
    final int videoWidth = 540;
    final int videoHeight = 300;
    // **************** Variable declarations *********************//
    JPanel uiPanel;
    JButton buttonStart;
    JLabel label1;

    int width, height, padding;
    BufferedImage testImage;
    BasicStroke stroke;
    ArrayList<Point> dotPosition = new ArrayList<Point>();;
    ArrayList<Dot> dotList = new ArrayList<Dot>();

    // Placeholders for video
    Rectangle2D.Double video1, video2, video3;

    private VideoCapture inputVideo;
    private VideoCapture videoForProcessing;
    private Mat frames;
    private Mat framesForProcessing;
    private ArrayList<ArrayList<Dot>> dotLists = new ArrayList<>();
    private boolean firstLoop = true;
    private boolean loading = true;
    private int dotListIndex = 0;
    private static FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);

    OpticalFlow of;

    // **************** End of Variable declarations **************//

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    //  Constructor for user interface
    public UI() {
        // Set the basic configurations of the frame
        this.setTitle("IAT455 Course Project Group 1");

        buttonStart = new JButton("Upload start Image");
        buttonStart.setBounds(20,140,150,30);
        this.setUpButtonClick(); // Calling defined method to setup button's action performed method
        buttonStart.setLayout(null); // Allow buttons position and size to be modified through setBounds
        this.add(buttonStart);

        setSize(1400,1000);
        this.setLayout(new BorderLayout());
        this.setVisible(true);

        // Load test image and video
        try{
            // Setting the attributes of test image
            testImage = ImageIO.read(new File("testImage.jpg"));
            width = testImage.getWidth()/2;
            height = testImage.getHeight()/2;

            // Setting the attributes of test video
            inputVideo = new VideoCapture("aerial_traffic.mp4");
            videoForProcessing = new VideoCapture("aerial_traffic.mp4");
            frames = new Mat();
            framesForProcessing = new Mat();

            // Start the thread for requesting the video frames
            new Thread(this).start();
        } catch (Exception e) {
            System.out.println("Error loading the images: " + e);
        }

        padding = 50;
        stroke = new BasicStroke(0);
        of = new OpticalFlow();

        //while loop for processing video + for loop to generate initial dots *********


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

    //need a create dot method - add new dots to dotlist ****** add all # for x and y before putting in constructor

    // Generate the Dots -- TURN INTO DOT CLASS --
    public void genDots(BufferedImage src, int dotRadius, Graphics g, int startX, int startY, Mat currentMat, Mat nextMat, boolean firstLoop) {
//        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Ellipse2D.Double dot;
        Graphics2D g2 = (Graphics2D)g;
        ArrayList<Dot> currentDots = new ArrayList<Dot>();

        for (int i = 1; i < src.getWidth(); i+=dotRadius) {
            for (int j = 1; j < src.getHeight(); j+=dotRadius) {
                // Dot position perturbations
                int perturbX = (int)(Math.random() * 6) - 3;
                int perturbY = (int)(Math.random() * 6) - 3;
                int finalX = startX+i+padding+perturbX;
                int finalY = startY+j+padding+perturbY;
                dot = new Ellipse2D.Double(finalX, finalY, dotRadius, dotRadius);

                //save dot position in arraylist
                dotPosition.add(new Point(startX+i+padding+perturbX, startY+j+padding+perturbY));

                //Fill dot with averaged color in the original image where the dot will cover
                Color new_rgb;
                new_rgb = computeConvolve(
                        src.getRGB(i - 1, j - 1), src.getRGB(i, j - 1), src.getRGB(i + 1, j - 1),
                        src.getRGB(i - 1, j), src.getRGB(i, j), src.getRGB(i + 1, j),
                        src.getRGB(i - 1, j + 1), src.getRGB(i, j + 1), src.getRGB(i + 1, j + 1)
                );
                currentDots.add(new Dot(finalX, finalY, dotRadius, new_rgb));
//                g2.setColor(new_rgb);
//                g2.setStroke(stroke);
//                // Draw the dot
//                g2.draw(dot);
//                g2.fill(dot);
            }
        }

        // Call optical flow
        ArrayList<Dot> nextDots = of.calcOptFlow(currentMat, nextMat, currentDots, (int)(startX + dotRadius/2 + padding),
                (int)(startY + dotRadius/2 + padding), (int)(startX + videoWidth - dotRadius/2 + padding),
                (int)(startY + videoHeight - dotRadius/2 +padding));
        if(firstLoop) {
            dotLists.add(currentDots);
        }
        dotLists.add(nextDots);
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
        Graphics2D g2 = (Graphics2D) g;
        // Display test Image
//        if(testImage != null) {
//            g.drawImage(testImage, width+30+padding, 0+padding, width, height,this);
//            BufferedImage resizedImage  = resizeImage(testImage, width, height);
//            genDots(resizedImage,10,g);
//        }

        // Do i turn this into a method and put it at the top to generate dots once,
        // and here again to keep calculating optical flow + moving dots?
        while (inputVideo != null) {
            if (inputVideo.read(frames)) {
                // Getting a bufferedimage object from mat frame
                BufferedImage image = Mat2BufferedImage(frames);

                // Draw the original image  on the left
                g.drawImage(image, 800, 130, videoWidth, videoHeight, this);

                // Draw the updated image on the right
                if(loading) {
                    BufferedImage modifiedImage = Mat2BufferedImage(frames);
                    modifiedImage = resizeImage(modifiedImage, videoWidth, videoHeight);
                    Mat currentMat = frames;

                    videoForProcessing.read(framesForProcessing);
                    Mat nextMat = framesForProcessing;
                    //Draw dots
                    if(nextMat != null) {
                        genDots(modifiedImage,5,g, 150, 85, currentMat, nextMat, this.firstLoop);
                    }
                    firstLoop = false;
                } else {
                     ArrayList<Dot> currList = dotLists.get(dotListIndex);
                     for(int i = 0; i < currList.size();  i++) {
                         currList.get(i).draw(g2);
                     }
                    dotListIndex++;
                }

            }

            //  If last frame detected, set the video to loop by reassigning the video file
            if(!inputVideo.grab()) {
                inputVideo = new VideoCapture("aerial_traffic.mp4");
                loading = false;
                dotListIndex = 0;
                System.out.println(dotLists.size());
            }
        }

        if(inputVideo == null) {
            g.drawString("Upload your video through the button on the left", 550, 100);
        }


        /* End of file upload UI */
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

    private BufferedImage resizeImage(BufferedImage src, int width, int height) {
        // Function retrieved from https://www.baeldung.com/java-resize-image
        Image result = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(result, 0, 0, null);
        return outputImage;
    }

    // Method to initialize button click event (To make code less messy)
    private void setUpButtonClick() {
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnValStart = fc.showOpenDialog(UI.this);

                if (returnValStart == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        // Setting the attributes of test video
                        inputVideo = new VideoCapture(file.getPath());
                        videoForProcessing = new VideoCapture(file.getPath());
                        videoForProcessing.read(framesForProcessing);
                        //generate first set of dots here
                        //genDots(videoForProcessing, 5, 150, 85);

                        repaint();
                    } catch (Exception error) {
                        print(error.getMessage());
//                        showMessageDialog(null, "Unable to translate the input file into a readable file. Please reupload!");
                    }
                } else {
                    showMessageDialog(null, "Unable to read the file, please try again!");
                }
            }
        });
    }
}
