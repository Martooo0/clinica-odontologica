package servicio;

import dominio.Tratamiento;
import excepcion.DatoInvalidoException;
import excepcion.TratamientoNoEncontradoException;
import observador.EventoTratamiento;
import observador.Sujeto;
import observador.TipoEvento;
import repositorio.RepositorioTratamiento;
import repositorio.RepositorioTurno;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static servicio.Validador.requerir;
import static servicio.Validador.requerirTexto;

/**
 * Hereda de Sujeto (Observer): avisa cuando se agrega, modifica o elimina un tratamiento,
 * así el desplegable de la ventana del paciente muestra siempre el catálogo y los precios actuales.
 */
public class ServicioTratamiento extends Sujeto {

    private final RepositorioTratamiento repositorio;
    private final RepositorioTurno repositorioTurno; // para no borrar un tratamiento que usan los turnos

    public ServicioTratamiento(RepositorioTratamiento repositorio, RepositorioTurno repositorioTurno) {
        this.repositorio = repositorio;
        this.repositorioTurno = repositorioTurno;
    }

    public Tratamiento registrar(Tratamiento tratamiento) {
        validar(tratamiento);
        if (repositorio.buscarPorNombre(tratamiento.getNombre()) != null) {
            throw new DatoInvalidoException("Ya existe un tratamiento con ese nombre");
        }
        tratamiento.setNombre(tratamiento.getNombre().trim());
        Tratamiento guardado = repositorio.registrar(tratamiento);
        notificarObservadores(new EventoTratamiento(TipoEvento.TRATAMIENTO_REGISTRADO, guardado));
        return guardado;
    }

    // Recibe un Tratamiento con los datos nuevos y el id del que hay que cambiar.
    // Primero valida todo y recién después modifica el objeto ORIGINAL (no lo reemplaza):
    // así los turnos que ya apuntan a este tratamiento ven el cambio de nombre.
    // Los PAGOS ya generados no cambian, porque tienen el precio copiado.
    public Tratamiento modificar(Tratamiento cambios) {
        validar(cambios);
        Tratamiento conMismoNombre = repositorio.buscarPorNombre(cambios.getNombre());
        if (conMismoNombre != null && !conMismoNombre.getId().equals(cambios.getId())) {
            throw new DatoInvalidoException("Ya existe otro tratamiento con ese nombre");
        }
        Tratamiento original = buscarPorId(cambios.getId());
        original.setNombre(cambios.getNombre().trim());
        original.setPrecio(cambios.getPrecio());
        Tratamiento actualizado = repositorio.actualizar(original);
        notificarObservadores(new EventoTratamiento(TipoEvento.TRATAMIENTO_MODIFICADO, actualizado));
        return actualizado;
    }

    public void eliminar(Long id) {
        Tratamiento tratamiento = buscarPorId(id);
        if (!repositorioTurno.buscarPorTratamiento(id).isEmpty()) {
            throw new DatoInvalidoException("No se puede eliminar: hay turnos con este tratamiento");
        }
        repositorio.eliminar(id);
        notificarObservadores(new EventoTratamiento(TipoEvento.TRATAMIENTO_ELIMINADO, tratamiento));
    }

    public Tratamiento buscarPorId(Long id) {
        return requerir(repositorio.buscarPorId(id),
                () -> new TratamientoNoEncontradoException("El tratamiento no está en el sistema"));
    }

    // Ordenados por nombre, así el desplegable del paciente queda prolijo
    public List<Tratamiento> listarTodos() {
        List<Tratamiento> tratamientos = repositorio.listarTodos();
        tratamientos.sort(Comparator.comparing(Tratamiento::getNombre));
        return tratamientos;
    }

    // La primera vez que se abre el sistema (no hay tratamientos.txt) carga un catálogo de ejemplo.
    // Los precios son inventados: el secretario los puede cambiar desde "Gestionar Tratamientos".
    public void inicializarCatalogo() {
        if (!repositorio.listarTodos().isEmpty()) return;
        repositorio.registrar(new Tratamiento("Consulta y diagnóstico", new BigDecimal("15000")));
        repositorio.registrar(new Tratamiento("Limpieza dental", new BigDecimal("30000")));
        repositorio.registrar(new Tratamiento("Arreglo de caries", new BigDecimal("45000")));
        repositorio.registrar(new Tratamiento("Extracción", new BigDecimal("55000")));
        repositorio.registrar(new Tratamiento("Blanqueamiento", new BigDecimal("90000")));
        repositorio.registrar(new Tratamiento("Endodoncia", new BigDecimal("120000")));
        repositorio.registrar(new Tratamiento("Colocación de brackets", new BigDecimal("350000")));
        repositorio.registrar(new Tratamiento("Control de brackets", new BigDecimal("25000")));
    }

    private void validar(Tratamiento tratamiento) {
        requerir(tratamiento, () -> new DatoInvalidoException("Ingrese un valor, por favor"));
        requerirTexto(tratamiento.getNombre(), "Ingrese un nombre de tratamiento válido, por favor");
        if (tratamiento.getNombre().contains(";")) {
            throw new DatoInvalidoException("El nombre no puede contener ';'"); // rompería el archivo .txt
        }
        if (tratamiento.getPrecio() == null || tratamiento.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DatoInvalidoException("El precio tiene que ser mayor a cero");
        }
    }
}
