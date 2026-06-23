package vista;

import controlador.ControladorOdontologo;
import dominio.Odontologo;
import excepcion.ClinicaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Ventana de gestión de Odontólogos. Misma plantilla que Pacientes, más simple
 * (menos campos y SIN domicilio). Acá la matrícula SÍ se puede modificar.
 */
public class VentanaOdontologo extends JFrame {

    private final ControladorOdontologo controladorOdontologo;

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtMatricula;

    private JTable tabla;
    private DefaultTableModel modelo;

    private Long idSeleccionado = null;

    public VentanaOdontologo(ControladorOdontologo controladorOdontologo) {
        this.controladorOdontologo = controladorOdontologo;
        configurarVentana();
        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
        refrescarTabla();
    }

    private void configurarVentana() {
        setTitle("Gestión de Odontólogos");
        setSize(700, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del odontólogo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(18);
        txtApellido = new JTextField(18);
        txtMatricula = new JTextField(18);

        agregarCampo(panel, gbc, 0, "Nombre:", txtNombre);
        agregarCampo(panel, gbc, 1, "Apellido:", txtApellido);
        agregarCampo(panel, gbc, 2, "Matrícula:", txtMatricula);
        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JTextField campo) {
        gbc.gridx = 0; gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(campo, gbc);
    }

    private JScrollPane crearTabla() {
        String[] columnas = {"ID", "Nombre", "Apellido", "Matrícula"};
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
        scroll.setBorder(BorderFactory.createTitledBorder("Odontólogos registrados"));
        return scroll;
    }

    private JPanel crearBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnAgregar.addActionListener(e -> accionAgregar());
        btnModificar.addActionListener(e -> accionModificar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        panel.add(btnAgregar);
        panel.add(btnModificar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        return panel;
    }

    private void accionAgregar() {
        if (hayCamposVacios()) {JOptionPane.showMessageDialog(this, "Completá los campos marcados en rojo.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controladorOdontologo.registrar(txtNombre.getText(), txtApellido.getText(), txtMatricula.getText());
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Odontólogo agregado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionModificar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccioná un odontólogo de la tabla para modificar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controladorOdontologo.modificar(idSeleccionado, txtNombre.getText(), txtApellido.getText(), txtMatricula.getText());
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Odontólogo modificado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccioná un odontólogo de la tabla para eliminar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que querés eliminar el odontólogo seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean eliminado = controladorOdontologo.eliminarPorId(idSeleccionado);
        if (eliminado) {
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Odontólogo eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el odontólogo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        List<Odontologo> odontologos = controladorOdontologo.listarTodos();
        for (Odontologo o : odontologos) {
            modelo.addRow(new Object[]{o.getId(), o.getNombre(), o.getApellido(), o.getMatricula()});
        }
    }

    private void cargarFilaEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        Long id = (Long) modelo.getValueAt(fila, 0);
        Odontologo o = controladorOdontologo.buscarPorId(id);
        idSeleccionado = o.getId();
        txtNombre.setText(o.getNombre());
        txtApellido.setText(o.getApellido());
        txtMatricula.setText(o.getMatricula());
    }

    private void limpiarCampos() {
        idSeleccionado = null;
        tabla.clearSelection();
        for (JTextField campo : todosLosCampos()) {
            campo.setText("");
            campo.setBorder(UIManager.getBorder("TextField.border"));
        }
        txtNombre.requestFocus();
    }

    private boolean hayCamposVacios() {
        boolean hayVacios = false;
        for (JTextField campo : todosLosCampos()) {
            if (campo.getText().trim().isEmpty()) {
                campo.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                hayVacios = true;
            } else {
                campo.setBorder(UIManager.getBorder("TextField.border"));
            }
        }
        return hayVacios;
    }

    private JTextField[] todosLosCampos() {
        return new JTextField[]{txtNombre, txtApellido, txtMatricula};
    }
}
