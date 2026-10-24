package vista;

import dominio.Paciente;
import dominio.Pago;
import dominio.Turno;
import excepcion.ClinicaException;
import fachada.ClinicaFacade;

import javax.swing.*;
import java.awt.*;

public class PanelMisTurnos extends JPanel {

    private final ClinicaFacade clinica;

    private final ModeloTablaSoloLectura modelo = new ModeloTablaSoloLectura("ID", "Tratamiento", "Odontólogo",
            "Fecha", "Hora", "Estado", "Total", "Pago");
    private final JTable tabla = new JTable(modelo);
    private final JButton btnCancelarTurno = new JButton("Cancelar turno seleccionado");

    private Paciente paciente = null;

    public PanelMisTurnos(ClinicaFacade clinica) {
        super(new BorderLayout());
        this.clinica = clinica;
        setBorder(BorderFactory.createTitledBorder("Mis turnos"));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        btnCancelarTurno.addActionListener(e -> accionCancelarTurno());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        botones.add(btnCancelarTurno);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        btnCancelarTurno.setEnabled(paciente != null);
        refrescar();
    }

    public void refrescar() {
        modelo.setRowCount(0);
        if (paciente == null) return;
        for (Turno turno : clinica.listarTurnosPorPaciente(paciente.getId())) {
            Pago pago = clinica.buscarPagoPorTurno(turno.getId());
            modelo.addRow(new Object[]{
                    turno.getId(),
                    turno.getNombreTratamiento(),
                    turno.getOdontologo().getNombreCompleto(),
                    turno.getFecha(),
                    turno.getHora(),
                    turno.getEstado(),
                    Formato.montoDe(pago),
                    Formato.estadoDe(pago)
            });
        }
    }

    private void accionCancelarTurno() {
        if (paciente == null) return;
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            Dialogos.aviso(this, "Sin selección", "Seleccioná un turno de la tabla.");
            return;
        }
        Long idTurno = modelo.getId(fila);
        if (!Dialogos.confirmar(this, "Cancelar turno", "¿Cancelar el turno #" + idTurno + "?")) return;
        try {
            clinica.cancelarTurnoPorPaciente(idTurno, paciente.getId());
        } catch (ClinicaException ex) {
            Dialogos.error(this, "No se pudo cancelar", ex.getMessage());
        }
    }
}
