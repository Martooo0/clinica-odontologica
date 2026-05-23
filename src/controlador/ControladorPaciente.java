package controlador;

import dominio.Domicilio;
import dominio.Paciente;
import servicio.ServicioPaciente;
import excepcion.PacienteNoEncontradoException;

import java.util.List;

public class ControladorPaciente {

    private ServicioPaciente servicioPaciente;

    public ControladorPaciente(ServicioPaciente servicioPaciente) {
        this.servicioPaciente = servicioPaciente;
    }

    public Paciente registrar(String nombre, String apellido, String dni, String email, String calle, String numero, String localidad, String provincia) {
        Domicilio domicilio = new Domicilio(calle, numero,  localidad, provincia);
        Paciente paciente = new Paciente(nombre, apellido, dni, email, domicilio);
        return servicioPaciente.registrar(paciente);
    }

    public Paciente modificar(Long id, String nombre, String apellido, String email, String calle, String numero, String localidad, String provincia) { // no se pasa el dni porque es un tema "legal".
        Paciente paciente = servicioPaciente.buscarPorId(id); // Acá vienen los datos de paciente para que sean cambiados. Si no se cambian, quedan igual que antes
        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setEmail(email);
        paciente.setDomicilio(new Domicilio(calle, numero, localidad, provincia));
        return servicioPaciente.modificar(paciente);
    }

    public boolean eliminarPorId(Long id) {
        try {
            servicioPaciente.eliminar(id); // primero llamamos al metodo eliminar de servicio para ver si puede eliminar a este paciente
        } catch (PacienteNoEncontradoException e) { // si no pudo, tira la excepcion y el controller da false
            return false;
        }
        return true; // si lo borró, o sea lo encontró, tira true
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
}