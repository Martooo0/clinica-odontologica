package dto;

import dominio.Odontologo;
import dominio.Paciente;
import dominio.Tratamiento;

public class SolicitudTurno {

    private final Paciente paciente;
    private final Odontologo odontologo;
    private final Tratamiento tratamiento;
    private final String fecha;
    private final String hora;

    public SolicitudTurno(Paciente paciente, Odontologo odontologo, Tratamiento tratamiento,
                          String fecha, String hora) {
        this.paciente = paciente;
        this.odontologo = odontologo;
        this.tratamiento = tratamiento;
        this.fecha = fecha;
        this.hora = hora;
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

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }
}
