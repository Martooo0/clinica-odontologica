package vista;

import dominio.MedioPago;
import dominio.Pago;
import dominio.Turno;
import excepcion.ClinicaException;
import fachada.ClinicaFacade;
import observador.Evento;
import observador.Observador;
import observador.TipoEvento;

import javax.swing.*;
import java.awt.*;

public class VentanaPagos extends Ventana implements Observador {

    private static final Dimension TAMANIO = new Dimension(1050, 480);

    private final ClinicaFacade clinica;

    private final ModeloTablaSoloLectura modelo = new ModeloTablaSoloLectura("ID", "Turno", "Paciente",
            "Tratamiento", "Obra social", "Precio lista", "Descuento", "Total", "Estado", "Medio", "Fecha de pago");
    private final JTable tabla = new JTable(modelo);
    private JComboBox<MedioPago> comboMedio;
    private JLabel lblTotalCobrado;

    public VentanaPagos(ClinicaFacade clinica) {
        super("Pagos", TAMANIO);
        this.clinica = clinica;
        add(crearTabla(), BorderLayout.CENTER);
        add(crearPanelCobro(), BorderLayout.SOUTH);
        refrescarTabla();

        suscribir(clinica, this);
    }

    @Override
    public void actualizar(Evento evento) {
        TipoEvento.Categoria categoria = evento.getCategoria();
        if (categoria == TipoEvento.Categoria.TURNO || categoria == TipoEvento.Categoria.PAGO) {
            refrescarTabla();
        }
    }

    private JScrollPane crearTabla() {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Pagos de turnos"));
        return scroll;
    }

    private JPanel crearPanelCobro() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        comboMedio = new JComboBox<>(MedioPago.values());
        JButton btnCobrar = new JButton("Registrar cobro");
        btnCobrar.addActionListener(e -> accionCobrar());
        izquierda.add(new JLabel("Medio de pago:"));
        izquierda.add(comboMedio);
        izquierda.add(btnCobrar);

        lblTotalCobrado = new JLabel();
        lblTotalCobrado.setFont(lblTotalCobrado.getFont().deriveFont(Font.BOLD));
        lblTotalCobrado.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(lblTotalCobrado, BorderLayout.EAST);
        return panel;
    }

    private void accionCobrar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            Dialogos.aviso(this, "Sin selección", "Seleccioná un pago de la tabla.");
            return;
        }
        try {
            Pago pago = clinica.registrarCobro(modelo.getId(fila), (MedioPago) comboMedio.getSelectedItem());
            // (la tabla se refresca sola por el Observer)
            Dialogos.info(this, "Éxito", "Cobro registrado: " + Formato.moneda(pago.getMontoFinal()));
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        for (Pago pago : clinica.listarPagos()) {
            Turno turno = pago.getTurno();
            modelo.addRow(new Object[]{
                    pago.getId(),
                    "#" + turno.getId() + " (" + turno.getFecha() + ")",
                    turno.getPaciente().getNombreCompleto(),
                    turno.getNombreTratamiento(),
                    pago.getObraSocial().getNombre() + " " + pago.getPorcentajeDescuento() + "%",
                    Formato.moneda(pago.getPrecioLista()),
                    "-" + Formato.moneda(pago.getDescuento()),
                    Formato.moneda(pago.getMontoFinal()),
                    pago.getEstado(),
                    Formato.valorDe(pago.getMedioPago()),
                    Formato.valorDe(pago.getFechaPago())
            });
        }
        lblTotalCobrado.setText("Total cobrado: " + Formato.moneda(clinica.totalCobrado()));
    }
}
