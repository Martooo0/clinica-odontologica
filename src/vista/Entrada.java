package vista;

import excepcion.DatoInvalidoException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

final class Entrada {

    private static final String FECHA_U_HORA_INVALIDA = "Fecha u hora con formato inválido.\nFecha: aaaa-mm-dd   Hora: hh:mm";
    private static final String PRECIO_INVALIDO = "El precio tiene que ser un número (ej: 30000 o 30000,50).";
    private static final String REGEX_PRECIO_DECIMAL = "\\d+\\.\\d{1,2}";

    private Entrada() {}

    static LocalDate leerFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(FECHA_U_HORA_INVALIDA);
        }
    }

    static LocalTime leerHora(String texto) {
        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(FECHA_U_HORA_INVALIDA);
        }
    }

    static BigDecimal leerPrecio(String texto) {
        String precio = texto.trim();
        if (!precio.matches(REGEX_PRECIO_DECIMAL)) {
            precio = precio.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(precio);
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException(PRECIO_INVALIDO);
        }
    }
}
