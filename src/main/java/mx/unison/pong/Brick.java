package mx.unison.pong;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Brick {
    private Rectangle2D.Double shape;
    private boolean destroyed;
    private int resistance;
    private Color color;
    private PowerUp.Tipo powerUpType;

    public Brick(double x, double y, double width, double height, Color color, int resistance, PowerUp.Tipo powerUpType) {
        this.shape = new Rectangle2D.Double(x, y, width, height);
        this.destroyed = false;
        this.resistance = resistance;
        this.color = color;
        this.powerUpType = powerUpType;
    }

    public void hit() {
        resistance--;
        if (resistance <= 0) {
            destroyed = true;
        } else {
            // cambia de color dependiendo de la resistencia
            if (resistance == 2) {
                this.color = new Color(255, 140, 0); // naranja
            } else if (resistance == 1) {
                this.color = new Color(255, 215, 0); // amarillo
            } else {
                this.color = this.color.darker();
            }
        }
    }

    public void dibujar(Graphics2D g2d) {
        if (destroyed) return;
        g2d.setColor(color);
        g2d.fillRect((int) shape.x, (int) shape.y, (int) shape.width, (int) shape.height);
    }

    public Rectangle2D.Double getShape() { return shape; }
    public boolean isDestroyed() { return destroyed; }
    public Color getColor() { return color; }
    public PowerUp.Tipo getPowerUpType() { return powerUpType; }
}