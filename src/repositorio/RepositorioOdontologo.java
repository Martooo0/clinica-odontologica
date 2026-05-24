package repositorio;

import dominio.Odontologo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioOdontologo implements IRepositorio<Odontologo> {

    private Map<Long, Odontologo> odontologos = new HashMap<>();
    private Long contadorId = 0L;

    @Override
    public Odontologo guardar(Odontologo entidad) {
        entidad.setId(++contadorId);
        odontologos.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Odontologo buscarPorId(Long id) {
        return odontologos.get(id);
    }

    @Override
    public List<Odontologo> buscarTodos() {
        return new ArrayList<>(odontologos.values());
    }

    @Override
    public Odontologo actualizar(Odontologo entidad) {
        odontologos.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public void eliminar(Long id) {
        odontologos.remove(id);
    }

    public Odontologo buscarPorMatricula(String matricula) {
        return odontologos.values().stream()
                .filter(o -> o.getMatricula().equals(matricula))
                .findFirst()
                .orElse(null);
    }
}