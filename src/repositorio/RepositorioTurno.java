package repositorio;

import dominio.Turno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return turnos.values().stream()
                .filter(t -> t.getPaciente().getId().equals(idPaciente))
                .collect(Collectors.toList());
    }

    public List<Turno> buscarPorOdontologo(Long idOdontologo) {
        return turnos.values().stream()
                .filter(t -> t.getOdontologo().getId().equals(idOdontologo))
                .collect(Collectors.toList());
    }

    public List<Turno> buscarPorFecha(LocalDate fecha) {
        return turnos.values().stream()
                .filter(t -> t.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<Turno> buscarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        return turnos.values().stream()
                .filter(t -> !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }
}
