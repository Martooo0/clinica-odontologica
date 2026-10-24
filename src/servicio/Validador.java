package servicio;

import dominio.Persona;
import excepcion.ClinicaException;
import excepcion.DatoInvalidoException;

import java.util.function.Supplier;

final class Validador {

    private Validador() {}

    static <T> T requerir(T valor, Supplier<? extends ClinicaException> error) {
        if (valor == null) {
            throw error.get();
        }
        return valor;
    }

    static void requerirTexto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    static void validarNombreYApellido(Persona persona) {
        requerir(persona, () -> new DatoInvalidoException("Ingrese un valor, por favor"));
        requerirTexto(persona.getNombre(), "Ingrese un nombre válido, por favor");
        requerirTexto(persona.getApellido(), "Ingrese un apellido válido, por favor");
    }
}
