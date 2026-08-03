package com.example;

import com.example.gui.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Swing no es thread-safe: todas las operaciones de GUI deben ejecutarse
        // en el Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}