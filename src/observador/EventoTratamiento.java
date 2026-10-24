package observador;

import dominio.Tratamiento;

public class EventoTratamiento extends Evento {

    private final Tratamiento tratamiento;

    public EventoTratamiento(TipoEvento tipo, Tratamiento tratamiento) {
        super(tipo);
        this.tratamiento = tratamiento;
    }

    @Override
    protected String getDetalle() {
        return tratamiento.getNombre() + " ($" + tratamiento.getPrecio().toPlainString() + ")";
    }
}
