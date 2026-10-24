package dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;


public enum ObraSocial {

    PARTICULAR("Particular", 0),
    OSDE("OSDE", 10),
    SWISS_MEDICAL("Swiss Medical", 15),
    GALENO("Galeno", 20),
    MEDIFE("Medifé", 25),
    OSECAC("OSECAC", 30),
    IOMA("IOMA", 50),
    PAMI("PAMI", 70);

    private final String nombre;
    private final int porcentajeDescuento;

    ObraSocial(String nombre, int porcentajeDescuento) {
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public BigDecimal calcularDescuento(BigDecimal precio) {
        return precio.multiply(BigDecimal.valueOf(porcentajeDescuento))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return nombre + " (" + porcentajeDescuento + "%)";
    }
}
