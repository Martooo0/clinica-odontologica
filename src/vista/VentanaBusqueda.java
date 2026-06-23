package vista;

import controlador.ControladorPaciente;
import controlador.ControladorOdontologo;
import dominio.Paciente;
import dominio.Odontologo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de Búsquedas. Permite filtrar pacientes u odontólogos en vivo:
 * a medida que se escribe en el campo de texto, la tabla se va achicando.
 * Novedad: usa un KeyListener (escucha el teclado) para refiltrar en cada tecla.
 */
public class VentanaBusqueda extends JFrame {

    private final ControladorPaciente controladorPaciente;
    private final ControladorOdontologo controladorOdontologo;

    private JComboBox<String> comboTipo;
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modelo;

    public VentanaBusqueda(ControladorPaciente controladorPaciente,
                           ControladorOdontologo controladorOdontologo) {
        this.controladorPaciente = controladorPaciente;
        this.controladorOdontologo = controladorOdontologo;
        configurarVentana();
        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        actualizar(); // arranca mostrando todos los pacientes
    }

    private void configurarVentana() {
        setTitle("Búsquedas");
        setSize(760, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Buscar"));

        comboTipo = new JComboBox<>(new String[]{"Pacientes", "Odontólogos"});
        // Si cambia el tipo, re-armo la tabla con las columnas correctas
        comboTipo.addActionListener(e -> actualizar());

        txtBuscar = new JTextField(22);
        // KeyListener: en cada tecla soltada, vuelvo a filtrar
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                actualizar();
            }
        });

        panel.add(new JLabel("Buscar en:"));
        panel.add(comboTipo);
        panel.add(new JLabel("Texto:"));
        panel.add(txtBuscar);
        return panel;
    }

    private JScrollPane crearTabla() {
        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Apellido", "DNI", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados"));
        return scroll;
    }

    /**
     * Vuelve a armar la tabla según el tipo elegido y el texto escrito.
     * Se llama desde el combo (cambió el tipo) y desde el KeyListener (cambió el texto).
     */
    private void actualizar() {
        String tipo = (String) comboTipo.getSelectedItem();
        String texto = txtBuscar.getText().trim().toLowerCase();

        if ("Odontólogos".equals(tipo)) {
            modelo.setColumnIdentifiers(new String[]{"ID", "Nombre", "Apellido", "Matrícula"});
            modelo.setRowCount(0);
            for (Odontologo o : controladorOdontologo.listarTodos()) {
                if (coincide(texto, o.getNombre(), o.getApellido(), o.getMatricula())) {
                    modelo.addRow(new Object[]{o.getId(), o.getNombre(), o.getApellido(), o.getMatricula()});
                }
            }
        } else { // Pacientes
            modelo.setColumnIdentifiers(new String[]{"ID", "Nombre", "Apellido", "DNI", "Email"});
            modelo.setRowCount(0);
            for (Paciente p : controladorPaciente.listarTodos()) {
                if (coincide(texto, p.getNombre(), p.getApellido(), p.getDni())) {
                    modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getApellido(), p.getDni(), p.getEmail()});
                }
            }
        }
    }

    // Devuelve true si el texto buscado aparece en alguno de los campos (sin distinguir mayúsculas)
    private boolean coincide(String texto, String... campos) {
        if (texto.isEmpty()) return true; // sin texto, muestra todo
        for (String campo : campos) {
            if (campo != null && campo.toLowerCase().contains(texto)) {
                return true;
            }
        }
        return false;
    }
}
