package mx.unison.pong;

import java.awt.geom.Rectangle2D;

public class Paddle {
    private double x, y; // Posición
    private final double WIDTH = 100; // ancho de la raqueta
    private final double HEIGHT = 15; // alto de la raqueta
    private final double SPEED = 8; // velocidad de movimiento

    public Paddle(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void moveLeft() {
        x -= SPEED;
    }

    public void moveRight() {
        x += SPEED;
    }

    // metodo para que la raqueta no se salga de la pantalla
    public void keepInBounds(double boardWidth) {
        if (x < 0) {
            x = 0; // si se sale por la izquierda, la pega al borde
        }
        if (x + WIDTH > boardWidth) {
            x = boardWidth - WIDTH; // si se sale por la derecha, la pega al borde
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return WIDTH; }
    public double getHeight() { return HEIGHT; }
}