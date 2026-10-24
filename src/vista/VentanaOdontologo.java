package vista;

import controlador.ControladorOdontologo;
import dominio.Odontologo;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class VentanaOdontologo extends VentanaABM {

    private static final Dimension TAMANIO = new Dimension(700, 480);
    private static final int ANCHO_CAMPO = 18;

    private final ControladorOdontologo controladorOdontologo;

    private final JTextField txtNombre = new JTextField(ANCHO_CAMPO);
    private final JTextField txtApellido = new JTextField(ANCHO_CAMPO);
    private final JTextField txtMatricula = new JTextField(ANCHO_CAMPO);

    public VentanaOdontologo(ControladorOdontologo controladorOdontologo) {
        super("Gestión de Odontólogos", TAMANIO, "odontólogo");
        this.controladorOdontologo = controladorOdontologo;
        add(crearFormulario(), BorderLayout.NORTH);
        agregarTabla("Odontólogos registrados", "ID", "Nombre", "Apellido", "Matrícula");
        refrescarTabla();
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del odontólogo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

    @Override
    protected void agregar() {
        controladorOdontologo.registrar(txtNombre.getText(), txtApellido.getText(), txtMatricula.getText());
    }

    @Override
    protected void modificar(Long id) {
        controladorOdontologo.modificar(id, txtNombre.getText(), txtApellido.getText(), txtMatricula.getText());
    }

    @Override
    protected void eliminar(Long id) {
        controladorOdontologo.eliminar(id);
    }

    @Override
    protected void mostrarEnFormulario(Long id) {
        Odontologo odontologo = controladorOdontologo.buscarPorId(id);
        txtNombre.setText(odontologo.getNombre());
        txtApellido.setText(odontologo.getApellido());
        txtMatricula.setText(odontologo.getMatricula());
    }

    @Override
    protected void limpiarFormulario() {
        Campos.limpiar(camposObligatorios());
        txtNombre.requestFocus();
    }

    @Override
    protected JTextField[] camposObligatorios() {
        return new JTextField[]{txtNombre, txtApellido, txtMatricula};
    }

    @Override
    protected List<Object[]> filas() {
        List<Object[]> filas = new ArrayList<>();
        for (Odontologo odontologo : controladorOdontologo.listarTodos()) {
            filas.add(new Object[]{odontologo.getId(), odontologo.getNombre(),
                    odontologo.getApellido(), odontologo.getMatricula()});
        }
        return filas;
    }
}
