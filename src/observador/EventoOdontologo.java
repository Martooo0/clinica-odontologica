package observador;

import dominio.Odontologo;

public class EventoOdontologo extends Evento {

    private final Odontologo odontologo;

    public EventoOdontologo(TipoEvento tipo, Odontologo odontologo) {
        super(tipo);
        this.odontologo = odontologo;
    }

    @Override
    protected String getDetalle() {
        return odontologo.getNombreCompleto() + " (Mat. " + odontologo.getMatricula() + ")";
    }
}
