package mx.unison.pong;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Crear el Frame
            JFrame frame = new JFrame("Breakout - Refactorizado");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // 2. Crear el juego (Modelo)
            GameModel model = new GameModel();

            // 3. Crear el panel (Vista)
            GamePanel panel = new GamePanel(model);
            frame.add(panel);

            // 4. Ajustar tamaño y mostrar
            frame.pack();
            frame.setLocationRelativeTo(null); // Centrar en pantalla
            frame.setVisible(true);

            // 5. Pedir foco para que el teclado funcione
            panel.requestFocusInWindow();

            // 6. Crear el Timer (Ciclo de juego)
            // Este Timer llama al método "actionPerformed" cada 10 milisegundos
            Timer timer = new Timer(10, e -> {
                model.update();       // Actualiza la lógica del juego
                panel.repaint();      // Vuelve a dibujar en pantalla
            });
            timer.start(); // ¡Iniciar el juego!
        });
    }
}