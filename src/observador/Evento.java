package observador;

import dominio.Odontologo;
import dominio.Paciente;
import dominio.Tratamiento;
import dominio.Turno;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class Evento {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final TipoEvento tipo;
    private final LocalTime momento = LocalTime.now();

    protected Evento(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public static Evento deTurno(TipoEvento tipo, Turno turno) {
        return new EventoTurno(tipo, turno);
    }

    public static Evento dePaciente(TipoEvento tipo, Paciente paciente) {
        return new EventoPaciente(tipo, paciente);
    }

    public static Evento deOdontologo(TipoEvento tipo, Odontologo odontologo) {
        return new EventoOdontologo(tipo, odontologo);
    }

    public static Evento deTratamiento(TipoEvento tipo, Tratamiento tratamiento) {
        return new EventoTratamiento(tipo, tratamiento);
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public TipoEvento.Categoria getCategoria() {
        return tipo.getCategoria();
    }

    public boolean involucraA(Paciente paciente) {
        return false;
    }

    public String getMensaje() {
        return "[" + momento.format(HORA) + "] " + tipo.getDescripcion() + " — " + getDetalle();
    }

    protected abstract String getDetalle();
}
