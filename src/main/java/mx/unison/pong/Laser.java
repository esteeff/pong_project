package mx.unison.pong;

import java.awt.geom.Rectangle2D;

public class Laser {
    private double x, y;
    private final int ANCHO = 6;
    private final int ALTO = 15;
    private final double VELOCIDAD = -6; // velocidad ajustada

    public Laser(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void mover() {
        y += VELOCIDAD;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, ANCHO, ALTO);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getAncho() { return ANCHO; }
    public int getAlto() { return ALTO; }
}