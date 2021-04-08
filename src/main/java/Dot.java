package main.java;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Dot {
    BasicStroke stroke = new BasicStroke(0);
    private int x, y, radius;
    Ellipse2D.Double dot;
//    Color new_rgb;
    Color color;

    public Dot(int x, int y, int radius, Color c) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        color = c;
        //create new dot
        dot = new Ellipse2D.Double(this.x, this.y, this.radius, this.radius);
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(this.color);
        g2.setStroke(stroke);
        // Draw the dot
        g2.draw(dot);
        g2.fill(dot);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRadius() { return this.radius; }

    public Color getColor() { return this.color; }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    // Compute convolve and return color variable
//    private Color computeConvolve(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9) {
//        int[] reds = new int[]{getRed(p1), getRed(p2), getRed(p3), getRed(p4), getRed(p5), getRed(p6), getRed(p7), getRed(p8), getRed(p9)};
//        int[] greens = new int[]{getGreen(p1), getGreen(p2), getGreen(p3), getGreen(p4), getGreen(p5), getGreen(p6), getGreen(p7), getGreen(p8), getGreen(p9)};
//        int[] blues = new int[]{getBlue(p1), getBlue(p2), getBlue(p3), getBlue(p4), getBlue(p5), getBlue(p6), getBlue(p7), getBlue(p8), getBlue(p9)};
//
//        int red = computeConvolveChannel(reds);
//        int green = computeConvolveChannel(greens);
//        int blue = computeConvolveChannel(blues);
//
//        Color c = new Color(red, green, blue);
//        return c;
////        return new Color(red, green, blue).getRGB();
//    }

    // Convolve kernel - calculate the average color within kernel
//    private int computeConvolveChannel(int[] values) {
//        int result = 0;
//        for(int i = 0; i < values.length; i++) {
//            result += values[i];
//        }
//        result /= 9;
//
//        if(result < 0)
//            return 0;
//        if(result > 255)
//            return 255;
//        return result;
//    }

    // Generate the Dots
//    public void dotAttributes(BufferedImage src, int dotRadius, Graphics g, int startX, int startY, int padding) {
//        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
//        Ellipse2D.Double dot;
//        Graphics2D g2 = (Graphics2D)g;

//        for (int i = 1; i < src.getWidth(); i+=dotRadius) {
//            for (int j = 1; j < src.getHeight(); j+=dotRadius) {
//                // Dot position perturbations
//                int perturbX = (int)(Math.random() * 6) - 3;
//                int perturbY = (int)(Math.random() * 6) - 3;
//                dot = new Ellipse2D.Double(startX+i+padding+perturbX, startY+j+padding+perturbY, dotRadius, dotRadius);
//        dot = new Ellipse2D.Double(startX, startY, dotRadius, dotRadius);

                //save dot position in arraylist
//                dotPosition.add(new Point(startX+i+padding+perturbX, startY+j+padding+perturbY));

                //Fill dot with averaged color in the original image where the dot will cover
//                Color new_rgb;
//                new_rgb = computeConvolve(
//                        src.getRGB(i - 1, j - 1), src.getRGB(i, j - 1), src.getRGB(i + 1, j - 1),
//                        src.getRGB(i - 1, j), src.getRGB(i, j), src.getRGB(i + 1, j),
//                        src.getRGB(i - 1, j + 1), src.getRGB(i, j + 1), src.getRGB(i + 1, j + 1)
//                );
//                g2.setColor(new_rgb);
//                g2.setStroke(stroke);
//                // Draw the dot
//                g2.draw(dot);
//                g2.fill(dot);
//            }
//        }
//    }

    //Getters for Color
//    private int getRed(int rgb) {
//        return (new Color(rgb)).getRed();
//    }
//
//    private int getGreen(int rgb) {
//        return (new Color(rgb)).getGreen();
//    }
//
//    private int getBlue(int rgb) {
//        return (new Color(rgb)).getBlue();
//    }
}
