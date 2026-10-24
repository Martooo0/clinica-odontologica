package controlador;

import dominio.Tratamiento;
import excepcion.DatoInvalidoException;
import servicio.ServicioTratamiento;

import java.math.BigDecimal;
import java.util.List;

public class ControladorTratamiento extends ControladorObservable {

    private static final String REGEX_PRECIO_DECIMAL = "\\d+\\.\\d{1,2}";

    private final ServicioTratamiento servicioTratamiento;

    public ControladorTratamiento(ServicioTratamiento servicioTratamiento) {
        super(servicioTratamiento);
        this.servicioTratamiento = servicioTratamiento;
    }

    public Tratamiento registrar(String nombre, String precio) {
        return servicioTratamiento.registrar(new Tratamiento(nombre, leerPrecio(precio)));
    }

    public Tratamiento modificar(Long id, String nombre, String precio) {
        Tratamiento cambios = new Tratamiento(nombre, leerPrecio(precio));
        cambios.setId(id);
        return servicioTratamiento.modificar(cambios);
    }

    public void eliminar(Long id) {
        servicioTratamiento.eliminar(id);
    }

    public Tratamiento buscarPorId(Long id) {
        return servicioTratamiento.buscarPorId(id);
    }

    public List<Tratamiento> listarTodos() {
        return servicioTratamiento.listarTodos();
    }

    private BigDecimal leerPrecio(String texto) {
        String precio = texto.trim();
        if (!precio.matches(REGEX_PRECIO_DECIMAL)) {
            precio = precio.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(precio);
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("El precio tiene que ser un número (ej: 30000 o 30000,50).");
        }
    }
}
