package dominio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Turno {

    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private Paciente paciente;
    private Odontologo odontologo;
    private EstadoTurno estado;

    public Turno(LocalDate fecha, LocalTime hora, Paciente paciente, Odontologo odontologo) {
        this.fecha = fecha;
        this.hora = hora;
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.estado = EstadoTurno.PENDIENTE;
    }

    public Turno() {}

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Odontologo getOdontologo() {
        return odontologo;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Turno [id=" + id + "]"
                + " - Paciente: " + paciente.getNombreCompleto()
                + " - Odontólogo: " + odontologo.getNombreCompleto()
                + " - Fecha: " + fecha + " a las " + hora
                + " - Estado: " + estado;
    }

    public Boolean esFuturo() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime momentoDelTurno = LocalDateTime.of(fecha, hora);
        return momentoDelTurno.isAfter(ahora);
    }
}