package dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SolicitudTurno {

    private final Long idPaciente;
    private final Long idOdontologo;
    private final Long idTratamiento;
    private final LocalDate fecha;
    private final LocalTime hora;

    public SolicitudTurno(Long idPaciente, Long idOdontologo, Long idTratamiento, LocalDate fecha, LocalTime hora) {
        this.idPaciente = idPaciente;
        this.idOdontologo = idOdontologo;
        this.idTratamiento = idTratamiento;
        this.fecha = fecha;
        this.hora = hora;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public Long getIdOdontologo() {
        return idOdontologo;
    }

    public Long getIdTratamiento() {
        return idTratamiento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }
}
