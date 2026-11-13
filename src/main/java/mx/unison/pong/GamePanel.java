package mx.unison.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    private GameModel model;
    
    private final Font fuenteRetro = new Font("Monospaced", Font.BOLD, 20); 

    public GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(GameModel.WIDTH, GameModel.HEIGHT));
        setBackground(new Color(40, 40, 40)); 
        setLayout(null);

        JButton pauseButton = new JButton("PAUSA");
        pauseButton.setBounds(GameModel.WIDTH - 100, 10, 90, 30);
        pauseButton.setFocusable(false);
        pauseButton.setBackground(new Color(60, 60, 60));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        pauseButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        pauseButton.addActionListener(e -> {
            model.togglePause();
            if (model.getState() == GameState.PAUSED) pauseButton.setText("DESPAUSA");
            else pauseButton.setText("PAUSA");
        });
        add(pauseButton);

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

        Paddle p = model.getPaddle();
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect((int) p.getX(), (int) p.getY(), (int) p.getWidth(), (int) p.getHeight());

        g2d.setColor(new Color(240, 240, 240));
        for (Ball b : model.getBalls()) {
            g2d.fillOval((int) b.getX(), (int) b.getY(), b.getDiameter(), b.getDiameter());
        }

        g2d.setColor(Color.RED);
        for (Laser l : model.getLasers()) {
            g2d.fillRect((int) l.getX(), (int) l.getY(), l.getAncho(), l.getAlto());
        }

        for (Brick brick : model.getBricks()) {
            brick.dibujar(g2d);
        }

        for (PowerUp powerUp : model.getPowerUps()) {
            powerUp.dibujar(g2d);
        }

        g2d.setColor(new Color(240, 240, 240));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("SCORE: " + model.getScore(), 10, 20);
        g2d.drawString("NIVEL: " + model.getNivel(), 180, 20);
        
        int vidaX = 20;
        int vidaY = 45;
        int diametro = 15;
        for (int i = 0; i < 3; i++) {
            g2d.setColor(new Color(240, 240, 240));
            if (i < model.getLives()) g2d.fillOval(vidaX, vidaY, diametro, diametro);
            else g2d.drawOval(vidaX, vidaY, diametro, diametro);
            vidaX += 25;
        }

        if (model.getState() == GameState.READY) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            String msg = "PRESIONA FLECHA ARRIBA";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
        } else if (model.getState() == GameState.PAUSED) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 30));
            String msg = "PAUSADO";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
        } else if (model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN) {
            int boxWidth = 450; int boxHeight = 250;
            int boxX = (getWidth() - boxWidth) / 2; int boxY = (getHeight() - boxHeight) / 2;

            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 35));
            String titulo = (model.getState() == GameState.WIN) ? "¡GANASTE!" : "FIN DEL JUEGO";
            FontMetrics fmTitulo = g2d.getFontMetrics();
            g2d.drawString(titulo, boxX + (boxWidth - fmTitulo.stringWidth(titulo)) / 2, boxY + 70);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
            String scoreMsg = "PUNTUACION: " + model.getScore();
            FontMetrics fmScore = g2d.getFontMetrics();
            g2d.drawString(scoreMsg, boxX + (boxWidth - fmScore.stringWidth(scoreMsg)) / 2, boxY + 120);

            String highScoreMsg = "RECORD: " + model.getHighScore();
            FontMetrics fmHighScore = g2d.getFontMetrics();
            g2d.drawString(highScoreMsg, boxX + (boxWidth - fmHighScore.stringWidth(highScoreMsg)) / 2, boxY + 150);
        }

        for (Component c : getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals("JUGAR DE NUEVO")) {
                c.setVisible(model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN);
            }
        }
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "leftPressed");
        am.put("leftPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.getPaddle().setMovingLeft(true); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "leftReleased");
        am.put("leftReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.getPaddle().setMovingLeft(false); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "rightPressed");
        am.put("rightPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.getPaddle().setMovingRight(true); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rightReleased");
        am.put("rightReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.getPaddle().setMovingRight(false); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up");
        am.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.shootBall(); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.togglePause(); }
        });

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