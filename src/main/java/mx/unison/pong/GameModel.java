package mx.unison.pong;

import java.awt.Color;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameModel {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks;
    
    private GameState state;
    private int score;
    private int lives;

    private int highScore;
    private final String RECORD_FILE = "record.txt";

    public GameModel() {
        loadHighScore();
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
        double startY = 90;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = startX + (j * brickWidth);
                double y = startY + (i * brickHeight);
                
                // resistencia y color dependiendo de la fila
                int resistance;
                Color color;
                
                if (i == 0) { // Fila superior: Azul
                    resistance = 1;
                    color = new Color(100, 150, 255); // Azul claro
                } else if (i == 1) { // Segunda fila: Rojo
                    resistance = 2;
                    color = new Color(220, 80, 80); // Rojo suave
                } else if (i == 2) { // Tercera fila: Amarillo
                    resistance = 3;
                    color = new Color(240, 200, 80); // Amarillo mostaza
                } else { // Fila inferior: Verde
                    resistance = 1;
                    color = new Color(80, 160, 100); // Verde oliva
                }
                
                // espacio de 2px entre ladrillos
                bricks.add(new Brick(x, y, brickWidth - 2, brickHeight - 2, color, resistance));
            }
        }
    }

    public void update() {
        // mover la raqueta SIEMPRE (incluso en READY)
        if (paddle.isMovingLeft()) {
            paddle.moveLeft();
            paddle.keepInBounds(WIDTH);
        }
        if (paddle.isMovingRight()) {
            paddle.moveRight();
            paddle.keepInBounds(WIDTH);
        }

        // si está en READY, la pelota sigue a la raqueta
        if (state == GameState.READY) {
            ball.setX(paddle.getX() + (paddle.getWidth() / 2) - (ball.getDiameter() / 2.0));
            ball.setY(paddle.getY() - ball.getDiameter() - 1);
            return; 
        }

        // si está pausado o terminado, no hacer nada más
        if (state != GameState.RUNNING) {
            return; 
        }

        // mover la pelota
        ball.move();

        // colisión con paredes 
        if (ball.getX() <= 0) {
            ball.setX(0);
            ball.setDx(-ball.getDx()); 
            Toolkit.getDefaultToolkit().beep(); // <-- SONIDO
        } else if (ball.getX() + ball.getDiameter() >= WIDTH) {
            ball.setX(WIDTH - ball.getDiameter());
            ball.setDx(-ball.getDx());
            Toolkit.getDefaultToolkit().beep(); // <-- SONIDO
        }

        // colisión con el techo
        if (ball.getY() <= 0) {
            ball.setY(0);
            ball.setDy(-ball.getDy());
            Toolkit.getDefaultToolkit().beep(); // <-- SONIDO
        }

        // colisión con la raqueta (rebote según el punto de impacto)
        if (ball.getBounds().intersects(paddle.getBounds())) {
            if (ball.getDy() > 0) { // solo rebotar si va hacia abajo
                ball.setY(paddle.getY() - ball.getDiameter());

                // Calculamos qué tan lejos del centro de la raqueta golpeó la pelota.
                // Va de -1.0 (extremo izquierdo) a 1.0 (extremo derecho)
                double centroRaqueta = paddle.getX() + (paddle.getWidth() / 2.0);
                double centroPelota = ball.getX() + (ball.getDiameter() / 2.0);
                
                // Calculamos la proporción del golpe
                double impacto = (centroPelota - centroRaqueta) / (paddle.getWidth() / 2.0);
                
                // Limitamos el impacto para que no sea mayor a 1 o menor a -1
                if (impacto < -1.0) impacto = -1.0;
                if (impacto > 1.0) impacto = 1.0;

                // Asignamos la nueva velocidad horizontal según el impacto
                // Si impacto es 0, se va derecho. Si es -1, va a la izquierda. Si es 1, a la derecha.
                ball.setDx(impacto * 3); // Multiplicamos por 3 para darle velocidad
                
                // Hacemos que siempre suba al rebotar
                ball.setDy(-Math.abs(ball.getDy()));
                
                Toolkit.getDefaultToolkit().beep();
            }
        }

        // colisión con los ladrillos
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.getBounds().intersects(brick.getShape())) {
                brick.hit(); 
                ball.setDy(-ball.getDy()); 
                score += 10; 
                Toolkit.getDefaultToolkit().beep(); // <-- SONIDO
                break; 
            }
        }

        // perdida de pelota (cae al fondo)
        if (ball.getY() > HEIGHT) {
            Toolkit.getDefaultToolkit().beep(); 
            lives--;
            
            if (lives <= 0) {
                saveHighScore();
                state = GameState.GAME_OVER;
            } else {
                // reinicia la pelota con velocidad 0 y pegarla a la raqueta
                ball = new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 0, 0);
                paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
                state = GameState.READY; // volver a READY para que no salga disparada
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
            saveHighScore();
            state = GameState.WIN;
        }
    }

    public void shootBall() {
        if (state == GameState.READY) {
            state = GameState.RUNNING;
            ball.setDx(0);  // no se mueve ni a la izquierda ni a la derecha
            ball.setDy(-4.8); // sube directo hacia arriba (más rápido)
        }
    }

    public void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

    private void loadHighScore() {
        try {
            if (Files.exists(Paths.get(RECORD_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(RECORD_FILE))).trim();
                highScore = Integer.parseInt(content);
            }
        } catch (Exception e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        if (score > highScore) {
            highScore = score;
            try (PrintWriter out = new PrintWriter(RECORD_FILE)) {
                out.println(highScore);
            } catch (Exception e) {
                System.out.println("No se pudo guardar el récord.");
            }
        }
    }

    public Ball getBall() { return ball; }
    public Paddle getPaddle() { return paddle; }
    public List<Brick> getBricks() { return bricks; }
    public GameState getState() { return state; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getHighScore() { return highScore; }
}