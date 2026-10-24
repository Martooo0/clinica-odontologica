package repositorio;

import dominio.Constantes;
import dominio.Identificable;
import excepcion.PersistenciaException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class RepositorioBase<T extends Identificable> implements IRepositorio<T> {

    protected static final String SEPARADOR = ";";
    protected static final String VACIO = Constantes.VALOR_VACIO;

    private final Map<Long, T> entidades = new HashMap<>();
    private Long contadorId = 0L;

    @Override
    public T registrar(T entidad) {
        entidad.setId(++contadorId);
        entidades.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public T buscarPorId(Long id) {
        return entidades.get(id);
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(entidades.values());
    }

    @Override
    public T actualizar(T entidad) {
        entidades.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public void eliminar(Long id) {
        entidades.remove(id);
    }

    protected List<T> filtrar(Predicate<T> condicion) {
        return entidades.values().stream()
                .filter(condicion)
                .collect(Collectors.toList());
    }

    protected T buscarPrimero(Predicate<T> condicion) {
        return entidades.values().stream()
                .filter(condicion)
                .findFirst()
                .orElse(null);
    }

    public void guardarTodos() {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(getArchivo()))) {
            for (T entidad : entidades.values()) {
                escritor.println(serializar(entidad));
            }
            if (escritor.checkError()) {
                throw new PersistenciaException("Error al guardar " + getArchivo());
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar " + getArchivo() + ": " + e.getMessage());
        }
    }

    public void cargar() {
        File fichero = new File(getArchivo());
        if (!fichero.exists()) return;

        try (BufferedReader lector = new BufferedReader(new FileReader(fichero))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                T entidad = parsearLinea(linea.split(SEPARADOR));
                if (entidad == null) continue;

                entidades.put(entidad.getId(), entidad);
                if (entidad.getId() > contadorId) {
                    contadorId = entidad.getId();
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer " + getArchivo() + ": " + e.getMessage());
        }
    }

    protected String unir(Object... valores) {
        return Arrays.stream(valores)
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARADOR));
    }

    protected abstract String getArchivo();

    protected abstract String serializar(T entidad);

    protected abstract T parsearLinea(String[] partes);
}
