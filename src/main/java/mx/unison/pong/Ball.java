package mx.unison.pong;

import java.awt.geom.Rectangle2D;

public class Ball {
    private double x, y; 
    private double dx, dy; 
    private final int DIAMETER = 15;

    public Ball(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, DIAMETER, DIAMETER);
    }

    // ESTOS SON LOS MÉTODOS QUE FALTAN:
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }
    public int getDiameter() { return DIAMETER; }
}