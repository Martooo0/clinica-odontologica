package observador;

import dominio.Paciente;
import dominio.Turno;

class EventoTurno extends Evento {

    private final Turno turno;

    EventoTurno(TipoEvento tipo, Turno turno) {
        super(tipo);
        this.turno = turno;
    }

    @Override
    public boolean involucraA(Paciente paciente) {
        return turno.getPaciente().getId().equals(paciente.getId());
    }

    @Override
    protected String getDetalle() {
        return "Turno #" + turno.getId()
                + ": " + turno.getPaciente().getNombreCompleto()
                + ", " + turno.getNombreTratamiento()
                + ", " + turno.getFecha() + " " + turno.getHora();
    }
}
