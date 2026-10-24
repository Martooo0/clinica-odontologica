package vista;

import dominio.EstadoTurno;
import dominio.Pago;
import dominio.Turno;
import excepcion.ClinicaException;
import fachada.ClinicaFacade;
import observador.Evento;
import observador.Observador;

import javax.swing.*;
import java.awt.*;

public class VentanaSecretario extends Ventana implements Observador {

    private static final String TITULO = "Clínica Odontológica \"Sonrisa Feliz\" — Secretaría";
    private static final Dimension TAMANIO = new Dimension(1000, 600);
    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 18);
    private static final double PROPORCION_TABLA = 0.65;

    private final ClinicaFacade clinica;

    private final ModeloTablaSoloLectura modeloPendientes = new ModeloTablaSoloLectura("ID", "Paciente",
            "Obra social", "Tratamiento", "Odontólogo", "Fecha", "Hora", "Total");
    private final JTable tablaPendientes = new JTable(modeloPendientes);
    private final AreaNotificaciones areaNotificaciones = new AreaNotificaciones("Notificaciones");

    public VentanaSecretario(ClinicaFacade clinica) {
        super(TITULO, TAMANIO);
        this.clinica = clinica;

        add(crearMenu(), BorderLayout.WEST);
        add(crearPanelCentral(), BorderLayout.CENTER);
        refrescarPendientes();

        suscribir(clinica, this);
    }

    private JPanel crearMenu() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 8));

        JLabel titulo = new JLabel("Menú principal", SwingConstants.CENTER);
        titulo.setFont(FUENTE_TITULO);

        JButton btnPacientes = new JButton("Gestionar Pacientes");
        JButton btnOdontologos = new JButton("Gestionar Odontólogos");
        JButton btnTurnos = new JButton("Gestionar Turnos");
        JButton btnTratamientos = new JButton("Gestionar Tratamientos");
        JButton btnPagos = new JButton("Pagos");
        JButton btnBusquedas = new JButton("Búsquedas");

        btnPacientes.addActionListener(e -> new VentanaGestionPacientes(clinica).setVisible(true));
        btnOdontologos.addActionListener(e -> new VentanaOdontologo(clinica).setVisible(true));
        btnTurnos.addActionListener(e -> new VentanaTurno(clinica).setVisible(true));
        btnTratamientos.addActionListener(e -> new VentanaTratamiento(clinica).setVisible(true));
        btnPagos.addActionListener(e -> new VentanaPagos(clinica).setVisible(true));
        btnBusquedas.addActionListener(e -> new VentanaBusqueda(clinica).setVisible(true));

        panel.add(titulo);
        panel.add(btnPacientes);
        panel.add(btnOdontologos);
        panel.add(btnTurnos);
        panel.add(btnTratamientos);
        panel.add(btnPagos);
        panel.add(btnBusquedas);
        return panel;
    }

    private JSplitPane crearPanelCentral() {
        // Arriba: tabla de solicitudes pendientes con sus botones
        tablaPendientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnConfirmar = new JButton("Confirmar turno");
        JButton btnRechazar = new JButton("Rechazar turno");
        btnConfirmar.addActionListener(e -> accionConfirmar());
        btnRechazar.addActionListener(e -> accionRechazar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        botones.add(btnConfirmar);
        botones.add(btnRechazar);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setBorder(BorderFactory.createTitledBorder("Solicitudes de turno pendientes"));
        arriba.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);
        arriba.add(botones, BorderLayout.SOUTH);

        // Abajo: notificaciones
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, arriba, areaNotificaciones);
        split.setResizeWeight(PROPORCION_TABLA); // 65% para la tabla
        split.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 8));
        return split;
    }

    @Override
    public void actualizar(Evento evento) {
        refrescarPendientes(); // cualquier cambio puede sumar o sacar pendientes

        // Notifico solo lo que hizo el PACIENTE (lo que hace el secretario ya lo sabe)
        if (evento.getTipo().esAccionDelPaciente()) {
            areaNotificaciones.notificar(evento.getMensaje());
        }
    }

    private void accionConfirmar() {
        Long id = idTurnoSeleccionado();
        if (id == null) {
            avisoSinSeleccion();
            return;
        }
        try {
            clinica.confirmarTurno(id);
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void accionRechazar() {
        Long id = idTurnoSeleccionado();
        if (id == null) {
            avisoSinSeleccion();
            return;
        }
        if (!Dialogos.confirmar(this, "Rechazar turno", "¿Rechazar (cancelar) el turno #" + id + "?")) return;
        try {
            clinica.cancelarTurno(id);
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private Long idTurnoSeleccionado() {
        int fila = tablaPendientes.getSelectedRow();
        return fila == -1 ? null : modeloPendientes.getId(fila);
    }

    private void avisoSinSeleccion() {
        Dialogos.aviso(this, "Sin selección", "Seleccioná una solicitud de la tabla.");
    }

    private void refrescarPendientes() {
        modeloPendientes.setRowCount(0);
        for (Turno turno : clinica.listarTurnosPorEstado(EstadoTurno.PENDIENTE)) {
            Pago pago = clinica.buscarPagoPorTurno(turno.getId());
            modeloPendientes.addRow(new Object[]{
                    turno.getId(),
                    turno.getPaciente().getNombreCompleto(),
                    turno.getPaciente().getObraSocial().getNombre(),
                    turno.getNombreTratamiento(),
                    turno.getOdontologo().getNombreCompleto(),
                    turno.getFecha(),
                    turno.getHora(),
                    Formato.montoDe(pago)
            });
        }
    }
}
