package repositorio;

import dominio.Paciente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioPaciente implements IRepositorio<Paciente> {

    private Map<Long, Paciente> pacientes = new HashMap<>();
    private Long contadorId = 0L;

    // Le damos el id al paciente
    @Override
    public Paciente guardar(Paciente entidad) {
        entidad.setId(++contadorId); // Primero aumenta el contador, luego asigna.
        pacientes.put(entidad.getId(), entidad); // clave = id | valor = lo demás
        return entidad;
    }

    @Override
    public Paciente buscarPorId(Long id) {
        return pacientes.get(id);
    }

    // Devuelvo todos los pacientes como una List. Por eso los meto adentro de un ArrayList nuevo: lo convierte a List y además hace una copia, así si alguien toca la lista que devuelvo, no debería pasar nada con el HashMap.
    @Override
    public List<Paciente> buscarTodos() {
        return new ArrayList<>(pacientes.values());
    }

    @Override
    public Paciente actualizar(Paciente entidad) {
        pacientes.put(entidad.getId(), entidad); // Si existe, lo reemplaza. Si no, la agrega. Igual service no lo va a dejar por PacienteNoEncontrado.
        return entidad;
    }

    @Override
    public void eliminar(Long id) {
        pacientes.remove(id);
    }

    public Paciente buscarPorDni(String dni){
        return pacientes.values().stream()
                .filter(p -> p.getDni().equals(dni))
                .findFirst()
                .orElse(null);
    }
}