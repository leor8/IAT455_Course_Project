package main.java;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Dot {
    BasicStroke stroke = new BasicStroke(0);
    private int x, y, radius;
    Ellipse2D.Double dot;
    Color color;

    public Dot(int x, int y, int radius, Color c) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        color = c;
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

    // Draw dot at desired location with radius
    public void drawCustomizedDot(Graphics g, int xOffset,  int yOffset, int radius) {
        Ellipse2D.Double newDot = new Ellipse2D.Double(this.x + xOffset, this.y + yOffset, radius, radius);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(this.color);
        g2.setStroke(stroke);
        // Draw the dot
        g2.draw(newDot);
        g2.fill(newDot);
    }

    // Helper functions
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getRadius() { return this.radius; }

    public Color getColor() { return this.color; }

}
