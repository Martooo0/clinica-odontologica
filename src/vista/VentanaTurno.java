package vista;

import controlador.ControladorTurno;
import controlador.ControladorPaciente;
import controlador.ControladorOdontologo;
import dominio.Paciente;
import dominio.Odontologo;
import dominio.Turno;
import dominio.EstadoTurno;
import excepcion.ClinicaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Ventana de gestión de Turnos.
 * Novedad respecto a las otras: usa JComboBox para elegir el paciente y el
 * odontólogo (de los que ya existen), y campos de texto para fecha y hora.
 * Necesita los 3 controladores: el de turnos para operar, y los de paciente/
 * odontólogo para llenar los combos.
 */
public class VentanaTurno extends JFrame {

    private final ControladorTurno controladorTurno;
    private final ControladorPaciente controladorPaciente;
    private final ControladorOdontologo controladorOdontologo;

    private JComboBox<String> comboPaciente;
    private JComboBox<String> comboOdontologo;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JComboBox<EstadoTurno> comboEstado;

    // Listas en paralelo a los combos: la posición del combo coincide con la de estas listas,
    // así recupero el objeto real a partir del índice seleccionado (sin renderer ni clases internas).
    private List<Paciente> pacientes;
    private List<Odontologo> odontologos;

    private JTable tabla;
    private DefaultTableModel modelo;

    private Long idSeleccionado = null;

    public VentanaTurno(ControladorTurno controladorTurno, ControladorPaciente controladorPaciente, ControladorOdontologo controladorOdontologo) {
        this.controladorTurno = controladorTurno;
        this.controladorPaciente = controladorPaciente;
        this.controladorOdontologo = controladorOdontologo;
        configurarVentana();
        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
        refrescarTabla();
    }

    private void configurarVentana() {
        setTitle("Gestión de Turnos");
        setSize(900, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del turno"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // El combo muestra texto corto y guardo los objetos reales en una lista paralela.
        comboPaciente = new JComboBox<>();
        pacientes = controladorPaciente.listarTodos();
        for (Paciente p : pacientes) {
            comboPaciente.addItem(p.getNombreCompleto() + " (DNI " + p.getDni() + ")");
        }

        comboOdontologo = new JComboBox<>();
        odontologos = controladorOdontologo.listarTodos();
        for (Odontologo o : odontologos) {
            comboOdontologo.addItem(o.getNombreCompleto() + " (Mat. " + o.getMatricula() + ")");
        }

        txtFecha = new JTextField(12);
        txtHora = new JTextField(12);
        comboEstado = new JComboBox<>(EstadoTurno.values()); // las 4 constantes del enum

        // Columna izquierda
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1; panel.add(comboPaciente, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Odontólogo:"), gbc);
        gbc.gridx = 1; panel.add(comboOdontologo, gbc);
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
        String[] columnas = {"ID", "Paciente", "Odontólogo", "Fecha", "Hora", "Estado"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
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
        int iPac = comboPaciente.getSelectedIndex();
        int iOdo = comboOdontologo.getSelectedIndex();
        if (iPac == -1 || iOdo == -1) {
            JOptionPane.showMessageDialog(this,
                    "Tenés que tener al menos un paciente y un odontólogo cargados.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Paciente paciente = pacientes.get(iPac);
        Odontologo odontologo = odontologos.get(iOdo);
        try {
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            controladorTurno.asignar(paciente.getId(), odontologo.getId(), fecha, hora);
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Turno asignado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Fecha u hora con formato inválido.\nFecha: aaaa-mm-dd   Hora: hh:mm",
                    "Formato inválido", JOptionPane.ERROR_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionReprogramar() {
        if (idSeleccionado == null) { avisoSinSeleccion(); return; }
        try {
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            controladorTurno.reprogramar(idSeleccionado, fecha, hora);
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Turno reprogramado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Fecha u hora con formato inválido.\nFecha: aaaa-mm-dd   Hora: hh:mm",
                    "Formato inválido", JOptionPane.ERROR_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionCambiarEstado() {
        if (idSeleccionado == null) { avisoSinSeleccion(); return; }
        try {
            EstadoTurno estado = (EstadoTurno) comboEstado.getSelectedItem();
            controladorTurno.cambiarEstado(idSeleccionado, estado);
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Estado actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminar() {
        if (idSeleccionado == null) { avisoSinSeleccion(); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que querés eliminar el turno seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean eliminado = controladorTurno.eliminarPorId(idSeleccionado);
        if (eliminado) {
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Turno eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el turno.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void avisoSinSeleccion() {
        JOptionPane.showMessageDialog(this, "Seleccioná un turno de la tabla primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        List<Turno> turnos = controladorTurno.listarTodos();
        for (Turno t : turnos) {
            modelo.addRow(new Object[]{
                    t.getId(),
                    t.getPaciente().getNombreCompleto(),
                    t.getOdontologo().getNombreCompleto(),
                    t.getFecha(),
                    t.getHora(),
                    t.getEstado()
            });
        }
    }

    private void cargarFilaEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        Long id = (Long) modelo.getValueAt(fila, 0);
        Turno t = controladorTurno.buscarPorId(id);
        idSeleccionado = t.getId();
        comboPaciente.setSelectedIndex(pacientes.indexOf(t.getPaciente()));     // misma posición en la lista paralela
        comboOdontologo.setSelectedIndex(odontologos.indexOf(t.getOdontologo()));
        txtFecha.setText(t.getFecha().toString());
        txtHora.setText(t.getHora().toString());
        comboEstado.setSelectedItem(t.getEstado());
    }

    private void limpiarCampos() {
        idSeleccionado = null;
        tabla.clearSelection();
        if (comboPaciente.getItemCount() > 0) comboPaciente.setSelectedIndex(0);
        if (comboOdontologo.getItemCount() > 0) comboOdontologo.setSelectedIndex(0);
        txtFecha.setText("");
        txtHora.setText("");
        comboEstado.setSelectedIndex(0);
    }
}
