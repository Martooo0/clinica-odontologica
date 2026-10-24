package controlador;

import dominio.Paciente;
import dto.DatosPaciente;
import servicio.ServicioPaciente;

import java.util.List;

public class ControladorPaciente extends ControladorObservable {

    private final ServicioPaciente servicioPaciente;

    public ControladorPaciente(ServicioPaciente servicioPaciente) {
        super(servicioPaciente);
        this.servicioPaciente = servicioPaciente;
    }

    public Paciente registrar(DatosPaciente datos) {
        return servicioPaciente.registrar(crearPaciente(datos));
    }

    public Paciente modificar(Long id, DatosPaciente datos) {
        Paciente cambios = crearPaciente(datos);
        cambios.setId(id);
        return servicioPaciente.modificar(cambios);
    }

    public void eliminar(Long id) {
        servicioPaciente.eliminar(id);
    }

    public Paciente buscarPorId(Long id) {
        return servicioPaciente.buscarPorId(id);
    }

    public Paciente buscarPorDni(String dni){
        return servicioPaciente.buscarPorDni(dni);
    }

    public List<Paciente> listarTodos() {
        return servicioPaciente.listarTodos();
    }

    public List<Paciente> listarPorApellido() {
        return servicioPaciente.listarPorApellido();
    }

    private Paciente crearPaciente(DatosPaciente datos) {
        return new Paciente(datos.getNombre(), datos.getApellido(), datos.getDni(), datos.getEmail(),
                datos.getDomicilio(), datos.getObraSocial());
    }
}
