package repositorio;

import dominio.Turno;
import dominio.Paciente;
import dominio.Odontologo;
import dominio.EstadoTurno;
import dominio.Tratamiento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RepositorioTurno extends RepositorioBase<Turno> {

    private static final String ARCHIVO = "turnos.txt";
    private static final int ID = 0;
    private static final int PACIENTE = 1;
    private static final int ODONTOLOGO = 2;
    private static final int FECHA = 3;
    private static final int HORA = 4;
    private static final int ESTADO = 5;
    private static final int TRATAMIENTO = 6;

    private final RepositorioPaciente repositorioPaciente;
    private final RepositorioOdontologo repositorioOdontologo;
    private final RepositorioTratamiento repositorioTratamiento;

    public RepositorioTurno(RepositorioPaciente repositorioPaciente, RepositorioOdontologo repositorioOdontologo,
                            RepositorioTratamiento repositorioTratamiento) {
        this.repositorioPaciente = repositorioPaciente;
        this.repositorioOdontologo = repositorioOdontologo;
        this.repositorioTratamiento = repositorioTratamiento;
    }

    public List<Turno> buscarPorPaciente(Long idPaciente) {
        return filtrar(turno -> turno.getPaciente().getId().equals(idPaciente));
    }

    public List<Turno> buscarPorOdontologo(Long idOdontologo) {
        return filtrar(turno -> turno.getOdontologo().getId().equals(idOdontologo));
    }

    public List<Turno> buscarPorTratamiento(Long idTratamiento) {
        return filtrar(turno -> turno.getTratamiento() != null && turno.getTratamiento().getId().equals(idTratamiento));
    }

    public List<Turno> buscarPorEstado(EstadoTurno estado) {
        return filtrar(turno -> turno.getEstado() == estado);
    }

    @Override
    protected String getArchivo() {
        return ARCHIVO;
    }

    @Override
    protected String serializar(Turno turno) {
        Object idTratamiento = turno.getTratamiento() == null ? VACIO : turno.getTratamiento().getId();
        return unir(turno.getId(), turno.getPaciente().getId(), turno.getOdontologo().getId(),
                turno.getFecha(), turno.getHora(), turno.getEstado().name(), idTratamiento);
    }

    @Override
    protected Turno parsearLinea(String[] partes) {
        // Convierto los IDs de vuelta en objetos pidiéndolos a los otros repos
        Paciente paciente = repositorioPaciente.buscarPorId(Long.parseLong(partes[PACIENTE]));
        Odontologo odontologo = repositorioOdontologo.buscarPorId(Long.parseLong(partes[ODONTOLOGO]));

        // Si el paciente u odontólogo ya no existen, descarto el turno (no apunta a nada)
        if (paciente == null || odontologo == null) return null;

        Tratamiento tratamiento = null;
        if (partes.length > TRATAMIENTO && !partes[TRATAMIENTO].equals(VACIO)) {
            tratamiento = repositorioTratamiento.buscarPorId(Long.parseLong(partes[TRATAMIENTO]));
        }

        Turno turno = new Turno(LocalDate.parse(partes[FECHA]), LocalTime.parse(partes[HORA]),
                paciente, odontologo, tratamiento);
        turno.setId(Long.parseLong(partes[ID]));
        turno.setEstado(EstadoTurno.valueOf(partes[ESTADO])); // pisa el PENDIENTE del constructor
        return turno;
    }
}
