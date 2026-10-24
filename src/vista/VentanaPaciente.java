package vista;

import controlador.ControladorPaciente;
import controlador.Controladores;
import dominio.Paciente;
import excepcion.ClinicaException;
import observador.Evento;
import observador.Observador;
import observador.TipoEvento;

import javax.swing.*;
import java.awt.*;


public class VentanaPaciente extends Ventana implements Observador {

    private static final String TITULO = "Clínica Odontológica \"Sonrisa Feliz\" — Pacientes";
    private static final Dimension TAMANIO = new Dimension(780, 640);

    private final ControladorPaciente controladorPaciente;

    // El paciente que está usando la ventana (null = nadie identificado todavía)
    private Paciente pacienteActual = null;

    // Identificación
    private JTextField txtDni;
    private JLabel lblSesion;
    private JButton btnSalir;

    private final PanelSolicitudTurno panelSolicitud;
    private final PanelMisTurnos panelMisTurnos;
    private final AreaNotificaciones areaNotificaciones = new AreaNotificaciones("Mis notificaciones", 5, 40);

    public VentanaPaciente(Controladores controladores) {
        super(TITULO, TAMANIO);
        setLayout(new BorderLayout(8, 8));
        this.controladorPaciente = controladores.getControladorPaciente();
        this.panelSolicitud = new PanelSolicitudTurno(controladores);
        this.panelMisTurnos = new PanelMisTurnos(controladores.getControladorTurno(), controladores.getControladorPago());

        add(crearPanelIdentificacion(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(areaNotificaciones, BorderLayout.SOUTH);
        actualizarSesion();

        suscribir(this, controladores.getControladorTurno(), controladores.getControladorPago(), controladorPaciente,
                controladores.getControladorOdontologo(), controladores.getControladorTratamiento());
    }

    private JPanel crearPanelIdentificacion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Identificación"));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        txtDni = new JTextField(12);
        JButton btnIngresar = new JButton("Ingresar");
        JButton btnRegistrarme = new JButton("Registrarme");
        btnSalir = new JButton("Salir");

        btnIngresar.addActionListener(e -> accionIngresar());
        txtDni.addActionListener(e -> accionIngresar()); // Enter en el campo = Ingresar
        btnRegistrarme.addActionListener(e -> accionRegistrarme());
        btnSalir.addActionListener(e -> accionSalir());

        fila.add(new JLabel("DNI:"));
        fila.add(txtDni);
        fila.add(btnIngresar);
        fila.add(btnRegistrarme);
        fila.add(btnSalir);

        lblSesion = new JLabel();
        lblSesion.setBorder(BorderFactory.createEmptyBorder(0, 10, 4, 10));

        panel.add(fila, BorderLayout.NORTH);
        panel.add(lblSesion, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(panelSolicitud, BorderLayout.NORTH);
        panel.add(panelMisTurnos, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void actualizar(Evento evento) {
        switch (evento.getCategoria()) {
            case ODONTOLOGO, TRATAMIENTO -> panelSolicitud.cargarCombos();
            case PACIENTE -> alCambiarMisDatos(evento);
            case TURNO, PAGO -> alCambiarMisTurnos(evento);
        }
    }

    // Solo me importan los eventos que son míos
    private boolean esMio(Evento evento) {
        return pacienteActual != null && evento.involucraA(pacienteActual);
    }

    // Eventos de paciente: el secretario modificó o eliminó mis datos
    private void alCambiarMisDatos(Evento evento) {
        if (!esMio(evento)) return;
        if (evento.getTipo() == TipoEvento.PACIENTE_ELIMINADO) {
            Dialogos.aviso(this, "Sesión cerrada", "Tu ficha fue dada de baja por la secretaría.");
            accionSalir();
        } else if (evento.getTipo() == TipoEvento.PACIENTE_MODIFICADO) {
            actualizarSesion(); // refresca nombre, obra social y el descuento de la cotización
            areaNotificaciones.agregar(evento.getMensaje());
        }
    }

    private void alCambiarMisTurnos(Evento evento) {
        if (!esMio(evento)) return;
        panelMisTurnos.refrescar();

        // No me notifico lo que hice yo mismo (pedir o cancelar desde esta ventana)
        if (!evento.getTipo().esAccionDelPaciente()) {
            areaNotificaciones.notificar(evento.getMensaje());
        }
    }

    private void accionIngresar() {
        String dni = txtDni.getText().trim();
        try {
            pacienteActual = controladorPaciente.buscarPorDni(dni);
            areaNotificaciones.limpiar(); // las notificaciones son de la sesión
            panelSolicitud.cargarCombos(); // por si el secretario agregó tratamientos u odontólogos
            actualizarSesion();
        } catch (ClinicaException ex) {
            Dialogos.aviso(this, "No te encontramos",
                    ex.getMessage() + "\nSi es tu primera vez, tocá \"Registrarme\".");
        }
    }

    private void accionRegistrarme() {
        FormularioPaciente formulario = new FormularioPaciente();
        formulario.setDni(txtDni.getText().trim());

        // Repito hasta que el registro salga bien o el usuario cancele (los campos conservan lo escrito)
        while (Dialogos.pedirDatos(this, "Registrarme como paciente", formulario)) {
            try {
                pacienteActual = controladorPaciente.registrar(formulario.getDatos());
                txtDni.setText(pacienteActual.getDni());
                areaNotificaciones.limpiar();
                actualizarSesion();
                Dialogos.info(this, "Registro exitoso",
                        "¡Listo, " + pacienteActual.getNombre() + "! Ya podés pedir turnos.");
                return;
            } catch (ClinicaException ex) {
                Dialogos.error(this, "Revisá los datos", ex.getMessage());
            }
        }
    }

    private void accionSalir() {
        pacienteActual = null;
        txtDni.setText("");
        areaNotificaciones.limpiar();
        actualizarSesion();
    }

    // Habilita/deshabilita según haya alguien identificado, y refresca lo que depende del paciente
    private void actualizarSesion() {
        btnSalir.setEnabled(pacienteActual != null);
        if (pacienteActual != null) {
            lblSesion.setText("Hola, " + pacienteActual.getNombreCompleto() + " — Obra social: "
                    + pacienteActual.getObraSocial());
        } else {
            lblSesion.setText("Ingresá con tu DNI para pedir turnos. Si es tu primera vez, registrate.");
        }
        panelSolicitud.setPaciente(pacienteActual);
        panelMisTurnos.setPaciente(pacienteActual);
    }
}
