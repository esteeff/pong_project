package mx.unison.pong;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks;
    
    private GameState state;
    private int score;
    private int lives;

    public GameModel() {
        resetGame();
    }

    public void resetGame() {
        // Crear pelota y raqueta en posiciones iniciales
        ball = new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 4, -4);
        paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
        
        bricks = new ArrayList<>();
        createBricks();

        score = 0;
        lives = 3;
        state = GameState.RUNNING; // Al inicio el juego corre
    }

    private void createBricks() {
        int rows = 5;
        int cols = 8;
        double brickWidth = 70;
        double brickHeight = 20;
        double startX = 35; // Para que quede centrado
        double startY = 50;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = startX + (j * brickWidth);
                double y = startY + (i * brickHeight);
                
                // Asignar resistencia y color dependiendo de la fila
                int resistance;
                Color color;
                if (i == 0) { // Fila superior (más dura)
                    resistance = 3;
                    color = Color.RED;
                } else if (i == 1) {
                    resistance = 2;
                    color = Color.ORANGE;
                } else { // Filas inferiores (fáciles)
                    resistance = 1;
                    color = Color.GREEN;
                }
                
                // Espacio de 2px entre ladrillos
                bricks.add(new Brick(x, y, brickWidth - 2, brickHeight - 2, color, resistance));
            }
        }
    }
    
    // --- MÉTODOS DE ACTUALIZACIÓN (Lógica) ---

    public void update() {
        if (state != GameState.RUNNING) {
            return; // Si está pausado, no se mueve nada
        }

        ball.move();

        // 1. Colisión con paredes laterales
        if (ball.getX() <= 0) {
            ball.setX(0);
            ball.setDx(-ball.getDx()); // Rebote
        } else if (ball.getX() + ball.getDiameter() >= WIDTH) {
            ball.setX(WIDTH - ball.getDiameter());
            ball.setDx(-ball.getDx());
        }

        // 2. Colisión con el techo
        if (ball.getY() <= 0) {
            ball.setY(0);
            ball.setDy(-ball.getDy());
        }

        // 3. Colisión con la raqueta
        if (ball.getBounds().intersects(paddle.getBounds())) {
            if (ball.getDy() > 0) { // Solo rebotar si va hacia abajo
                ball.setY(paddle.getY() - ball.getDiameter());
                ball.setDy(-ball.getDy());
            }
        }

        // 4. Colisión con los ladrillos
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.getBounds().intersects(brick.getShape())) {
                brick.hit(); // Le pega al ladrillo
                ball.setDy(-ball.getDy()); // Rebote vertical
                score += 10; // Sumar puntos
                break; // Solo romper un ladrillo por frame
            }
        }

        // 5. Pérdida de pelota (cae al fondo)
        if (ball.getY() > HEIGHT) {
            lives--;
            if (lives <= 0) {
                state = GameState.GAME_OVER;
            } else {
                // Reiniciar pelota y raqueta
                ball = new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 4, -4);
                paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
            }
        }

        // 6. Condición de victoria
        boolean allDestroyed = true;
        for (Brick b : bricks) {
            if (!b.isDestroyed()) {
                allDestroyed = false;
                break;
            }
        }
        if (allDestroyed) {
            state = GameState.WIN;
        }
    }

    // --- MÉTODOS DE CONTROL ---
    public void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

    // Getters
    public Ball getBall() { return ball; }
    public Paddle getPaddle() { return paddle; }
    public List<Brick> getBricks() { return bricks; }
    public GameState getState() { return state; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
}