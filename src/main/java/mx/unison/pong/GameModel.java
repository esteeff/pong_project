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
    public static int nivelGlobal = 1;

    private List<Ball> balls; 
    private List<Laser> lasers; 
    private Paddle paddle;
    private List<Brick> bricks;
    private List<PowerUp> powerUps;
    
    private GameState state;
    private int nivel;
    private int score;
    private int lives;

    private int highScore;
    private final String RECORD_FILE = "record.txt";

    public GameModel() {
        loadHighScore();
        resetGame();
    }

    public void resetGame() {
        balls = new ArrayList<>();
        lasers = new ArrayList<>();
        balls.add(new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 0, 0));
        
        paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
        bricks = new ArrayList<>();
        powerUps = new ArrayList<>();
        
        createBricks();

        score = 0;
        lives = 3;
        nivel = 1;
        nivelGlobal = 1;
        state = GameState.MENU;
    }

    public void iniciarJuego() {
        state = GameState.READY;
    }

    public void iniciarNivel(int nivelSeleccionado) {
        nivel = nivelSeleccionado;
        nivelGlobal = nivel;
        lives = 3;
        score = 0; // reiniciar score
        balls.clear();
        lasers.clear();
        balls.add(new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 0, 0));
        paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
        powerUps.clear();
        createBricks();
        state = GameState.READY;
    }

    private void createBricks() {
        bricks.clear();
        int rows = 4 + nivel; 
        if (nivel == 3) rows = 8; 
        if (rows > 8) rows = 8; 

        int cols = 8;
        double brickWidth = 70;
        double brickHeight = 20;
        double startX = 35; 
        double startY = 90; 

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = startX + (j * brickWidth);
                double y = startY + (i * brickHeight);
                
                int resistance = 1;
                Color color = new Color(100, 150, 255);

                if (i % 4 == 0) { resistance = 1; color = new Color(100, 150, 255); }
                else if (i % 4 == 1) { resistance = 2; color = new Color(220, 80, 80); }
                else if (i % 4 == 2) { resistance = 3; color = new Color(240, 200, 80); }
                else { resistance = 1; color = new Color(80, 160, 100); }

                double probabilidad = Math.random();
                
                PowerUp.Tipo premio = null;
                if (probabilidad > 0.9) premio = PowerUp.Tipo.PELOTA;
                else if (probabilidad > 0.85) premio = PowerUp.Tipo.LASER;
                else if (probabilidad > 0.80) premio = PowerUp.Tipo.VIDA;
                
                bricks.add(new Brick(x, y, brickWidth - 2, brickHeight - 2, color, resistance, premio));
            }
        }
    }

    public void update() {
        if (paddle.isMovingLeft()) { paddle.moveLeft(); paddle.keepInBounds(WIDTH); }
        if (paddle.isMovingRight()) { paddle.moveRight(); paddle.keepInBounds(WIDTH); }

        if (state == GameState.READY) {
            for (Ball b : balls) {
                b.setX(paddle.getX() + (paddle.getWidth() / 2) - (b.getDiameter() / 2.0));
                b.setY(paddle.getY() - b.getDiameter() - 1);
            }
            return; 
        }

        if (state != GameState.RUNNING) return; 

        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball b = balls.get(i);
            b.move();

            if (b.getX() <= 0) { b.setX(0); b.setDx(-b.getDx()); Toolkit.getDefaultToolkit().beep(); }
            else if (b.getX() + b.getDiameter() >= WIDTH) { b.setX(WIDTH - b.getDiameter()); b.setDx(-b.getDx()); Toolkit.getDefaultToolkit().beep(); }
            if (b.getY() <= 0) { b.setY(0); b.setDy(-b.getDy()); Toolkit.getDefaultToolkit().beep(); }

            if (b.getBounds().intersects(paddle.getBounds())) {
                if (b.getDy() > 0) { 
                    b.setY(paddle.getY() - b.getDiameter());
                    double centroRaqueta = paddle.getX() + (paddle.getWidth() / 2.0);
                    double centroPelota = b.getX() + (b.getDiameter() / 2.0);
                    double impacto = (centroPelota - centroRaqueta) / (paddle.getWidth() / 2.0);
                    if (impacto < -1.0) impacto = -1.0;
                    if (impacto > 1.0) impacto = 1.0;
                    b.setDx(impacto * 3); b.setDy(-Math.abs(b.getDy()));
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && b.getBounds().intersects(brick.getShape())) {
                    romperLadrillo(brick);
                    b.setDy(-b.getDy()); 
                    break; 
                }
            }

            if (b.getY() > HEIGHT) balls.remove(i);
        }

        for (int i = lasers.size() - 1; i >= 0; i--) {
            Laser l = lasers.get(i);
            l.mover();
            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && l.getBounds().intersects(brick.getShape())) {
                    romperLadrillo(brick);
                    lasers.remove(i);
                    break;
                }
            }
            if (l.getY() < 0) lasers.remove(i);
        }

        if (balls.isEmpty()) {
            Toolkit.getDefaultToolkit().beep(); 
            lives--;
            if (lives <= 0) {
                saveHighScore();
                state = GameState.GAME_OVER;
            } else {
                balls.add(new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 0, 0));
                paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
                lasers.clear();
                state = GameState.READY; 
            }
        }

        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            p.mover();

            if (p.getBounds().intersects(paddle.getBounds())) {
                p.setRecogido(true);
                
                if (p.getTipo() == PowerUp.Tipo.PELOTA) {
                    balls.add(new Ball(paddle.getX() + 40, paddle.getY() - 20, 3, -4.8));
                } else if (p.getTipo() == PowerUp.Tipo.LASER) {
                    for (int j = 0; j < 3; j++) {
                        lasers.add(new Laser(paddle.getX() + 10, paddle.getY() - 20 - (j * 20)));
                        lasers.add(new Laser(paddle.getX() + paddle.getWidth() - 15, paddle.getY() - 20 - (j * 20)));
                    }
                } else if (p.getTipo() == PowerUp.Tipo.VIDA) {
                    lives++;
                }
                
                powerUps.remove(i);
            } else if (p.getY() > HEIGHT) {
                powerUps.remove(i);
            }
        }

        boolean allDestroyed = true;
        for (Brick b : bricks) {
            if (!b.isDestroyed()) { allDestroyed = false; break; }
        }
        if (allDestroyed) {
            if (nivel < 3) {
                state = GameState.NIVEL_COMPLETADO;
            } else {
                saveHighScore(); state = GameState.WIN;
            }
        }
    }

    public void siguienteNivel() {
        nivel++;
        nivelGlobal = nivel;
        lives = 3;
        balls.clear();
        lasers.clear();
        balls.add(new Ball(WIDTH / 2.0 - 7, HEIGHT - 100, 0, 0));
        paddle = new Paddle(WIDTH / 2.0 - 50, HEIGHT - 30);
        powerUps.clear();
        createBricks();
        state = GameState.READY;
    }

    private void romperLadrillo(Brick brick) {
        brick.hit();
        score += 10;
        Toolkit.getDefaultToolkit().beep();

        if (brick.isDestroyed()) {
            if (brick.getPowerUpType() != null) {
                powerUps.add(new PowerUp(brick.getShape().x, brick.getShape().y, brick.getPowerUpType()));
            }
        }
    }

    public void shootBall() {
        if (state == GameState.READY) {
            state = GameState.RUNNING;
            for (Ball b : balls) {
                b.setDx(0);  
                b.setDy(-4.8 - (nivel * 0.2));
            }
        }
    }

    public void togglePause() {
        if (state == GameState.RUNNING) state = GameState.PAUSED;
        else if (state == GameState.PAUSED) state = GameState.RUNNING;
    }

    private void loadHighScore() {
        try {
            if (Files.exists(Paths.get(RECORD_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(RECORD_FILE))).trim();
                highScore = Integer.parseInt(content);
            }
        } catch (Exception e) { highScore = 0; }
    }

    private void saveHighScore() {
        if (score > highScore) {
            highScore = score;
            try (PrintWriter out = new PrintWriter(RECORD_FILE)) { out.println(highScore); } 
            catch (Exception e) { System.out.println("No se pudo guardar el record."); }
        }
    }

    public List<Ball> getBalls() { return balls; }
    public List<Laser> getLasers() { return lasers; }
    public Paddle getPaddle() { return paddle; }
    public List<Brick> getBricks() { return bricks; }
    public List<PowerUp> getPowerUps() { return powerUps; }
    public GameState getState() { return state; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getHighScore() { return highScore; }
    public int getNivel() { return nivel; }
}