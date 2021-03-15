package main.java;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class UI extends Frame {
    // **************** Variable declarations *********************//
    int width, height;
    BufferedImage testImage;

    // **************** End of Variable declarations **************//

    //  Constructor for user interface
    public UI() {
        // Set the basic configurations of the frame
        this.setTitle("IAT455 Course Project Group 1");
        this.setVisible(true);

        // Load test image and video
        try{
            testImage = ImageIO.read(new File("testImage.jpg"));
            width = testImage.getWidth()/3;
            height = testImage.getHeight()/3;
        } catch (Exception e) {
            System.out.println("Error loading the images: " + e);
        }
    }

    // The render method for the user interface
    public void paint(Graphics g) {
        this.setSize(1400, 1000);

        // Display test Image
        g.drawImage(testImage, 0, 0, width, height,this);
    }

    //  Main method required to initialize the app
    public static void main(String[] args) {
        UI UI = new UI();// instantiate the class
        UI.repaint(); // paint the frame

    }
}
