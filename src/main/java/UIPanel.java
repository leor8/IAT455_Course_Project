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
import java.util.Hashtable;

import static javax.swing.JOptionPane.showMessageDialog;

public class UIPanel extends JPanel implements ActionListener, ChangeListener {
    // **************** Constants declarations *********************//
    final int VIDEO_WIDTH = 540;
    final int VIDEO_HEIGHT = 300;
    final int RADIUS_MIN = 5;
    final int RADIUS_MAX = 15;
    // **************** End of Constants declarations **************//
    private VideoCapture inputVideo;
    private Mat frames;
    private static FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);

    JButton buttonStart;
    JSlider radiusSlider;

    private int dotRadiusSelected = 5;

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

        radiusSlider = new JSlider(JSlider.HORIZONTAL, RADIUS_MIN, RADIUS_MAX, 5);
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

        // Initialize the image manipulation helper method
        im = new ImageManipulator();

        // Load test image and video
        try{
            // Setting the attributes of test video
//            inputVideo = new VideoCapture("testVideo.wmv");
            frames = new Mat();
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
                g.drawImage(image, 800, 110, VIDEO_WIDTH, VIDEO_HEIGHT, this);

                // Draw the updated image on the right
                BufferedImage modifiedImage = im.Mat2BufferedImage(frames);
                modifiedImage = im.resizeImage(modifiedImage, VIDEO_WIDTH, VIDEO_HEIGHT);
                genDots(modifiedImage,10, g, 150, 65);

                // Draw the intermidiate image
                genDots(modifiedImage,dotRadiusSelected, g, 400, 65 + VIDEO_HEIGHT + 30);
            }

            //  If last frame detected, set the video to loop by reassigning the video file
            if(!inputVideo.grab()) {
                inputVideo = new VideoCapture("testVideo.wmv");
                System.out.println("new video loop");
            }
        }

        if(inputVideo == null) {
            g2.setColor(Color.black);
            g.drawString("Upload your video through the button on the left", 550, 300);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    // Generate the Dots
    public void genDots(BufferedImage src, int dotRadius, Graphics g, int startX, int startY) {
//        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Ellipse2D.Double dot;
        Graphics2D g2 = (Graphics2D)g;

        for (int i = 1; i < src.getWidth(); i+=dotRadius) {
            for (int j = 1; j < src.getHeight(); j+=dotRadius) {
                // Dot position perturbations
                int perturbX = (int)(Math.random() * 6) - 3;
                int perturbY = (int)(Math.random() * 6) - 3;
                dot = new Ellipse2D.Double(startX+i+padding+perturbX, startY+j+padding+perturbY, dotRadius, dotRadius);

                //Fill dot with averaged color in the original image where the dot will cover
                Color new_rgb;
                new_rgb = im.computeConvolve(
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

    private void setUpButtonClick() {
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnValStart = fc.showOpenDialog(UIPanel.this);

                if (returnValStart == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        // Setting the attributes of test video
                        inputVideo = new VideoCapture(file.getPath());
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
