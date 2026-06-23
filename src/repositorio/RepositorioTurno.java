package repositorio;

import dominio.Turno;
import dominio.Paciente;
import dominio.Odontologo;
import dominio.EstadoTurno;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositorioTurno implements IRepositorio<Turno> {

    private Map<Long, Turno> turnos = new HashMap<>();
    private Long contadorId = 0L;
    private static final String ARCHIVO = "turnos.txt";
    private static final String SEP = ";";

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

    public void guardarTodos() {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(ARCHIVO);
            pw = new PrintWriter(fw);
            for (Turno t : turnos.values()) {
                // Guardo los IDs de paciente y odontólogo, NO los objetos enteros (para no duplicar datos... no sé si está bien)
                pw.println(t.getId() + SEP + t.getPaciente().getId() + SEP + t.getOdontologo().getId()
                        + SEP + t.getFecha() + SEP + t.getHora() + SEP + t.getEstado().name());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar turnos: " + e.getMessage());
        } finally {
            if (pw != null) pw.close();
        }
    }

    // Recibe los otros dos repos para poder resolver los IDs a objetos reales
    public void cargar(RepositorioPaciente repositorioPaciente, RepositorioOdontologo repositorioOdontologo) {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return;

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(ARCHIVO);
            br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(SEP);

                // Convierto los IDs de vuelta en objetos pidiéndolos a los otros repos
                Paciente paciente = repositorioPaciente.buscarPorId(Long.parseLong(partes[1]));
                Odontologo odontologo = repositorioOdontologo.buscarPorId(Long.parseLong(partes[2]));

                // Si el paciente u odontólogo ya no existen, descarto el turno (no apunta a nada)
                if (paciente == null || odontologo == null) continue;

                Turno turno = new Turno(LocalDate.parse(partes[3]), LocalTime.parse(partes[4]), paciente, odontologo);
                turno.setId(Long.parseLong(partes[0]));
                turno.setEstado(EstadoTurno.valueOf(partes[5])); // pisa el PENDIENTE del constructor

                turnos.put(turno.getId(), turno);

                if (turno.getId() > contadorId) {
                    contadorId = turno.getId();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer turnos: " + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el archivo: " + e.getMessage());
                }
            }
        }
    }
}
