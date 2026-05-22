package repositorio;

import dominio.Turno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioTurno implements IRepositorio<Turno> {

    private Map<Long, Turno> turnos = new HashMap<>();
    private Long contadorId = 0L;

    @Override
    public Turno guardar(Turno entidad) {
        entidad.setId(++contadorId);
        turnos.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Turno buscarPorId(Long id) {
        return turnos.get(id);
    }

    @Override
    public List<Turno> buscarTodos() {
        return new ArrayList<>(turnos.values());
    }

    @Override
    public Turno actualizar(Turno entidad) {
        turnos.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public void eliminar(Long id) {
        turnos.remove(id);
    }

    public List<Turno> buscarPorPaciente(Long idPaciente) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos.values()) {
            if (t.getPaciente().getId().equals(idPaciente)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public List<Turno> buscarPorOdontologo(Long idOdontologo) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos.values()) {
            if (t.getOdontologo().getId().equals(idOdontologo)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public List<Turno> buscarPorFecha(LocalDate fecha) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos.values()) {
            if (t.getFecha().equals(fecha)) {
                resultado.add(t);
            }
        }
        return resultado;
    }
}
