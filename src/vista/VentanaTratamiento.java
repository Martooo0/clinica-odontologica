package vista;

import dominio.Tratamiento;
import fachada.ClinicaFacade;

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

public class VentanaTratamiento extends VentanaABM {

    private static final Dimension TAMANIO = new Dimension(620, 460);

    private final ClinicaFacade clinica;

    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtPrecio = new JTextField(10);

    public VentanaTratamiento(ClinicaFacade clinica) {
        super("Gestión de Tratamientos", TAMANIO, "tratamiento");
        this.clinica = clinica;
        add(crearFormulario(), BorderLayout.NORTH);
        agregarTabla("Tratamientos que ofrece la clínica", "ID", "Nombre", "Precio de lista");
        refrescarTabla();
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del tratamiento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; panel.add(txtNombre, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Precio de lista ($):"), gbc);
        gbc.gridx = 1; panel.add(txtPrecio, gbc);
        return panel;
    }

    @Override
    protected void agregar() {
        clinica.registrarTratamiento(txtNombre.getText(), Entrada.leerPrecio(txtPrecio.getText()));
    }

    @Override
    protected void modificar(Long id) {
        clinica.modificarTratamiento(id, txtNombre.getText(), Entrada.leerPrecio(txtPrecio.getText()));
    }

    @Override
    protected String mensajeModificado() {
        return "Tratamiento modificado.\nLos turnos ya pedidos mantienen el precio que se les cotizó.";
    }

    @Override
    protected void eliminar(Long id) {
        clinica.eliminarTratamiento(id);
    }

    @Override
    protected void mostrarEnFormulario(Long id) {
        Tratamiento tratamiento = clinica.buscarTratamiento(id);
        txtNombre.setText(tratamiento.getNombre());
        txtPrecio.setText(tratamiento.getPrecio().toPlainString());
    }

    @Override
    protected void limpiarFormulario() {
        Campos.limpiar(txtNombre, txtPrecio);
        txtNombre.requestFocus();
    }

    @Override
    protected List<Object[]> filas() {
        List<Object[]> filas = new ArrayList<>();
        for (Tratamiento tratamiento : clinica.listarTratamientos()) {
            filas.add(new Object[]{tratamiento.getId(), tratamiento.getNombre(), Formato.moneda(tratamiento.getPrecio())});
        }
        return filas;
    }
}
