package main.java;

import java.awt.*;

public class UI extends Frame {
    // **************** Variable declarations *********************//
    int width, height;

    // **************** End of Variable declarations **************//

    //  Constructor for user interface
    public UI() {
        // Set the basic configurations of the frame
        this.setTitle("IAT455 Course Project Group 1");
        this.setVisible(true);

        // Get single width  and height of the image
        width = 500;
        height = 500;
    }

    // The render method for the user interface
    public void paint(Graphics g) {
        this.setSize(1500, 1000);
    }

    //  Main method required to initialize the app
    public static void main(String[] args) {
        UI UI = new UI();// instantiate the class
        UI.repaint(); // paint the frame

    }
}
