package vista;

import dominio.Constantes;
import dominio.Cotizacion;
import dominio.Odontologo;
import dominio.Paciente;
import dominio.Tratamiento;
import dominio.Turno;
import dto.SolicitudTurno;
import excepcion.ClinicaException;
import fachada.ClinicaFacade;

import javax.swing.*;
import java.awt.*;

public class PanelSolicitudTurno extends JPanel {

    private static final Dimension TAMANIO_PRECIO = new Dimension(290, 150);
    private static final float TAMANIO_TOTAL = 16f;

    private final ClinicaFacade clinica;

    private final ComboEntidad<Tratamiento> comboTratamiento = new ComboEntidad<>(Tratamiento::getNombre);
    private final ComboEntidad<Odontologo> comboOdontologo =
            new ComboEntidad<>(odontologo -> "Dr/a. " + odontologo.getNombreCompleto());
    private final JTextField txtFecha = new JTextField(10);
    private final JTextField txtHora = new JTextField(10);
    private final JLabel lblPrecioLista = new JLabel();
    private final JLabel lblDescuento = new JLabel();
    private final JLabel lblTotal = new JLabel();
    private final JButton btnSolicitar = new JButton("Solicitar turno");

    private Paciente paciente = null;

    public PanelSolicitudTurno(ClinicaFacade clinica) {
        super(new BorderLayout(10, 0));
        this.clinica = clinica;
        setBorder(BorderFactory.createTitledBorder("Solicitar turno"));

        comboTratamiento.addActionListener(e -> actualizarCotizacion()); // al elegir un tratamiento, muestro el precio
        btnSolicitar.addActionListener(e -> accionSolicitar());

        add(crearPanelDatos(), BorderLayout.CENTER);
        add(crearPanelPrecio(), BorderLayout.EAST);
        cargarCombos();
        setPaciente(null);
    }

    // Izquierda: los datos del turno
    private JPanel crearPanelDatos() {
        JPanel datos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; datos.add(new JLabel("Tratamiento:"), gbc);
        gbc.gridx = 1; datos.add(comboTratamiento, gbc);
        gbc.gridx = 0; gbc.gridy = 1; datos.add(new JLabel("Odontólogo:"), gbc);
        gbc.gridx = 1; datos.add(comboOdontologo, gbc);
        gbc.gridx = 0; gbc.gridy = 2; datos.add(new JLabel("Fecha (aaaa-mm-dd):"), gbc);
        gbc.gridx = 1; datos.add(txtFecha, gbc);
        gbc.gridx = 0; gbc.gridy = 3; datos.add(new JLabel("Hora (hh:mm):"), gbc);
        gbc.gridx = 1; datos.add(txtHora, gbc);
        return datos;
    }

    // Derecha: el precio
    private JPanel crearPanelPrecio() {
        JPanel precio = new JPanel(new GridLayout(4, 1, 2, 2));
        precio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Precio"),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, TAMANIO_TOTAL));
        precio.add(lblPrecioLista);
        precio.add(lblDescuento);
        precio.add(lblTotal);
        precio.add(btnSolicitar);
        precio.setPreferredSize(TAMANIO_PRECIO);
        return precio;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        btnSolicitar.setEnabled(paciente != null);
        actualizarCotizacion(); // el descuento depende de la obra social del paciente
    }

    public void cargarCombos() {
        comboTratamiento.cargar(clinica.listarTratamientos());
        comboOdontologo.cargar(clinica.listarOdontologos());
        actualizarCotizacion(); // por si cambió el precio del tratamiento elegido
    }

    private void accionSolicitar() {
        if (paciente == null) return; // el botón ya está deshabilitado, pero por las dudas
        Tratamiento tratamiento = comboTratamiento.getSeleccionado();
        Odontologo odontologo = comboOdontologo.getSeleccionado();
        if (tratamiento == null || odontologo == null) {
            Dialogos.aviso(this, "Faltan datos", "Elegí un tratamiento y un odontólogo.");
            return;
        }
        try {
            Turno turno = clinica.solicitarTurno(new SolicitudTurno(paciente.getId(), odontologo.getId(),
                    tratamiento.getId(), Entrada.leerFecha(txtFecha.getText()), Entrada.leerHora(txtHora.getText())));
            // (la tabla "Mis turnos" se refresca sola: el servicio avisa y actualizar() la recarga)
            txtFecha.setText("");
            txtHora.setText("");
            Dialogos.info(this, "Solicitud enviada",
                    "Turno #" + turno.getId() + " solicitado. Queda pendiente hasta que la secretaría lo confirme.");
        } catch (ClinicaException ex) {
            Dialogos.error(this, "No se pudo solicitar", ex.getMessage());
        }
    }

    // Muestra precio de lista, descuento y total del tratamiento elegido.
    // La cuenta la hace el servicio (con la obra social del paciente); la vista solo muestra.
    private void actualizarCotizacion() {
        Tratamiento tratamiento = comboTratamiento.getSeleccionado();
        if (tratamiento == null) {
            lblPrecioLista.setText("Precio de lista: " + Constantes.VALOR_VACIO);
            lblDescuento.setText("Descuento: " + Constantes.VALOR_VACIO);
            lblTotal.setText("Total: " + Constantes.VALOR_VACIO);
            return;
        }
        Cotizacion cotizacion = clinica.cotizar(tratamiento, paciente);

        lblPrecioLista.setText("Precio de lista: " + Formato.moneda(cotizacion.getPrecioLista()));
        if (paciente == null) {
            lblDescuento.setText("Descuento: ingresá para ver el de tu obra social");
        } else {
            lblDescuento.setText("Descuento " + cotizacion.getObraSocial() + ": -" + Formato.moneda(cotizacion.getDescuento()));
        }
        lblTotal.setText("Total: " + Formato.moneda(cotizacion.getTotal()));
    }
}
