package vista;

import controlador.ControladorPaciente;
import controlador.ControladorOdontologo;
import dominio.Paciente;
import dominio.Odontologo;
import observador.Evento;
import observador.Observador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class VentanaBusqueda extends Ventana implements Observador {

    private static final Dimension TAMANIO = new Dimension(760, 480);
    private static final String[] COLUMNAS_PACIENTES = {"ID", "Nombre", "Apellido", "DNI", "Email", "Obra social"};
    private static final String[] COLUMNAS_ODONTOLOGOS = {"ID", "Nombre", "Apellido", "Matrícula"};

    private final ControladorPaciente controladorPaciente;
    private final ControladorOdontologo controladorOdontologo;

    private JComboBox<TipoBusqueda> comboTipo;
    private JTextField txtBuscar;
    private final ModeloTablaSoloLectura modelo = new ModeloTablaSoloLectura(COLUMNAS_PACIENTES);

    public VentanaBusqueda(ControladorPaciente controladorPaciente,
                           ControladorOdontologo controladorOdontologo) {
        super("Búsquedas", TAMANIO);
        this.controladorPaciente = controladorPaciente;
        this.controladorOdontologo = controladorOdontologo;
        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        buscar(); // arranca mostrando todos los pacientes

        suscribir(this, controladorPaciente, controladorOdontologo);
    }

    @Override
    public void actualizar(Evento evento) {
        buscar();
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Buscar"));

        comboTipo = new JComboBox<>(TipoBusqueda.values());
        comboTipo.addActionListener(e -> buscar());

        txtBuscar = new JTextField(22);
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscar();
            }
        });

        panel.add(new JLabel("Buscar en:"));
        panel.add(comboTipo);
        panel.add(new JLabel("Texto:"));
        panel.add(txtBuscar);
        return panel;
    }

    private JScrollPane crearTabla() {
        JScrollPane scroll = new JScrollPane(new JTable(modelo));
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados"));
        return scroll;
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim().toLowerCase();

        if (comboTipo.getSelectedItem() == TipoBusqueda.ODONTOLOGOS) {
            modelo.setColumnIdentifiers(COLUMNAS_ODONTOLOGOS);
            modelo.setRowCount(0);
            for (Odontologo odontologo : controladorOdontologo.listarTodos()) {
                if (coincide(texto, odontologo.getNombre(), odontologo.getApellido(), odontologo.getMatricula())) {
                    modelo.addRow(new Object[]{odontologo.getId(), odontologo.getNombre(),
                            odontologo.getApellido(), odontologo.getMatricula()});
                }
            }
        } else {
            modelo.setColumnIdentifiers(COLUMNAS_PACIENTES);
            modelo.setRowCount(0);
            for (Paciente paciente : controladorPaciente.listarTodos()) {
                String obraSocial = paciente.getObraSocial().getNombre();
                if (coincide(texto, paciente.getNombre(), paciente.getApellido(), paciente.getDni(), obraSocial)) {
                    modelo.addRow(new Object[]{paciente.getId(), paciente.getNombre(), paciente.getApellido(),
                            paciente.getDni(), paciente.getEmail(), obraSocial});
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
