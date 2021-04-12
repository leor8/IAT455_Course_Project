package main.java;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class UIPanel extends JPanel implements ActionListener, ChangeListener {
    // **************** Constants declarations *********************//
    final int VIDEO_WIDTH = 540;
    final int VIDEO_HEIGHT = 300;
    final int RADIUS_MIN = 9;
    final int RADIUS_MAX = 17;
    final int DEFAULT_RADIUS_VAL = 9;
    // **************** End of Constants declarations **************//

    // **************** Variable declarations *********************//
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

    ArrayList<Point> dotPosition = new ArrayList<Point>();

    private int dotRadiusSelected = DEFAULT_RADIUS_VAL;

    private Timer timer;
    private int padding;
    BasicStroke stroke;

    private ImageManipulator im;

    private File fileReference;

    // **************** End of variable declarations *********************//

    public UIPanel(int width, int height) {
        // Set the size of the canvas
        this.setPreferredSize(new Dimension(width, height));

        // Set up ui elements (button and radius)
        buttonStart = new JButton("Upload Start Video");
        buttonStart.setBounds(20,140,150,30);
        this.setUpButtonClick(); // Calling defined method to setup button's action performed method
        add(buttonStart);

        radiusSlider = new JSlider(JSlider.HORIZONTAL, RADIUS_MIN, RADIUS_MAX, DEFAULT_RADIUS_VAL);
        radiusSlider.setPaintTicks(true);
        radiusSlider.setPaintLabels(true);
        radiusSlider.setPaintTrack(true);
        radiusSlider.addChangeListener(this);

        add(radiusSlider);

        // Set Mat holders
        frames = new Mat();
        framesForProcessing = new Mat();

        // Set up timer for frame render
        timer = new Timer(1, this);
        timer.start();

        // Variables for generating dots
        padding = 50;
        stroke = new BasicStroke(0);
        of = new OpticalFlow();

        // Initialize the image manipulation helper method
        im = new ImageManipulator();


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
                    g.drawString("Filtered Video with customized radius", 700, 80);
                    g.drawString("First Frame filtered as intermediate result", 450, 420);

                    // Only display the first frame as intermediate result
                    ArrayList<Dot> firstFrame = dotLists.get(0);
                    for(int i = 0; i < firstFrame.size(); i++) {
                        firstFrame.get(i).drawCustomizedDot(g2, -250, VIDEO_HEIGHT + 40, 14);
                    }

                    // Display the filtered result with customized dot size
                    ArrayList<Dot> currList = dotLists.get(dotListIndex);
                    for(int i = 0; i < currList.size();  i++) {
                        currList.get(i).drawCustomizedDot(g2, 0, 0, dotRadiusSelected);
                    }
                    dotListIndex++;
                }

            }

            //  If last frame detected, set the video to loop by reassigning the video file
            if(!inputVideo.grab()) {
                System.out.println("Reload");
                inputVideo = new VideoCapture(fileReference.getPath());
                loading = false;
                dotListIndex = 0;
            }
        }

        if(inputVideo == null) {
            g.drawString("Upload your video through the button on top", 550, 100);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    // Generate the Dots
    public void genDots(BufferedImage src, int dotRadius, Graphics g, int startX, int startY,
                        Mat currentMat, Mat nextMat, boolean firstLoop, boolean recordDotPos) {
        ArrayList<Dot> currentDots = new ArrayList<Dot>();

        for (int i = 1; i < src.getWidth(); i+=dotRadius) {
            for (int j = 1; j < src.getHeight(); j+=dotRadius) {
                // Dot position perturbations
                int perturbX = (int)(Math.random() * 6) - 3;
                int perturbY = (int)(Math.random() * 6) - 3;
                int finalX = startX+i+padding+perturbX;
                int finalY = startY+j+padding+perturbY;

                //save dot position in arraylist
                dotPosition.add(new Point(finalX, finalY));

                //Fill dot with averaged color in the original image where the dot will cover
                Color new_rgb;
                new_rgb = im.computeConvolve(
                        src.getRGB(i - 1, j - 1), src.getRGB(i, j - 1), src.getRGB(i + 1, j - 1),
                        src.getRGB(i - 1, j), src.getRGB(i, j), src.getRGB(i + 1, j),
                        src.getRGB(i - 1, j + 1), src.getRGB(i, j + 1), src.getRGB(i + 1, j + 1)
                );
                currentDots.add(new Dot(finalX, finalY, dotRadius, new_rgb));
            }
        }

        // Call optical flow
        ArrayList<Dot> nextDots = of.calcOptFlow(currentMat, nextMat, currentDots, (int) (startX + dotRadius / 2 + padding),
                (int) (startY + dotRadius / 2 + padding), (int) (startX + VIDEO_WIDTH - dotRadius / 2 + padding),
                (int) (startY + VIDEO_HEIGHT - dotRadius / 2 + padding));
        if (firstLoop && recordDotPos) {
            dotLists.add(currentDots);
        }
        if (recordDotPos) {
            dotLists.add(nextDots);
        }

    }

    // Button binding function to make code less messy
    private void setUpButtonClick() {
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnValStart = fc.showOpenDialog(UIPanel.this);

                if (returnValStart == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        // Setting the attributes of input video
                        fileReference = file;
                        inputVideo = new VideoCapture(file.getPath());
                        videoForProcessing = new VideoCapture(file.getPath());
                        videoForProcessing.read(framesForProcessing);

                        // If a new file is inputted
                        dotLists = new ArrayList<>();
                        firstLoop = true;
                        loading = true;
                        dotListIndex = 0;
                        repaint();
                    } catch (Exception error) {
                        showMessageDialog(null, "Unable to translate the input file into a readable file. Please reupload!");
                    }
                } else {
                    showMessageDialog(null, "Unable to read the file, please try again!");
                }
            }
        });
    }

    public void stateChanged(ChangeEvent e) {
        // State change method to detect slider  change
        dotRadiusSelected = radiusSlider.getValue();
    }
}
