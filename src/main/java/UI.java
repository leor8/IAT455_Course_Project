package main.java;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class UI extends JFrame{
    // **************** End of Variable declarations **************//

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    //  Constructor for user interface
    public UI() {
        setTitle("IAT455 Group 1 Course Project");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //instantiating our BallPanel
        UIPanel uiPanel = new UIPanel(1400, 800);

        //adding it to the current frame
        this.add(uiPanel);
        this.pack();
        //displaying the frame
        this.setVisible(true);
    }

    //  Main method required to initialize the app
    public static void main(String[] args) {
        UI UI = new UI();// instantiate the class
    }
}
