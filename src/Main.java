import excepcion.PersistenciaException;
import fachada.ClinicaFacade;
import vista.VentanaPaciente;
import vista.VentanaSecretario;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    private static final int MARGEN = 10;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, sigue con el aspecto por defecto de Swing
                System.err.println("No se pudo aplicar el aspecto del sistema: " + e.getMessage());
            }
            iniciar();
        });
    }

    private static void iniciar() {
        ClinicaFacade clinica = new ClinicaFacade();
        try {
            clinica.cargarTodo();
        } catch (PersistenciaException e) {
            mostrarError(null, e);
        }

        VentanaPaciente ventanaPaciente = new VentanaPaciente(clinica);
        VentanaSecretario ventanaSecretario = new VentanaSecretario(clinica);

        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        ventanaPaciente.setLocation(pantalla.x + MARGEN, pantalla.y + MARGEN);
        ventanaSecretario.setLocation(Math.max(pantalla.x, pantalla.x + pantalla.width - ventanaSecretario.getWidth() - MARGEN),
                pantalla.y + MARGEN);

        // Guardado automático: cada vez que se cierra una de las dos ventanas se guarda todo y cuando ya no queda ninguna abierta, se termina el programa.
        WindowAdapter alCerrar = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    clinica.guardarTodo();
                } catch (PersistenciaException ex) {
                    mostrarError(e.getWindow(), ex);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (!ventanaPaciente.isDisplayable() && !ventanaSecretario.isDisplayable()) {
                    System.exit(0);
                }
            }
        };
        ventanaPaciente.addWindowListener(alCerrar);
        ventanaSecretario.addWindowListener(alCerrar);

        ventanaPaciente.setVisible(true);
        ventanaSecretario.setVisible(true);
    }

    private static void mostrarError(Component padre, PersistenciaException e) {
        JOptionPane.showMessageDialog(padre, e.getMessage(), "Error de archivos", JOptionPane.ERROR_MESSAGE);
    }
}
