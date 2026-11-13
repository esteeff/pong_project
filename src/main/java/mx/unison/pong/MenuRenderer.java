package mx.unison.pong;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Random;

public class MenuRenderer {

    public void dibujar(Graphics2D g2d, int ancho, int alto, int opcionMenu, boolean mostrandoInstrucciones, boolean mostrandoNiveles) {
        // fondo oscuro azulado
        g2d.setColor(new Color(25, 30, 40)); 
        g2d.fillRect(0, 0, ancho, alto);

        // dibujar ladrillos transparentes de fondo
        Color[] colores = {
            new Color(100, 150, 255), new Color(220, 80, 80), 
            new Color(240, 200, 80), new Color(80, 160, 100)
        };
        
        // los colocamos en una cuadricula para que no se encimen
        int ladrilloAncho = 60;
        int ladrilloAlto = 15;
        int espacioX = 80;
        int espacioY = 40;
        
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                // solo dibujamos algunos para que no sature la pantalla
                if ((i + j) % 3 == 0) {
                    int x = 30 + (j * espacioX);
                    int y = 20 + (i * espacioY);
                    Color c = colores[(i + j) % 4];
                    g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30)); // transparencia
                    g2d.fillRect(x, y, ladrilloAncho, ladrilloAlto);
                }
            }
        }


        // titulo
        g2d.setColor(new Color(240, 240, 240));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 50));
        String titulo = "BREAKOUT";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(titulo, (ancho - fm.stringWidth(titulo)) / 2, 150);

        if (mostrandoInstrucciones) {
            dibujarVentanaInstrucciones(g2d, ancho, alto);
        } else if (mostrandoNiveles) {
            dibujarVentanaNiveles(g2d, ancho, alto);
        } else {
            g2d.setFont(new Font("Monospaced", Font.BOLD, 25));
            String[] opciones = {"JUGAR", "COMO JUGAR", "NIVELES"};
            int y = 250;
            for (int i = 0; i < opciones.length; i++) {
                FontMetrics fmOpt = g2d.getFontMetrics();
                int x = (ancho - fmOpt.stringWidth(opciones[i])) / 2;

                if (i == opcionMenu) {
                    g2d.setColor(new Color(240, 200, 80)); // amarillo
                    g2d.drawString(opciones[i], x, y);
                    g2d.setColor(new Color(240, 240, 240));
                } else {
                    g2d.drawString(opciones[i], x, y);
                }
                y += 50;
            }
        }
    }

    private void dibujarVentanaInstrucciones(Graphics2D g2d, int ancho, int alto) {
        int boxWidth = 550; int boxHeight = 450; 
        int boxX = (ancho - boxWidth) / 2; int boxY = (alto - boxHeight) / 2;

        g2d.setColor(new Color(30, 35, 45));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // dibujar la x para cerrar
        g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
        g2d.setColor(new Color(220, 80, 80)); // rojo
        g2d.drawString("X", boxX + boxWidth - 35, boxY + 35);
        g2d.setColor(Color.WHITE);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
        String titulo = "COMO JUGAR";
        FontMetrics fmTitulo = g2d.getFontMetrics();
        g2d.drawString(titulo, boxX + (boxWidth - fmTitulo.stringWidth(titulo)) / 2, boxY + 40);

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 15));
        String[] lineas = {
            "Mueve con FLECHAS, inicia con ARRIBA",
            "Pausa con ESPACIO, volver al menu con M",
            "Presiona ENTER para cerrar esta ventana",
            "",
            "SIMBOLOS:",
            "[+] Pelota extra",
            "[L] Disparos laser",
            "[C] Vida extra",
            "",
            "LADRILLOS:",
            "Azul: 1 golpe",
            "Rojo: 2 golpes",
            "Amarillo: 3 golpes",
            "Verde: 1 golpe"
        };

        int y = boxY + 80;
        for (String linea : lineas) {
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(linea, boxX + (boxWidth - fm.stringWidth(linea)) / 2, y);
            y += 24; 
        }
    }

    private void dibujarVentanaNiveles(Graphics2D g2d, int ancho, int alto) {
        int boxWidth = 400; int boxHeight = 250;
        int boxX = (ancho - boxWidth) / 2; int boxY = (alto - boxHeight) / 2;

        g2d.setColor(new Color(30, 35, 45));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // dibujar la x para cerrar
        g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
        g2d.setColor(new Color(220, 80, 80)); // rojo
        g2d.drawString("X", boxX + boxWidth - 35, boxY + 35);
        g2d.setColor(Color.WHITE);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
        String titulo = "NIVELES";
        FontMetrics fmTitulo = g2d.getFontMetrics();
        g2d.drawString(titulo, boxX + (boxWidth - fmTitulo.stringWidth(titulo)) / 2, boxY + 40);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 30));
        int nivelX = boxX + 50;
        int nivelY = boxY + 100;
        for (int i = 1; i <= 3; i++) {
            g2d.setColor(new Color(100, 150, 255)); // azul
            g2d.drawOval(nivelX, nivelY, 50, 50);
            String num = String.valueOf(i);
            FontMetrics fmNum = g2d.getFontMetrics();
            g2d.drawString(num, nivelX + (50 - fmNum.stringWidth(num)) / 2, nivelY + 35);
            nivelX += 100;
        }
        g2d.setColor(Color.WHITE);

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 16));
        String msg = "Presiona ENTER para volver";
        FontMetrics fmMsg = g2d.getFontMetrics();
        g2d.drawString(msg, boxX + (boxWidth - fmMsg.stringWidth(msg)) / 2, boxY + 200);
    }
}