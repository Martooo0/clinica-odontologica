package vista;

import controlador.ControladorPaciente;
import controlador.ControladorOdontologo;
import controlador.ControladorTurno;
import repositorio.RepositorioPaciente;
import repositorio.RepositorioOdontologo;
import repositorio.RepositorioTurno;
import servicio.ServicioPaciente;
import servicio.ServicioOdontologo;
import servicio.ServicioTurno;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicación (menú con botones).
 *
 * Cumple además dos roles del sistema:
 *  - PUNTO DE COMPOSICIÓN: arma toda la cadena repos -> servicios -> controladores
 *    (lo que antes hacía MenuPrincipal en la versión de consola).
 *  - CICLO DE VIDA: carga los datos desde archivo al abrir y los guarda al cerrar.
 */
public class VentanaPrincipal extends JFrame {

    // Guardo los repos como campos para poder volcarlos a archivo cuando se cierra la app
    private final RepositorioPaciente repositorioPaciente;
    private final RepositorioOdontologo repositorioOdontologo;
    private final RepositorioTurno repositorioTurno;

    // Controladores que le voy a pasar a cada ventana de sección
    private final ControladorPaciente controladorPaciente;
    private final ControladorOdontologo controladorOdontologo;
    private final ControladorTurno controladorTurno;

    public VentanaPrincipal() {
        repositorioPaciente = new RepositorioPaciente();
        repositorioOdontologo = new RepositorioOdontologo();
        repositorioTurno = new RepositorioTurno();

        // Cargo los archivos, con un ordenn especifico: pacientes y odontólogos primero porque los turnos tienen que saber que existan para no tener problemas con los IDs.
        repositorioPaciente.cargar();
        repositorioOdontologo.cargar();
        repositorioTurno.cargar(repositorioPaciente, repositorioOdontologo);

        ServicioPaciente servicioPaciente = new ServicioPaciente(repositorioPaciente);
        ServicioOdontologo servicioOdontologo = new ServicioOdontologo(repositorioOdontologo);
        ServicioTurno servicioTurno = new ServicioTurno(repositorioTurno, repositorioPaciente, repositorioOdontologo);

        controladorPaciente = new ControladorPaciente(servicioPaciente);
        controladorOdontologo = new ControladorOdontologo(servicioOdontologo);
        controladorTurno = new ControladorTurno(servicioTurno);
        // ------------------------------------------

        configurarVentana();
        crearBotones();
    }

    private void configurarVentana() {
        setTitle("Clínica Odontológica \"Sonrisa Feliz\"");
        setSize(420, 360);
        setLocationRelativeTo(null);                       // centra la ventana en la pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // la X cierra la app

        // Antes de salir, guardo todo a archivo (guardado automático al cerrar)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarTodo();
            }
        });
    }

    private void crearBotones() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Menú principal", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton btnPacientes = new JButton("Gestionar Pacientes");
        JButton btnOdontologos = new JButton("Gestionar Odontólogos");
        JButton btnTurnos = new JButton("Gestionar Turnos");
        JButton btnBusquedas = new JButton("Búsquedas");

        // Cada botón abre su sección (por ahora, placeholder hasta construir cada ventana)
        btnPacientes.addActionListener(e -> abrirPacientes());
        btnOdontologos.addActionListener(e -> abrirOdontologos());
        btnTurnos.addActionListener(e -> abrirTurnos());
        btnBusquedas.addActionListener(e -> abrirBusquedas());

        panel.add(titulo);
        panel.add(btnPacientes);
        panel.add(btnOdontologos);
        panel.add(btnTurnos);
        panel.add(btnBusquedas);

        add(panel);
    }

    private void abrirPacientes() {
        new VentanaPaciente(controladorPaciente).setVisible(true);
    }

    private void abrirOdontologos() {
        new VentanaOdontologo(controladorOdontologo).setVisible(true);
    }

    private void abrirTurnos() {
        new VentanaTurno(controladorTurno, controladorPaciente, controladorOdontologo).setVisible(true);
    }

    private void abrirBusquedas() {
        new VentanaBusqueda(controladorPaciente, controladorOdontologo).setVisible(true);
    }

    private void guardarTodo() {
        repositorioPaciente.guardarTodos();
        repositorioOdontologo.guardarTodos();
        repositorioTurno.guardarTodos();
    }
}
