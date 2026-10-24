package vista;

import dominio.Domicilio;
import dominio.ObraSocial;
import dominio.Paciente;
import dto.DatosPaciente;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class FormularioPaciente extends JPanel {

    private static final int ANCHO_CAMPO = 16;

    private final JTextField txtNombre = new JTextField(ANCHO_CAMPO);
    private final JTextField txtApellido = new JTextField(ANCHO_CAMPO);
    private final JTextField txtDni = new JTextField(ANCHO_CAMPO);
    private final JTextField txtEmail = new JTextField(ANCHO_CAMPO);
    private final JTextField txtCalle = new JTextField(ANCHO_CAMPO);
    private final JTextField txtNumero = new JTextField(ANCHO_CAMPO);
    private final JTextField txtLocalidad = new JTextField(ANCHO_CAMPO);
    private final JTextField txtProvincia = new JTextField(ANCHO_CAMPO);
    private final JComboBox<ObraSocial> comboObraSocial = new JComboBox<>(ObraSocial.values());

    public FormularioPaciente() {
        super(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Columna izquierda (datos personales)
        agregarCampo(gbc, 0, 0, "Nombre:", txtNombre);
        agregarCampo(gbc, 0, 1, "Apellido:", txtApellido);
        agregarCampo(gbc, 0, 2, "DNI:", txtDni);
        agregarCampo(gbc, 0, 3, "Email:", txtEmail);
        // Columna derecha (domicilio)
        agregarCampo(gbc, 2, 0, "Calle:", txtCalle);
        agregarCampo(gbc, 2, 1, "Número:", txtNumero);
        agregarCampo(gbc, 2, 2, "Localidad:", txtLocalidad);
        agregarCampo(gbc, 2, 3, "Provincia:", txtProvincia);
        // Fila extra: obra social (desplegable con las constantes del enum)
        agregarCampo(gbc, 0, 4, "Obra social:", comboObraSocial);
    }

    private void agregarCampo(GridBagConstraints gbc, int x, int y, String etiqueta, JComponent campo) {
        gbc.gridx = x; gbc.gridy = y;
        add(new JLabel(etiqueta), gbc);
        gbc.gridx = x + 1;
        add(campo, gbc);
    }

    public DatosPaciente getDatos() {
        Domicilio domicilio = new Domicilio(txtCalle.getText(), txtNumero.getText(),
                txtLocalidad.getText(), txtProvincia.getText());
        return new DatosPaciente(txtNombre.getText(), txtApellido.getText(), txtDni.getText().trim(),
                txtEmail.getText(), domicilio, (ObraSocial) comboObraSocial.getSelectedItem());
    }

    public void mostrar(Paciente paciente) {
        Domicilio domicilio = paciente.getDomicilio();
        txtNombre.setText(paciente.getNombre());
        txtApellido.setText(paciente.getApellido());
        txtDni.setText(paciente.getDni());
        txtEmail.setText(paciente.getEmail());
        txtCalle.setText(domicilio.getCalle());
        txtNumero.setText(domicilio.getNumero());
        txtLocalidad.setText(domicilio.getLocalidad());
        txtProvincia.setText(domicilio.getProvincia());
        comboObraSocial.setSelectedItem(paciente.getObraSocial());
    }

    public void setDni(String dni) {
        txtDni.setText(dni);
    }

    public void limpiar() {
        Campos.limpiar(getCamposDeTexto());
        comboObraSocial.setSelectedItem(ObraSocial.PARTICULAR);
        txtNombre.requestFocus();
    }

    public JTextField[] getCamposDeTexto() {
        return new JTextField[]{txtNombre, txtApellido, txtDni, txtEmail, txtCalle, txtNumero, txtLocalidad, txtProvincia};
    }
}
