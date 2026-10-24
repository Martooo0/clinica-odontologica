package dominio;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pago implements Identificable {

    private Long id;
    private Turno turno;
    private BigDecimal precioLista;
    private ObraSocial obraSocial;          // la obra social que tenía el paciente al pedir el turno
    private int porcentajeDescuento;        // el % que tenía esa obra social en ese momento
    private BigDecimal descuento;
    private BigDecimal montoFinal;
    private EstadoPago estado;
    private MedioPago medioPago;            // null hasta que se cobra
    private LocalDate fechaPago;            // null hasta que se cobra

    public Pago() {}

    public Pago(Turno turno, Cotizacion cotizacion) {
        this.turno = turno;
        this.precioLista = cotizacion.getPrecioLista();
        this.obraSocial = cotizacion.getObraSocial();
        this.porcentajeDescuento = cotizacion.getObraSocial().getPorcentajeDescuento();
        this.descuento = cotizacion.getDescuento();
        this.montoFinal = cotizacion.getTotal();
        this.estado = EstadoPago.PENDIENTE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Turno getTurno() {
        return turno;
    }

    public BigDecimal getPrecioLista() {
        return precioLista;
    }

    public ObraSocial getObraSocial() {
        return obraSocial;
    }

    public int getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public BigDecimal getMontoFinal() {
        return montoFinal;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public void setPrecioLista(BigDecimal precioLista) {
        this.precioLista = precioLista;
    }

    public void setObraSocial(ObraSocial obraSocial) {
        this.obraSocial = obraSocial;
    }

    public void setPorcentajeDescuento(int porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    @Override
    public String toString() {
        return "Pago [id=" + id + "] turno " + turno.getId() + " - $" + montoFinal + " - " + estado;
    }
}
