package dominio;

import excepcion.DatoInvalidoException;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno implements Identificable {

    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private Paciente paciente;
    private Odontologo odontologo;
    private Tratamiento tratamiento;
    private EstadoTurno estado;

    public Turno(LocalDate fecha, LocalTime hora, Paciente paciente, Odontologo odontologo, Tratamiento tratamiento) {
        this.fecha = fecha;
        this.hora = hora;
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.tratamiento = tratamiento;
        this.estado = EstadoTurno.PENDIENTE;
    }

    public Turno() {}

    @Override
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

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    // Para mostrar en tablas sin repetir el chequeo de null en cada ventana
    public String getNombreTratamiento() {
        return tratamiento == null ? Constantes.VALOR_VACIO : tratamiento.getNombre();
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    @Override
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

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    public void cambiarEstado(EstadoTurno nuevoEstado) {
        if (!estado.puedePasarA(nuevoEstado)) {
            throw new DatoInvalidoException("Un turno " + estado + " no puede pasar a " + nuevoEstado);
        }
        this.estado = nuevoEstado;
    }

    @Override
    public String toString() {
        return "Turno [id=" + id + "]"
                + " - Paciente: " + paciente.getNombreCompleto()
                + " - Odontólogo: " + odontologo.getNombreCompleto()
                + " - Tratamiento: " + getNombreTratamiento()
                + " - Fecha: " + fecha + " a las " + hora
                + " - Estado: " + estado;
    }
}