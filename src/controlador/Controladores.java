package controlador;

public class Controladores {

    private final ControladorPaciente controladorPaciente;
    private final ControladorOdontologo controladorOdontologo;
    private final ControladorTurno controladorTurno;
    private final ControladorTratamiento controladorTratamiento;
    private final ControladorPago controladorPago;

    public Controladores(ControladorPaciente controladorPaciente, ControladorOdontologo controladorOdontologo,
                         ControladorTurno controladorTurno, ControladorTratamiento controladorTratamiento,
                         ControladorPago controladorPago) {
        this.controladorPaciente = controladorPaciente;
        this.controladorOdontologo = controladorOdontologo;
        this.controladorTurno = controladorTurno;
        this.controladorTratamiento = controladorTratamiento;
        this.controladorPago = controladorPago;
    }

    public ControladorPaciente getControladorPaciente() {
        return controladorPaciente;
    }

    public ControladorOdontologo getControladorOdontologo() {
        return controladorOdontologo;
    }

    public ControladorTurno getControladorTurno() {
        return controladorTurno;
    }

    public ControladorTratamiento getControladorTratamiento() {
        return controladorTratamiento;
    }

    public ControladorPago getControladorPago() {
        return controladorPago;
    }
}
