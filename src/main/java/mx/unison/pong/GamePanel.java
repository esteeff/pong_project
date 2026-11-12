package mx.unison.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    private GameModel model;
    
    // fuente retro para todo el juego
    private final Font fuenteRetro = new Font("Monospaced", Font.BOLD, 20); 

    public GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(GameModel.WIDTH, GameModel.HEIGHT));
        setBackground(new Color(40, 40, 40)); // fondo gris oscuro
        setLayout(null);

        // botón de pausa
        JButton pauseButton = new JButton("PAUSA");
        pauseButton.setBounds(GameModel.WIDTH - 100, 10, 90, 30);
        pauseButton.setFocusable(false);
        pauseButton.setBackground(new Color(60, 60, 60));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        pauseButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        pauseButton.addActionListener(e -> {
            model.togglePause();
            if (model.getState() == GameState.PAUSED) {
                pauseButton.setText("DESPAUSA");
            } else {
                pauseButton.setText("PAUSA");
            }
        });
        add(pauseButton);

        // botón de jugar de nuevo (estilo retro)
        JButton restartButton = new JButton("JUGAR DE NUEVO");
        restartButton.setBounds(GameModel.WIDTH / 2 - 100, GameModel.HEIGHT / 2 + 40, 200, 40);
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        restartButton.setBackground(new Color(60, 60, 60)); 
        restartButton.setForeground(Color.WHITE);
        restartButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        restartButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        restartButton.addActionListener(e -> {
            model.resetGame();
            restartButton.setVisible(false);
            pauseButton.setText("PAUSA");
        });
        add(restartButton);
        
        setupKeyBindings();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // dibujar raqueta
        Paddle p = model.getPaddle();
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect((int) p.getX(), (int) p.getY(), (int) p.getWidth(), (int) p.getHeight());

        // dibujar pelota
        Ball b = model.getBall();
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillOval((int) b.getX(), (int) b.getY(), b.getDiameter(), b.getDiameter());

        // dibujar ladrillos
        for (Brick brick : model.getBricks()) {
            if (!brick.isDestroyed()) {
                g2d.setColor(brick.getColor());
                g2d.fillRect((int) brick.getShape().x, (int) brick.getShape().y, 
                             (int) brick.getShape().width, (int) brick.getShape().height);
            }
        }

        // dibujar puntaje
        g2d.setColor(new Color(240, 240, 240));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("SCORE: " + model.getScore(), 10, 20);
        
        // dibujar vidas como circulos
        int vidaX = 20;
        int vidaY = 45;
        int diametro = 15;

        for (int i = 0; i < 3; i++) {
            g2d.setColor(new Color(240, 240, 240));
            if (i < model.getLives()) {
                g2d.fillOval(vidaX, vidaY, diametro, diametro);
            } else {
                g2d.drawOval(vidaX, vidaY, diametro, diametro);
            }
            vidaX += 25;
        }

        // dibujar mensajes de estado
        if (model.getState() == GameState.READY) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            String msg = "PRESIONA FLECHA ARRIBA";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);

        } else if (model.getState() == GameState.PAUSED) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 30));
            String msg = "PAUSADO";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);

        } else if (model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN) {
            // recuadro de fin de juego
            int boxWidth = 450;
            int boxHeight = 250;
            int boxX = (getWidth() - boxWidth) / 2;
            int boxY = (getHeight() - boxHeight) / 2;

            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 35));
            String titulo = (model.getState() == GameState.WIN) ? "¡GANASTE!" : "FIN DEL JUEGO";
            FontMetrics fmTitulo = g2d.getFontMetrics();
            int tituloX = boxX + (boxWidth - fmTitulo.stringWidth(titulo)) / 2;
            g2d.drawString(titulo, tituloX, boxY + 70);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
            String scoreMsg = "PUNTUACION: " + model.getScore();
            FontMetrics fmScore = g2d.getFontMetrics();
            int scoreX = boxX + (boxWidth - fmScore.stringWidth(scoreMsg)) / 2;
            g2d.drawString(scoreMsg, scoreX, boxY + 120);
            
            // mostrar el record mas alto
            String highScoreMsg = "RÉCORD: " + model.getHighScore();
            FontMetrics fmHighScore = g2d.getFontMetrics();
            int highScoreX = boxX + (boxWidth - fmHighScore.stringWidth(highScoreMsg)) / 2;
            g2d.drawString(highScoreMsg, highScoreX, boxY + 150);
        }

        // mostrar boton de reinicio si el juego termino
        Component[] components = getComponents();
        for (Component c : components) {
            if (c instanceof JButton && ((JButton) c).getText().equals("JUGAR DE NUEVO")) {
                c.setVisible(model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN);
            }
        }
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // flecha izquierda
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "leftPressed");
        am.put("leftPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.getPaddle().setMovingLeft(true);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "leftReleased");
        am.put("leftReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.getPaddle().setMovingLeft(false);
            }
        });

        // flecha derecha
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "rightPressed");
        am.put("rightPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.getPaddle().setMovingRight(true);
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rightReleased");
        am.put("rightReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.getPaddle().setMovingRight(false);
            }
        });

        // flecha arriba para disparar
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up");
        am.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.shootBall();
            }
        });

        // barra espaciadora para pausar
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.togglePause();
            }
        });

        // tecla r para reiniciar
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false), "restart");
        am.put("restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN) {
                    model.resetGame();
                }
            }
        });
    }
}