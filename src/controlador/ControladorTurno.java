package controlador;

import dominio.EstadoTurno;
import dominio.Turno;
import excepcion.TurnoNoEncontradoException;
import servicio.ServicioTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ControladorTurno {

    private ServicioTurno servicioTurno;

    public ControladorTurno(ServicioTurno servicioTurno) {
        this.servicioTurno = servicioTurno;
    }

    public Turno asignar(Long idPaciente, Long idOdontologo, LocalDate fechaInicio, LocalTime hora) {
        return servicioTurno.reservar(idPaciente, idOdontologo, fechaInicio, hora);
    }

    public Turno confirmar(Long idTurno) {
        return servicioTurno.confirmar(idTurno);
    }

    public Turno cancelar(Long idTurno) {
        return servicioTurno.cancelar(idTurno);
    }

    public Turno reprogramar(Long idTurno, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Turno turno = servicioTurno.buscarPorId(idTurno);
        turno.setFecha(nuevaFecha);
        turno.setHora(nuevaHora);
        return servicioTurno.modificar(turno);
    }

    public Turno cambiarEstado(Long idTurno, EstadoTurno nuevoEstado) {
        Turno turno = servicioTurno.buscarPorId(idTurno);
        turno.setEstado(nuevoEstado);
        return servicioTurno.modificar(turno);
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

    public List<Turno> listarPorOdontologo(Long idOdontologo) {
        return servicioTurno.listarPorOdontologo(idOdontologo);
    }

    public List<Turno> listarPorFecha(LocalDate fecha) {
        return servicioTurno.listarPorFecha(fecha);
    }

    public boolean eliminarPorId(Long id) {
        try {
            servicioTurno.eliminar(id);
        } catch (TurnoNoEncontradoException e) {
            return false;
        }
        return true;
    }
}
