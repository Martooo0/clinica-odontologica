package servicio;

import dominio.Turno;
import dominio.EstadoTurno;

import observador.EventoTurno;
import observador.Sujeto;
import observador.TipoEvento;

import repositorio.RepositorioTurno;

import excepcion.DatoInvalidoException;
import excepcion.PacienteNoEncontradoException;
import excepcion.OdontologoNoEncontradoException;
import excepcion.TurnoYaReservadoException;
import excepcion.TurnoNoEncontradoException;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static servicio.Validador.requerir;

/**
 * Reglas de los turnos.
 *
 * Hereda de Sujeto (patrón Observer): cada vez que un turno cambia, avisa a las ventanas
 * suscriptas. Así, cuando el paciente pide un turno desde su ventana, la ventana del
 * secretario se entera sola y refresca su lista, sin que nadie apriete "actualizar".
 */
public class ServicioTurno extends Sujeto {

    private final RepositorioTurno repositorioTurno;
    private final ServicioPago servicioPago; // cada turno nuevo genera su pago

    public ServicioTurno(RepositorioTurno repositorioTurno, ServicioPago servicioPago) {
        this.repositorioTurno = repositorioTurno;
        this.servicioPago = servicioPago;
    }

    // ---------------- Alta de turnos ----------------

    // Lo usa el PACIENTE desde su ventana. Avisa al secretario.
    public Turno solicitar(Turno turno) {
        return crearTurno(turno, TipoEvento.TURNO_SOLICITADO);
    }

    // Lo usa el SECRETARIO desde "Gestionar Turnos". Avisa al paciente.
    public Turno asignar(Turno turno) {
        return crearTurno(turno, TipoEvento.TURNO_ASIGNADO);
    }

    // Las validaciones son las mismas sin importar quién pide el turno
    private Turno crearTurno(Turno turno, TipoEvento tipoEvento) {
        validarFechaYHora(turno.getFecha(), turno.getHora());
        requerir(turno.getPaciente(), () -> new PacienteNoEncontradoException("Paciente no encontrado"));
        requerir(turno.getOdontologo(), () -> new OdontologoNoEncontradoException("Odontólogo no encontrado"));
        requerir(turno.getTratamiento(), () -> new DatoInvalidoException("Seleccione un tratamiento"));
        verificarDisponibilidad(turno.getOdontologo().getId(), turno.getFecha(), turno.getHora(), null);

        Turno guardado = repositorioTurno.registrar(turno);
        servicioPago.generarPago(guardado); // queda PENDIENTE, con el precio congelado
        notificarObservadores(new EventoTurno(tipoEvento, guardado));
        return guardado;
    }

    // ---------------- Cambios de estado ----------------

    public Turno confirmar(Long idTurno) {
        return cambiarEstado(idTurno, EstadoTurno.CONFIRMADO);
    }

    public Turno cancelar(Long idTurno) {
        return cambiarEstado(idTurno, EstadoTurno.CANCELADO);
    }

    public Turno cambiarEstado(Long idTurno, EstadoTurno estado) {
        requerir(estado, () -> new DatoInvalidoException("Seleccione un estado válido"));
        Turno turno = buscarPorId(idTurno);
        // Si "descancelo" un turno, su horario pudo haberlo tomado otro mientras estaba cancelado
        if (turno.getEstado() == EstadoTurno.CANCELADO && estado != EstadoTurno.CANCELADO) {
            verificarDisponibilidad(turno.getOdontologo().getId(), turno.getFecha(), turno.getHora(), idTurno);
        }
        return aplicarEstado(turno, estado, eventoPara(estado));
    }

    // El paciente solo puede cancelar SUS turnos, y solo si todavía no se atendieron
    public Turno cancelarPorPaciente(Long idTurno, Long idPaciente) {
        Turno turno = buscarPorId(idTurno);
        if (!turno.getPaciente().getId().equals(idPaciente)) {
            throw new DatoInvalidoException("Ese turno no es tuyo");
        }
        if (turno.getEstado() != EstadoTurno.PENDIENTE && turno.getEstado() != EstadoTurno.CONFIRMADO) {
            throw new DatoInvalidoException("Solo se pueden cancelar turnos pendientes o confirmados");
        }
        return aplicarEstado(turno, EstadoTurno.CANCELADO, TipoEvento.TURNO_CANCELADO_POR_PACIENTE);
    }

