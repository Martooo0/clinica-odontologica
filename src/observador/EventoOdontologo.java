package observador;

import dominio.Odontologo;

class EventoOdontologo extends Evento {

    private final Odontologo odontologo;

    EventoOdontologo(TipoEvento tipo, Odontologo odontologo) {
        super(tipo);
        this.odontologo = odontologo;
    }

    @Override
    protected String getDetalle() {
        return odontologo.getNombreCompleto() + " (Mat. " + odontologo.getMatricula() + ")";
    }
}
