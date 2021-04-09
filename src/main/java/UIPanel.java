package main.java;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import static javax.swing.JOptionPane.showMessageDialog;

public class UIPanel extends JPanel implements ActionListener, ChangeListener {
    // **************** Constants declarations *********************//
    final int VIDEO_WIDTH = 540;
    final int VIDEO_HEIGHT = 300;
    final int RADIUS_MIN = 9;
    final int RADIUS_MAX = 17;
    final int DEFAULT_RADIUS_VAL = 9;
    // **************** End of Constants declarations **************//
    private VideoCapture videoFileForReference;
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

    JButton buttonStart;
    JSlider radiusSlider;

    ArrayList<Point> dotPosition = new ArrayList<Point>();;
    ArrayList<Dot> dotList = new ArrayList<Dot>();

    private int dotRadiusSelected = DEFAULT_RADIUS_VAL;

    private Timer timer;
    private int padding;
    BasicStroke stroke;

    private ImageManipulator im;

    public UIPanel(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));

        buttonStart = new JButton("Upload start Image");
        buttonStart.setBounds(20,140,150,30);
        this.setUpButtonClick(); // Calling defined method to setup button's action performed method
//        buttonStart.setLayout(null); // Allow buttons position and size to be modified through setBounds
        add(buttonStart);

        radiusSlider = new JSlider(JSlider.HORIZONTAL, RADIUS_MIN, RADIUS_MAX, DEFAULT_RADIUS_VAL);
        radiusSlider.setPaintTicks(true);
        radiusSlider.setPaintLabels(true);
        radiusSlider.setPaintTrack(true);
        radiusSlider.addChangeListener(this);

        add(radiusSlider);

        // Set up timer for frame render
        timer = new Timer(1, this);
        timer.start();

        // Variables for generating dots
        padding = 50;
        stroke = new BasicStroke(0);
        of = new OpticalFlow();

        // Initialize the image manipulation helper method
        im = new ImageManipulator();

        // Load test image and video
        try{
            // Setting the attributes of test video
//            inputVideo = new VideoCapture("testVideo.wmv");
            frames = new Mat();
            framesForProcessing = new Mat();
        } catch (Exception e) {
            System.out.println("Error loading the images: " + e);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawString("Customized Radius:" + dotRadiusSelected, 700, 50);
        if (inputVideo != null) {
            if (inputVideo.read(frames)) {
                // Getting a bufferedimage object from mat frame
                BufferedImage image = im.Mat2BufferedImage(frames);

                // Draw the original image  on the left
                g.drawImage(image, 150, 90, VIDEO_WIDTH, VIDEO_HEIGHT, this);

                // Draw the updated image on the right
                if(loading) {
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                    g.drawString("The video is being processed. Please wait.", 380, 600);
                    BufferedImage modifiedImage = im.Mat2BufferedImage(frames);
                    modifiedImage = im.resizeImage(modifiedImage, VIDEO_WIDTH, VIDEO_HEIGHT);
                    Mat currentMat = frames;

                    videoForProcessing.read(framesForProcessing);
                    Mat nextMat = framesForProcessing;
                    //Loading dots
                    if(nextMat != null) {
                        genDots(modifiedImage,9,g, 650, 37, currentMat, nextMat, this.firstLoop, true);
                        genDots(modifiedImage, dotRadiusSelected, g, 400, 45 + VIDEO_HEIGHT + 40, currentMat, nextMat, this.firstLoop, false);
                    }
                    firstLoop = false;
                } else {
                    g.drawString("Original Video", 140, 80);
                    g.drawString("Processed Video with default radius", 700, 80);
                    g.drawString("Processed Video with customized radius", 450, 420);
                    ArrayList<Dot> currList = dotLists.get(dotListIndex);
                    for(int i = 0; i < currList.size();  i++) {
                        currList.get(i).draw(g2);
                        currList.get(i).drawCustomizedDot(g2, -250, VIDEO_HEIGHT + 40, dotRadiusSelected);
                    }
                    dotListIndex++;
                }

            }

            //  If last frame detected, set the video to loop by reassigning the video file
            if(!inputVideo.grab()) {
                inputVideo = videoFileForReference;
                loading = false;
                dotListIndex = 0;
            }
        }

        if(inputVideo == null) {
            g.drawString("Upload your video through the button on the left", 550, 100);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    //need a create dot method - add new dots to dotlist ****** add all # for x and y before putting in constructor

    // Generate the Dots -- TURN INTO DOT CLASS --
    public void genDots(BufferedImage src, int dotRadius, Graphics g, int startX, int startY, Mat currentMat, Mat nextMat, boolean firstLoop, boolean recordDotPos) {
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
                new_rgb = im.computeConvolve(
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
                (int)(startY + dotRadius/2 + padding), (int)(startX + VIDEO_WIDTH - dotRadius/2 + padding),
                (int)(startY + VIDEO_HEIGHT - dotRadius/2 +padding));
        if(firstLoop && recordDotPos) {
            dotLists.add(currentDots);
        }
        if(recordDotPos) {
            dotLists.add(nextDots);
        }
    }

    private void setUpButtonClick() {
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnValStart = fc.showOpenDialog(UIPanel.this);

                if (returnValStart == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        // Setting the attributes of test video
                        videoFileForReference = new VideoCapture(file.getPath());
                        inputVideo = new VideoCapture(file.getPath());
                        videoForProcessing = new VideoCapture(file.getPath());
                        videoForProcessing.read(framesForProcessing);
                        System.out.println("video inputed");
                        repaint();
                    } catch (Exception error) {
                        System.out.println(error.getMessage());
//                        showMessageDialog(null, "Unable to translate the input file into a readable file. Please reupload!");
                    }
                } else {
                    showMessageDialog(null, "Unable to read the file, please try again!");
                }
            }
        });
    }

    public void stateChanged(ChangeEvent e) {
        dotRadiusSelected = radiusSlider.getValue();
    }
}
