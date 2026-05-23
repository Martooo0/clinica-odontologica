package servicio;

import dominio.Paciente;
import excepcion.DniDuplicadoException;
import excepcion.DatoInvalidoException;
import excepcion.PacienteNoEncontradoException;
import repositorio.RepositorioPaciente;

import java.util.List;

public class ServicioPaciente {

    private RepositorioPaciente repositorio;

    public ServicioPaciente(RepositorioPaciente repositorio) {
        this.repositorio = repositorio;
    }

    public Paciente registrar(Paciente paciente) {
        if (paciente.getDni() == null || paciente.getDni().isEmpty()) { // Se fija que haya algún dato
            throw new DatoInvalidoException("Intente nuevamente, por favor: ");
        }
        Paciente existe = repositorio.buscarPorDni(paciente.getDni()); // el repositorio busca a ver si tiene ese dato guardado y lo guarda en una variable
        if (existe != null) { // Si tiene algo la variable, está duplicado
            throw new DniDuplicadoException("El DNI ya existe en el sistema, intente con otro: ");
        }
        return repositorio.guardar(paciente); // si no encontró nada, lo va a pasar a repositorio para que lo guarde
    }

    public Paciente buscarPorId(Long idPaciente) {
        Paciente paciente = repositorio.buscarPorId(idPaciente); // Busca el id en repositorio y lo guarda en una variable
        if (paciente == null) { // Si no hay nada, tira error, si no, lo guarda
            throw new PacienteNoEncontradoException("El paciente no esta en el sistema, intente con otro: ");
        }
        return paciente;
    }

    public Paciente buscarPorDni(String dniPaciente) {
        if (dniPaciente == null || dniPaciente.isEmpty()) { // Se fija que haya algún dato
            throw new DatoInvalidoException("Intente nuevamente, por favor: ");
        }
        Paciente paciente = repositorio.buscarPorDni(dniPaciente); // Busca el id en repositorio y lo guarda en una variable
        if (paciente == null) { // Si no hay nada, tira error, si no, lo guarda
            throw new PacienteNoEncontradoException("El paciente no está en el sistema, intente con otro: ");
        }
        return paciente;
    }

    public Paciente modificar(Paciente paciente) {
        buscarPorId(paciente.getId());
        return repositorio.actualizar(paciente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        repositorio.eliminar(id);
    }

    public List<Paciente> listarTodos() {
        return repositorio.buscarTodos();
    }
}