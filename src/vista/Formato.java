package vista;

import dominio.Constantes;
import dominio.Pago;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Formato {

    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));

    private Formato() {} // no se instancia: solo tiene métodos static

    public static String moneda(BigDecimal monto) {
        if (monto == null) return Constantes.VALOR_VACIO;
        return MONEDA.format(monto);
    }

    public static String montoDe(Pago pago) {
        return pago == null ? Constantes.VALOR_VACIO : moneda(pago.getMontoFinal());
    }

    public static Object estadoDe(Pago pago) {
        return pago == null ? Constantes.VALOR_VACIO : pago.getEstado();
    }

    public static Object valorDe(Object valor) {
        return valor == null ? Constantes.VALOR_VACIO : valor;
    }
}
