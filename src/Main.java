import controlador.ControladorOdontologo;
import controlador.ControladorPaciente;
import controlador.ControladorPago;
import controlador.ControladorTratamiento;
import controlador.ControladorTurno;
import controlador.Controladores;
import excepcion.PersistenciaException;
import repositorio.RepositorioBase;
import repositorio.RepositorioOdontologo;
import repositorio.RepositorioPaciente;
import repositorio.RepositorioPago;
import repositorio.RepositorioTratamiento;
import repositorio.RepositorioTurno;
import servicio.ServicioOdontologo;
import servicio.ServicioPaciente;
import servicio.ServicioPago;
import servicio.ServicioTratamiento;
import servicio.ServicioTurno;
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
import java.util.List;

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
        RepositorioPaciente repositorioPaciente = new RepositorioPaciente();
        RepositorioOdontologo repositorioOdontologo = new RepositorioOdontologo();
        RepositorioTratamiento repositorioTratamiento = new RepositorioTratamiento();
        RepositorioTurno repositorioTurno = new RepositorioTurno(repositorioPaciente, repositorioOdontologo,
                repositorioTratamiento);
        RepositorioPago repositorioPago = new RepositorioPago(repositorioTurno);

        // El orden de carga importa: cada uno necesita que ya existan los que referencia.
        // Turnos -> pacientes, odontólogos y tratamientos. Pagos -> turnos.
        List<RepositorioBase<?>> repositorios = List.of(repositorioPaciente, repositorioOdontologo,
                repositorioTratamiento, repositorioTurno, repositorioPago);
        try {
            for (RepositorioBase<?> repositorio : repositorios) {
                repositorio.cargar();
            }
        } catch (PersistenciaException e) {
            mostrarError(null, e);
        }

        ServicioPaciente servicioPaciente = new ServicioPaciente(repositorioPaciente);
        ServicioOdontologo servicioOdontologo = new ServicioOdontologo(repositorioOdontologo);
        ServicioTratamiento servicioTratamiento = new ServicioTratamiento(repositorioTratamiento, repositorioTurno);
        ServicioPago servicioPago = new ServicioPago(repositorioPago);
        ServicioTurno servicioTurno = new ServicioTurno(repositorioTurno, servicioPago);

        servicioTratamiento.inicializarCatalogo(); // primera vez: catálogo de ejemplo

        Controladores controladores = new Controladores(
                new ControladorPaciente(servicioPaciente),
                new ControladorOdontologo(servicioOdontologo),
                new ControladorTurno(servicioTurno),
                new ControladorTratamiento(servicioTratamiento),
                new ControladorPago(servicioPago));

        VentanaPaciente ventanaPaciente = new VentanaPaciente(controladores);
        VentanaSecretario ventanaSecretario = new VentanaSecretario(controladores);

        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        ventanaPaciente.setLocation(pantalla.x + MARGEN, pantalla.y + MARGEN);
        ventanaSecretario.setLocation(Math.max(pantalla.x, pantalla.x + pantalla.width - ventanaSecretario.getWidth() - MARGEN),
                pantalla.y + MARGEN);

        // Guardado automático: cada vez que se cierra una de las dos ventanas se guarda todo y cuando ya no queda ninguna abierta, se termina el programa.
        WindowAdapter alCerrar = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    for (RepositorioBase<?> repositorio : repositorios) {
                        repositorio.guardarTodos();
                    }
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
