package mx.unison.pong;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class Brick {
    private Rectangle2D.Double shape;
    private boolean destroyed;
    private int resistance; // NUEVO: Cuántos golpes aguanta
    private Color color;

    public Brick(double x, double y, double width, double height, Color color, int resistance) {
        this.shape = new Rectangle2D.Double(x, y, width, height);
        this.destroyed = false;
        this.resistance = resistance;
        this.color = color;
    }

    // Este método se llama cuando la pelota toca el ladrillo
    public void hit() {
        resistance--;
        if (resistance <= 0) {
            destroyed = true;
        } else {
            // Si le queda vida, cambiamos su color para que se vea golpeado
            this.color = this.color.darker();
        }
    }

    public Rectangle2D.Double getShape() { return shape; }
    public boolean isDestroyed() { return destroyed; }
    public Color getColor() { return color; }
}