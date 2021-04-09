package main.java;

import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageManipulator {
    // Helper class for keeping code clean
    public ImageManipulator() {

    }

    public BufferedImage resizeImage(BufferedImage src, int width, int height) {
        // Function retrieved from https://www.baeldung.com/java-resize-image
        Image result = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(result, 0, 0, null);
        return outputImage;
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

    // Compute convolve and return color variable
    public Color computeConvolve(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9) {
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
    public int computeConvolveChannel(int[] values) {
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
}
