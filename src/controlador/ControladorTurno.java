package controlador;

import dominio.EstadoTurno;
import dominio.Turno;
import dto.SolicitudTurno;
import excepcion.DatoInvalidoException;
import servicio.ServicioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ControladorTurno extends ControladorObservable {

    private static final String FORMATO_INVALIDO = "Fecha u hora con formato inválido.\nFecha: aaaa-mm-dd   Hora: hh:mm";

    private final ServicioTurno servicioTurno;

    public ControladorTurno(ServicioTurno servicioTurno) {
        super(servicioTurno);
        this.servicioTurno = servicioTurno;
    }

    // Secretario: asigna un turno desde "Gestionar Turnos"
    public Turno asignar(SolicitudTurno solicitud) {
        return servicioTurno.asignar(crearTurno(solicitud));
    }

    // Paciente: solicita un turno desde su ventana
    public Turno solicitar(SolicitudTurno solicitud) {
        return servicioTurno.solicitar(crearTurno(solicitud));
    }

    public Turno confirmar(Long idTurno) {
        return servicioTurno.confirmar(idTurno);
    }

    public Turno cancelar(Long idTurno) {
        return servicioTurno.cancelar(idTurno);
    }

    public Turno cancelarPorPaciente(Long idTurno, Long idPaciente) {
        return servicioTurno.cancelarPorPaciente(idTurno, idPaciente);
    }

    public Turno reprogramar(Long idTurno, String nuevaFecha, String nuevaHora) {
        return servicioTurno.reprogramar(idTurno, leerFecha(nuevaFecha), leerHora(nuevaHora));
    }

    public Turno cambiarEstado(Long idTurno, EstadoTurno nuevoEstado) {
        return servicioTurno.cambiarEstado(idTurno, nuevoEstado);
    }

    public Turno buscarPorId(Long idTurno) {
        return servicioTurno.buscarPorId(idTurno);
    }

    public List<Turno> listarTodos() {
        return servicioTurno.listarTodos();
    }

    public List<Turno> listarPorPaciente(Long idPaciente) {
        return servicioTurno.listarPorPaciente(idPaciente);
    }

    public List<Turno> listarPorEstado(EstadoTurno estado) {
        return servicioTurno.listarPorEstado(estado);
    }

    public void eliminar(Long id) {
        servicioTurno.eliminar(id);
    }

    private Turno crearTurno(SolicitudTurno solicitud) {
        return new Turno(leerFecha(solicitud.getFecha()), leerHora(solicitud.getHora()),
                solicitud.getPaciente(), solicitud.getOdontologo(), solicitud.getTratamiento());
    }

    private LocalDate leerFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(FORMATO_INVALIDO);
        }
    }

    private LocalTime leerHora(String texto) {
        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(FORMATO_INVALIDO);
        }
    }
}
