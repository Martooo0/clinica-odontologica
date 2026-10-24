package dominio;

import java.math.BigDecimal;

public class Cotizacion {

    private final Tratamiento tratamiento;
    private final ObraSocial obraSocial;
    private final BigDecimal precioLista;
    private final BigDecimal descuento;

    public Cotizacion(Tratamiento tratamiento, ObraSocial obraSocial) {
        this.tratamiento = tratamiento;
        this.obraSocial = obraSocial;
        this.precioLista = tratamiento.getPrecio();
        this.descuento = obraSocial.calcularDescuento(precioLista); // la obra social sabe cuánto descuenta
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public ObraSocial getObraSocial() {
        return obraSocial;
    }

    public BigDecimal getPrecioLista() {
        return precioLista;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public BigDecimal getTotal() {
        return precioLista.subtract(descuento);
    }
}
