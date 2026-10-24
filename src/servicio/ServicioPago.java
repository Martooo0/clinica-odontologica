package servicio;

import dominio.Cotizacion;
import dominio.EstadoPago;
import dominio.EstadoTurno;
import dominio.MedioPago;
import dominio.ObraSocial;
import dominio.Paciente;
import dominio.Pago;
import dominio.Tratamiento;
import dominio.Turno;
import excepcion.DatoInvalidoException;
import excepcion.PagoNoEncontradoException;
import observador.Evento;
import observador.Sujeto;
import observador.TipoEvento;
import repositorio.RepositorioPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static servicio.Validador.requerir;

/**
 * Reglas de los pagos. Hereda de Sujeto para poder avisar "se registró un pago"
 * (el paciente lo ve en sus notificaciones).
 */
public class ServicioPago extends Sujeto {

    private final RepositorioPago repositorio;

    public ServicioPago(RepositorioPago repositorio) {
        this.repositorio = repositorio;
    }

    // Arma el presupuesto: precio de lista - descuento de la obra social = total
    public Cotizacion cotizar(Tratamiento tratamiento, Paciente paciente) {
        requerir(tratamiento, () -> new DatoInvalidoException("Seleccione un tratamiento"));
        ObraSocial obraSocial = paciente == null ? ObraSocial.PARTICULAR : paciente.getObraSocial();
        return new Cotizacion(tratamiento, obraSocial);
    }

    // Se llama cuando se crea un turno: deja el pago PENDIENTE con los montos congelados
    public Pago generarPago(Turno turno) {
        if (turno.getTratamiento() == null) {
            throw new DatoInvalidoException("El turno no tiene tratamiento: no se puede generar el pago");
        }
        Cotizacion cotizacion = cotizar(turno.getTratamiento(), turno.getPaciente());
        return repositorio.registrar(new Pago(turno, cotizacion));
    }

    // El secretario cobra: el pago pasa a PAGADO con el medio y la fecha de hoy
    public Pago registrarCobro(Long idPago, MedioPago medioPago) {
        requerir(medioPago, () -> new DatoInvalidoException("Seleccione un medio de pago"));
        Pago pago = buscarPorId(idPago);
        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new DatoInvalidoException("Solo se pueden cobrar pagos pendientes (este está " + pago.getEstado() + ")");
        }
        pago.setEstado(EstadoPago.PAGADO);
        pago.setMedioPago(medioPago);
        pago.setFechaPago(LocalDate.now());
        repositorio.actualizar(pago);
        notificarObservadores(Evento.deTurno(TipoEvento.PAGO_REGISTRADO, pago.getTurno()));
        return pago;
    }

    // Mantiene el pago coherente con el estado del turno:
    // turno cancelado -> pago pendiente se anula; turno "descancelado" -> pago anulado vuelve a pendiente.
    // Un pago que ya se cobró no se toca.
    public void sincronizarConTurno(Turno turno) {
        Pago pago = repositorio.buscarPorTurno(turno.getId());
        if (pago == null) return; // turnos viejos, de antes de los pagos

        turno.getEstado().actualizarPago(pago);
        repositorio.actualizar(pago);
    }

    public void eliminarPorTurno(Long idTurno) {
        Pago pago = repositorio.buscarPorTurno(idTurno);
        if (pago != null) {
            repositorio.eliminar(pago.getId());
        }
    }

    public Pago buscarPorId(Long idPago) {
        return requerir(repositorio.buscarPorId(idPago), () -> new PagoNoEncontradoException("El pago no está en el sistema"));
    }

    // Devuelve null si el turno no tiene pago (turnos viejos). No lanza excepción porque es un caso válido.
    public Pago buscarPorTurno(Long idTurno) {
        return repositorio.buscarPorTurno(idTurno);
    }

    public List<Pago> listarTodos() {
        return repositorio.listarTodos();
    }

    public BigDecimal totalCobrado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Pago pago : repositorio.listarTodos()) {
            if (pago.getEstado() == EstadoPago.PAGADO) {
                total = total.add(pago.getMontoFinal());
            }
        }
        return total;
    }
}