    private Turno aplicarEstado(Turno turno, EstadoTurno estado, TipoEvento tipoEvento) {
        turno.setEstado(estado);
        repositorioTurno.actualizar(turno); // no valida la fecha: cambiar el estado de un turno pasado es válido
        servicioPago.sincronizarConTurno(turno);
        notificarObservadores(new EventoTurno(tipoEvento, turno));
        return turno;
    }

    // ---------------- Reprogramar / eliminar ----------------

    public Turno reprogramar(Long idTurno, LocalDate nuevaFecha, LocalTime nuevaHora) {
        validarFechaYHora(nuevaFecha, nuevaHora);
        Turno turno = buscarPorId(idTurno);
        verificarDisponibilidad(turno.getOdontologo().getId(), nuevaFecha, nuevaHora, idTurno); // se excluye a sí mismo
        turno.setFecha(nuevaFecha);
        turno.setHora(nuevaHora);
        repositorioTurno.actualizar(turno);
        notificarObservadores(new EventoTurno(TipoEvento.TURNO_REPROGRAMADO, turno));
        return turno;
    }

    public void eliminar(Long id) {
        Turno turno = buscarPorId(id);
        servicioPago.eliminarPorTurno(id); // sin turno, el pago no tiene sentido
        repositorioTurno.eliminar(id);
        notificarObservadores(new EventoTurno(TipoEvento.TURNO_ELIMINADO, turno));
    }

    // ---------------- Consultas ----------------

    public Turno buscarPorId(Long idTurno) {
        return requerir(repositorioTurno.buscarPorId(idTurno), () -> new TurnoNoEncontradoException("Turno no encontrado"));
    }

    public List<Turno> listarTodos() {
        return repositorioTurno.listarTodos();
    }

    public List<Turno> listarPorPaciente(Long idPaciente) {
        return repositorioTurno.buscarPorPaciente(idPaciente);
    }

    public List<Turno> listarPorEstado(EstadoTurno estado) {
        return repositorioTurno.buscarPorEstado(estado);
    }

    // ---------------- Apoyo ----------------

    private void validarFechaYHora(LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            throw new DatoInvalidoException("La fecha y la hora no pueden ser nulas");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha del turno no puede ser anterior a hoy");
        }
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new DatoInvalidoException("Ese horario de hoy ya pasó");
        }
    }

    // El odontólogo no puede tener dos turnos a la misma hora. Los cancelados no cuentan (ese horario quedó libre).
    // idTurnoExcluido sirve para reprogramar: el turno que estoy moviendo no choca consigo mismo.
    private void verificarDisponibilidad(Long idOdontologo, LocalDate fecha, LocalTime hora, Long idTurnoExcluido) {
        List<Turno> turnosDelOdontologo = repositorioTurno.buscarPorOdontologo(idOdontologo);
        for (Turno turno : turnosDelOdontologo) {
            if (turno.getEstado() == EstadoTurno.CANCELADO) continue;
            if (turno.getId().equals(idTurnoExcluido)) continue;
            if (turno.getFecha().equals(fecha) && turno.getHora().equals(hora)) {
                throw new TurnoYaReservadoException("El odontólogo ya tiene un turno ese día a esa hora");
            }
        }
    }

    private TipoEvento eventoPara(EstadoTurno estado) {
        switch (estado) {
            case CONFIRMADO: return TipoEvento.TURNO_CONFIRMADO;
            case CANCELADO:  return TipoEvento.TURNO_CANCELADO;
            case COMPLETADO: return TipoEvento.TURNO_COMPLETADO;
            default:         return TipoEvento.TURNO_PENDIENTE;
        }
    }
}
