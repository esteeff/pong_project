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
        // crear pelota y raqueta en posiciones iniciales
        ball = new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 0, 0); // velocidad 0 al inicio
        paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
        
        bricks = new ArrayList<>();
        createBricks();

        score = 0;
        lives = 3;
        state = GameState.READY; // empieza en READY, no en RUNNING
    }
    private void createBricks() {
        int rows = 5;
        int cols = 8;
        double brickWidth = 70;
        double brickHeight = 20;
        double startX = 35; // para que quede centrado
        double startY = 50;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = startX + (j * brickWidth);
                double y = startY + (i * brickHeight);
                
                // resistencia y color dependiendo de la fila
                int resistance;
                Color color;
                if (i == 0) { // fila superior (más dura)
                    resistance = 3;
                    color = Color.RED;
                } else if (i == 1) {
                    resistance = 2;
                    color = Color.ORANGE;
                } else { // filas inferiores (fáciles)
                    resistance = 1;
                    color = Color.GREEN;
                }
                
                // espacio de 2px entre ladrillos
                bricks.add(new Brick(x, y, brickWidth - 2, brickHeight - 2, color, resistance));
            }
        }
    }

              public void update() {
        if (state == GameState.READY) {
            ball.setX(paddle.getX() + (paddle.getWidth() / 2) - (ball.getDiameter() / 2.0));
            ball.setY(paddle.getY() - ball.getDiameter() - 1);
            return; 
        }
        
        if (state != GameState.RUNNING) {
            return; 
        }

         ball.move();

        // colisión con paredes laterales
        if (ball.getX() <= 0) {
            ball.setX(0);
            ball.setDx(-ball.getDx()); // rebote
        } else if (ball.getX() + ball.getDiameter() >= WIDTH) {
            ball.setX(WIDTH - ball.getDiameter());
            ball.setDx(-ball.getDx());
        }

        // colisión con el techo
        if (ball.getY() <= 0) {
            ball.setY(0);
            ball.setDy(-ball.getDy());
        }

        // colisión con la raqueta
        if (ball.getBounds().intersects(paddle.getBounds())) {
            if (ball.getDy() > 0) { // solo rebotar si va hacia abajo
                ball.setY(paddle.getY() - ball.getDiameter());
                ball.setDy(-ball.getDy());
            }
        }

        // colisión con los ladrillos
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.getBounds().intersects(brick.getShape())) {
                brick.hit(); // le pega al ladrillo
                ball.setDy(-ball.getDy()); // rebote vertical
                score += 10; // sumar puntos
                break; // solo romper un ladrillo por frame
            }
        }

        // perdida de pelota (cae al fondo)
        if (ball.getY() > HEIGHT) {
            lives--;
            if (lives <= 0) {
                state = GameState.GAME_OVER;
            } else {
                // reiniciar pelota y raqueta
                ball = new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 4, -4);
                paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
            }
        }

        // condición de victoria
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

    public void shootBall() {
        if (state == GameState.READY) {
            state = GameState.RUNNING;
            ball.setDx(4);  // velocidad inicial X
            ball.setDy(-4); // velocidad inicial Y
        }
    }

    public void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

   
    public Ball getBall() { return ball; }
    public Paddle getPaddle() { return paddle; }
    public List<Brick> getBricks() { return bricks; }
    public GameState getState() { return state; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
}