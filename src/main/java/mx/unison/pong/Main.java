package mx.unison.pong;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Breakout - Refactorizado");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            GameModel model = new GameModel();

            GamePanel panel = new GamePanel(model);
            frame.add(panel);

            // ajustar tamaño y mostrar
            frame.pack();
            frame.setLocationRelativeTo(null); // centrar en pantalla
            frame.setVisible(true);
            panel.requestFocusInWindow(); // Darle foco al panel para que reciba las teclas

            // para que el teclado funcione
            panel.requestFocusInWindow();

            // este Timer llama al método "actionPerformed" cada 10 milisegundos
            Timer timer = new Timer(10, e -> {
                model.update();       // actualiza la lógica del juego
                panel.repaint();      // vuelve a dibujar en pantalla
            });
            timer.start(); // inicia el juego
        });
    }
}