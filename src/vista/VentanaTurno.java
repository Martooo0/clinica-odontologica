package vista;

import controlador.Controladores;
import controlador.ControladorTurno;
import controlador.ControladorPaciente;
import controlador.ControladorOdontologo;
import controlador.ControladorTratamiento;
import dominio.Paciente;
import dominio.Odontologo;
import dominio.Turno;
import dominio.Tratamiento;
import dominio.EstadoTurno;
import dto.SolicitudTurno;
import excepcion.ClinicaException;
import observador.Evento;
import observador.Observador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class VentanaTurno extends Ventana implements Observador {

    private static final Dimension TAMANIO = new Dimension(900, 540);

    private final ControladorTurno controladorTurno;
    private final ControladorPaciente controladorPaciente;
    private final ControladorOdontologo controladorOdontologo;
    private final ControladorTratamiento controladorTratamiento;

    private final ComboEntidad<Paciente> comboPaciente =
            new ComboEntidad<>(paciente -> paciente.getNombreCompleto() + " (DNI " + paciente.getDni() + ")");
    private final ComboEntidad<Odontologo> comboOdontologo =
            new ComboEntidad<>(odontologo -> odontologo.getNombreCompleto() + " (Mat. " + odontologo.getMatricula() + ")");
    private final ComboEntidad<Tratamiento> comboTratamiento =
            new ComboEntidad<>(tratamiento -> tratamiento.getNombre() + " (" + Formato.moneda(tratamiento.getPrecio()) + ")");
    private final JTextField txtFecha = new JTextField(12);
    private final JTextField txtHora = new JTextField(12);
    private final JComboBox<EstadoTurno> comboEstado = new JComboBox<>(EstadoTurno.values());

    private final ModeloTablaSoloLectura modelo =
            new ModeloTablaSoloLectura("ID", "Paciente", "Odontólogo", "Tratamiento", "Fecha", "Hora", "Estado");
    private final JTable tabla = new JTable(modelo);

    private Long idSeleccionado = null;

    public VentanaTurno(Controladores controladores) {
        super("Gestión de Turnos", TAMANIO);
        this.controladorTurno = controladores.getControladorTurno();
        this.controladorPaciente = controladores.getControladorPaciente();
        this.controladorOdontologo = controladores.getControladorOdontologo();
        this.controladorTratamiento = controladores.getControladorTratamiento();
        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
        recargarComboPacientes();
        recargarComboOdontologos();
        recargarComboTratamientos();
        refrescarTabla();

        // Me suscribo a los cambios de turnos... y me doy de baja al cerrar la ventana.
        // Si no me doy de baja, el servicio seguiría avisándole a una ventana cerrada.
        suscribir(this, controladorTurno, controladorPaciente, controladorOdontologo, controladorTratamiento);
    }

    @Override
    public void actualizar(Evento evento) {
        switch (evento.getCategoria()) {
            case TURNO -> refrescarTabla();
            case PACIENTE -> recargarComboPacientes();
            case ODONTOLOGO -> {
                recargarComboOdontologos();
                refrescarTabla(); // la tabla muestra el nombre del odontólogo
            }
            case TRATAMIENTO -> {
                recargarComboTratamientos();
                refrescarTabla(); // la tabla muestra el nombre del tratamiento
            }
            default -> { }
        }
    }

    private void recargarComboPacientes() {
        comboPaciente.cargar(controladorPaciente.listarTodos());
    }

    private void recargarComboOdontologos() {
        comboOdontologo.cargar(controladorOdontologo.listarTodos());
    }

    private void recargarComboTratamientos() {
        comboTratamiento.cargar(controladorTratamiento.listarTodos());
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del turno"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Columna izquierda
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1; panel.add(comboPaciente, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Odontólogo:"), gbc);
        gbc.gridx = 1; panel.add(comboOdontologo, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Tratamiento:"), gbc);
        gbc.gridx = 1; panel.add(comboTratamiento, gbc);
        // Columna derecha
        gbc.gridx = 2; gbc.gridy = 0; panel.add(new JLabel("Fecha (aaaa-mm-dd):"), gbc);
        gbc.gridx = 3; panel.add(txtFecha, gbc);
        gbc.gridx = 2; gbc.gridy = 1; panel.add(new JLabel("Hora (hh:mm):"), gbc);
        gbc.gridx = 3; panel.add(txtHora, gbc);
        gbc.gridx = 2; gbc.gridy = 2; panel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 3; panel.add(comboEstado, gbc);

        return panel;
    }

    private JScrollPane crearTabla() {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarFilaEnFormulario();
            }
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Turnos registrados"));
        return scroll;
    }

    private JPanel crearBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        JButton btnAsignar = new JButton("Asignar");
        JButton btnReprogramar = new JButton("Reprogramar");
        JButton btnCambiarEstado = new JButton("Cambiar estado");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnAsignar.addActionListener(e -> accionAsignar());
        btnReprogramar.addActionListener(e -> accionReprogramar());
        btnCambiarEstado.addActionListener(e -> accionCambiarEstado());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        panel.add(btnAsignar);
        panel.add(btnReprogramar);
        panel.add(btnCambiarEstado);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        return panel;
    }

    private void accionAsignar() {
        Paciente paciente = comboPaciente.getSeleccionado();
        Odontologo odontologo = comboOdontologo.getSeleccionado();
        Tratamiento tratamiento = comboTratamiento.getSeleccionado();
        if (paciente == null || odontologo == null || tratamiento == null) {
            Dialogos.aviso(this, "Faltan datos",
                    "Tenés que tener al menos un paciente, un odontólogo y un tratamiento cargados.");
            return;
        }
        try {
            controladorTurno.asignar(new SolicitudTurno(paciente, odontologo, tratamiento,
                    txtFecha.getText(), txtHora.getText()));
            limpiarCampos();
            Dialogos.info(this, "Éxito", "Turno asignado correctamente.");
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void accionReprogramar() {
        if (idSeleccionado == null) { avisoSinSeleccion(); return; }
        try {
            controladorTurno.reprogramar(idSeleccionado, txtFecha.getText(), txtHora.getText());
            limpiarCampos();
            Dialogos.info(this, "Éxito", "Turno reprogramado.");
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void accionCambiarEstado() {
        if (idSeleccionado == null) { avisoSinSeleccion(); return; }
        try {
            EstadoTurno estado = (EstadoTurno) comboEstado.getSelectedItem();
            controladorTurno.cambiarEstado(idSeleccionado, estado);
            limpiarCampos();
            Dialogos.info(this, "Éxito", "Estado actualizado.");
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void accionEliminar() {
        if (idSeleccionado == null) { avisoSinSeleccion(); return; }
        boolean confirmado = Dialogos.confirmar(this, "Confirmar eliminación",
                "¿Seguro que querés eliminar el turno seleccionado?");
        if (!confirmado) return;
        try {
            controladorTurno.eliminar(idSeleccionado);
            limpiarCampos();
            Dialogos.info(this, "Éxito", "Turno eliminado.");
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void avisoSinSeleccion() {
        Dialogos.aviso(this, "Sin selección", "Seleccioná un turno de la tabla primero.");
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        for (Turno turno : controladorTurno.listarTodos()) {
            modelo.addRow(new Object[]{
                    turno.getId(),
                    turno.getPaciente().getNombreCompleto(),
                    turno.getOdontologo().getNombreCompleto(),
                    turno.getNombreTratamiento(),
                    turno.getFecha(),
                    turno.getHora(),
                    turno.getEstado()
            });
        }
    }

    private void cargarFilaEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        Turno turno = controladorTurno.buscarPorId(modelo.getId(fila));
        idSeleccionado = turno.getId();
        comboPaciente.seleccionar(turno.getPaciente());
        comboOdontologo.seleccionar(turno.getOdontologo());
        comboTratamiento.seleccionar(turno.getTratamiento()); // -1 (vacío) en turnos viejos sin tratamiento
        txtFecha.setText(turno.getFecha().toString());
        txtHora.setText(turno.getHora().toString());
        comboEstado.setSelectedItem(turno.getEstado());
    }

    private void limpiarCampos() {
        idSeleccionado = null;
        tabla.clearSelection();
        if (comboPaciente.getItemCount() > 0) comboPaciente.setSelectedIndex(0);
        if (comboOdontologo.getItemCount() > 0) comboOdontologo.setSelectedIndex(0);
        if (comboTratamiento.getItemCount() > 0) comboTratamiento.setSelectedIndex(0);
        txtFecha.setText("");
        txtHora.setText("");
        comboEstado.setSelectedIndex(0);
    }
}
