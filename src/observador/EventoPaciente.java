package observador;

import dominio.Paciente;

class EventoPaciente extends Evento {

    private final Paciente paciente;

    EventoPaciente(TipoEvento tipo, Paciente paciente) {
        super(tipo);
        this.paciente = paciente;
    }

    @Override
    public boolean involucraA(Paciente otro) {
        return paciente.getId().equals(otro.getId());
    }

    @Override
    protected String getDetalle() {
        return paciente.getNombreCompleto() + " (DNI " + paciente.getDni()
                + ", " + paciente.getObraSocial().getNombre() + ")";
    }
}
