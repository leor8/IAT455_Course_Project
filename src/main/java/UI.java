package main.java;

import org.opencv.core.Core;
import javax.swing.*;

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
