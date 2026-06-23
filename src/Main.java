import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Toda la GUI de Swing debe construirse en el hilo de eventos (Event Dispatch Thread).
        // SwingUtilities.invokeLater se asegura de eso.
        SwingUtilities.invokeLater(() -> {
            try {
                // Hace que la ventana use el aspecto nativo del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, sigue con el aspecto por defecto de Swing
            }
            new VentanaPrincipal().setVisible(true);
        });
    }
}
