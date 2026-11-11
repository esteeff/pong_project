package mx.unison.pong;

import java.awt.geom.Rectangle2D;

public class Paddle {
    private double x, y; // Posición
    private final double WIDTH = 100; // Ancho de la raqueta
    private final double HEIGHT = 15; // Alto de la raqueta
    private final double SPEED = 8; // Velocidad de movimiento

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

    // Método para que la raqueta no se salga de la pantalla
    public void keepInBounds(double boardWidth) {
        if (x < 0) {
            x = 0; // Si se sale por la izquierda, la pega al borde
        }
        if (x + WIDTH > boardWidth) {
            x = boardWidth - WIDTH; // Si se sale por la derecha, la pega al borde
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return WIDTH; }
    public double getHeight() { return HEIGHT; }
}