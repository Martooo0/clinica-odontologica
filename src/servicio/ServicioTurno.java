package servicio;

import dominio.Odontologo;
import dominio.Turno;
import dominio.Paciente;
import dominio.EstadoTurno;

import repositorio.RepositorioOdontologo;
import repositorio.RepositorioPaciente;
import repositorio.RepositorioTurno;

import excepcion.DatoInvalidoException;
import excepcion.PacienteNoEncontradoException;
import excepcion.OdontologoNoEncontradoException;
import excepcion.TurnoYaReservadoException;
import excepcion.TurnoNoEncontradoException;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class ServicioTurno {

    private RepositorioTurno repositorioTurno;
    private RepositorioPaciente repositorioPaciente;
    private RepositorioOdontologo repositorioOdontologo;

    public ServicioTurno(RepositorioTurno repositorioTurno, RepositorioPaciente repositorioPaciente, RepositorioOdontologo repositorioOdontologo) {
        this.repositorioTurno = repositorioTurno;
        this.repositorioPaciente = repositorioPaciente;
        this.repositorioOdontologo = repositorioOdontologo;
    }

    public Turno reservar(Long idPaciente, Long idOdontologo, LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            throw new DatoInvalidoException("La fecha y la hora no pueden ser nulas");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha del turno no puede ser anterior a hoy");
        }
        Paciente paciente = repositorioPaciente.buscarPorId(idPaciente);
        if (paciente == null) {
            throw new PacienteNoEncontradoException("Paciente no encontrado");
        }
        Odontologo odontologo = repositorioOdontologo.buscarPorId(idOdontologo);
        if (odontologo == null) {
            throw new OdontologoNoEncontradoException("Odontólogo no encontrado");
        }
        List<Turno> turnosDelOdontologo = repositorioTurno.buscarPorOdontologo(idOdontologo); // en una variable se guardan los turnos que tiene asignado un odontologo
        for (Turno turno : turnosDelOdontologo) { // recorre cada turno
            if (turno.getFecha().equals(fecha) && turno.getHora().equals(hora)) { // verifica que no coincida la fecha y la hora
                throw new TurnoYaReservadoException("Turno ya reservado");
            }
        }
        return repositorioTurno.guardar(new Turno(fecha, hora, paciente, odontologo));
    }

    public Turno buscarPorId(Long idTurno) {
        Turno turno = repositorioTurno.buscarPorId(idTurno);
        if (turno == null) {
            throw new TurnoNoEncontradoException("Turno no encontrado");
        }
        return turno;
    }

    public Turno confirmar(Long idTurno) {
        Turno turno = buscarPorId(idTurno);
        turno.setEstado(EstadoTurno.CONFIRMADO);
        return repositorioTurno.actualizar(turno);
    }

    public Turno cancelar(Long idTurno) {
        Turno turno = buscarPorId(idTurno);
        turno.setEstado(EstadoTurno.CANCELADO);
        return repositorioTurno.actualizar(turno);
    }

    public Turno modificar(Turno turno) {
        if (turno == null) {
            throw new DatoInvalidoException("Ingrese un valor, por favor");
        }
        if (turno.getFecha() == null || turno.getHora() == null) {
            throw new DatoInvalidoException("La fecha y la hora no pueden ser nulas");
        }
        if (turno.getFecha().isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha del turno no puede ser anterior a hoy");
        }
        buscarPorId(turno.getId());
        return repositorioTurno.actualizar(turno);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        repositorioTurno.eliminar(id);
    }

    public List<Turno> listarTodos() {
        return repositorioTurno.buscarTodos();
    }

    public List<Turno> listarPorPaciente(Long idPaciente) {
        return repositorioTurno.buscarPorPaciente(idPaciente);
    }

    public List<Turno> listarPorOdontologo(Long idOdontologo) {
        return repositorioTurno.buscarPorOdontologo(idOdontologo);
    }

    public List<Turno> listarPorFecha(LocalDate fecha) {
        return repositorioTurno.buscarPorFecha(fecha);
    }


}
