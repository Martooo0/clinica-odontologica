package observador;

import dominio.Tratamiento;

class EventoTratamiento extends Evento {

    private final Tratamiento tratamiento;

    EventoTratamiento(TipoEvento tipo, Tratamiento tratamiento) {
        super(tipo);
        this.tratamiento = tratamiento;
    }

    @Override
    protected String getDetalle() {
        return tratamiento.getNombre() + " ($" + tratamiento.getPrecio().toPlainString() + ")";
    }
}
