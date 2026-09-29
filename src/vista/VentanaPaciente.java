package vista;

import controlador.ControladorPaciente;
import dominio.Paciente;
import excepcion.ClinicaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaPaciente extends JFrame {

    private final ControladorPaciente controladorPaciente;

    // Campos del formulario
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtEmail;
    private JTextField txtCalle;
    private JTextField txtNumero;
    private JTextField txtLocalidad;
    private JTextField txtProvincia;

    // Aca la Tabla
    private JTable tabla;
    private DefaultTableModel modelo;

    // id del paciente seleccionado en la tabla. Primero lo pongo en null para después "darlo de alta".
    private Long idSeleccionado = null;

    public VentanaPaciente(ControladorPaciente controladorPaciente) {
        this.controladorPaciente = controladorPaciente;
        configurarVentana();
        add(crearFormulario(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
        refrescarTabla();
    }

    private void configurarVentana() {
        setTitle("Gestión de Pacientes");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // cerrar esta ventana no cierra la app
        setLayout(new BorderLayout(10, 10));
    }

    // ---------------- NORTE: formulario ----------------
    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del paciente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(16);
        txtApellido = new JTextField(16);
        txtDni = new JTextField(16);
        txtEmail = new JTextField(16);
        txtCalle = new JTextField(16);
        txtNumero = new JTextField(16);
        txtLocalidad = new JTextField(16);
        txtProvincia = new JTextField(16);

        // Columna izquierda (datos personales)
        agregarCampo(panel, gbc, 0, 0, "Nombre:", txtNombre);
        agregarCampo(panel, gbc, 0, 1, "Apellido:", txtApellido);
        agregarCampo(panel, gbc, 0, 2, "DNI:", txtDni);
        agregarCampo(panel, gbc, 0, 3, "Email:", txtEmail);
        // Columna derecha (domicilio)
        agregarCampo(panel, gbc, 2, 0, "Calle:", txtCalle);
        agregarCampo(panel, gbc, 2, 1, "Número:", txtNumero);
        agregarCampo(panel, gbc, 2, 2, "Localidad:", txtLocalidad);
        agregarCampo(panel, gbc, 2, 3, "Provincia:", txtProvincia);

        return panel;
    }

    // Helper para no repetir las 4 líneas de gbc en cada campo
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int x, int y, String etiqueta, JTextField campo) {
        gbc.gridx = x; gbc.gridy = y;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = x + 1;
        panel.add(campo, gbc);
    }

    private JScrollPane crearTabla() {
        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Email", "Domicilio"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // la tabla es de solo lectura
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // MouseListener: clic en una fila -> cargo ese paciente en el formulario
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarFilaEnFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Pacientes registrados"));
        return scroll;
    }

    // ---------------- SUR: botones ----------------
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

    // ---------------- Acciones ----------------
    private void accionAgregar() {
        if (hayCamposVacios()) {
            JOptionPane.showMessageDialog(this, "Completá los campos marcados en rojo.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controladorPaciente.registrar(txtNombre.getText(), txtApellido.getText(), txtDni.getText(), txtEmail.getText(), txtCalle.getText(), txtNumero.getText(), txtLocalidad.getText(), txtProvincia.getText());
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Paciente agregado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            // El servicio rechazó los datos (dni inválido, email sin @, dni duplicado, etc.)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionModificar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccioná un paciente de la tabla para modificar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // OJO: modificar NO recibe DNI (el DNI no se cambia, es un dato "legal")
            controladorPaciente.modificar(idSeleccionado, txtNombre.getText(), txtApellido.getText(), txtEmail.getText(), txtCalle.getText(), txtNumero.getText(), txtLocalidad.getText(), txtProvincia.getText());
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Paciente modificado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClinicaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccioná un paciente de la tabla para eliminar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que querés eliminar el paciente seleccionado?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean eliminado = controladorPaciente.eliminarPorId(idSeleccionado);
        if (eliminado) {
            refrescarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Paciente eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el paciente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla() {
        modelo.setRowCount(0); // vacío la tabla y la vuelvo a llenar desde cero
        List<Paciente> pacientes = controladorPaciente.listarTodos();
        for (Paciente p : pacientes) {
            modelo.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getApellido(), p.getDni(), p.getEmail(), p.getDomicilio()
            });
        }
    }

    private void cargarFilaEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        Long id = (Long) modelo.getValueAt(fila, 0);     // el ID está en la columna 0
        Paciente p = controladorPaciente.buscarPorId(id); // traigo el objeto completo

        idSeleccionado = p.getId();
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        txtDni.setText(p.getDni());
        txtEmail.setText(p.getEmail());
        txtCalle.setText(p.getDomicilio().getCalle());
        txtNumero.setText(p.getDomicilio().getNumero());
        txtLocalidad.setText(p.getDomicilio().getLocalidad());
        txtProvincia.setText(p.getDomicilio().getProvincia());
    }

    private void limpiarCampos() {
        idSeleccionado = null;
        tabla.clearSelection();
        for (JTextField campo : todosLosCampos()) {
            campo.setText("");
            campo.setBorder(UIManager.getBorder("TextField.border")); // restauro borde normal
        }
        txtNombre.requestFocus();
    }

    // Validación visual: marca en rojo los campos vacíos y devuelve true si hay alguno
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
        return new JTextField[]{txtNombre, txtApellido, txtDni, txtEmail, txtCalle, txtNumero, txtLocalidad, txtProvincia};
    }
}
