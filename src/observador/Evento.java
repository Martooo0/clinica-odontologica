package observador;

import dominio.Paciente;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class Evento {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final TipoEvento tipo;
    private final LocalTime momento = LocalTime.now();

    protected Evento(TipoEvento tipo) {
        this.tipo = tipo;
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
