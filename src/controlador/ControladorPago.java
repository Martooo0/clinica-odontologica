package controlador;

import dominio.Cotizacion;
import dominio.MedioPago;
import dominio.Paciente;
import dominio.Pago;
import dominio.Tratamiento;
import servicio.ServicioPago;

import java.math.BigDecimal;
import java.util.List;

public class ControladorPago extends ControladorObservable {

    private final ServicioPago servicioPago;

    public ControladorPago(ServicioPago servicioPago) {
        super(servicioPago);
        this.servicioPago = servicioPago;
    }

    public Cotizacion cotizar(Tratamiento tratamiento, Paciente paciente) {
        return servicioPago.cotizar(tratamiento, paciente);
    }

    public Pago registrarCobro(Long idPago, MedioPago medioPago) {
        return servicioPago.registrarCobro(idPago, medioPago);
    }

    public Pago buscarPorTurno(Long idTurno) {
        return servicioPago.buscarPorTurno(idTurno);
    }

    public List<Pago> listarTodos() {
        return servicioPago.listarTodos();
    }

    public BigDecimal totalCobrado() {
        return servicioPago.totalCobrado();
    }
}
