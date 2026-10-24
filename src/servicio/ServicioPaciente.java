package servicio;

import dominio.Paciente;
import dominio.ObraSocial;
import observador.Evento;
import observador.Sujeto;
import observador.TipoEvento;
import excepcion.DniDuplicadoException;
import excepcion.DatoInvalidoException;
import excepcion.PacienteNoEncontradoException;
import repositorio.RepositorioPaciente;

import java.util.List;
import java.util.Collections;

import static servicio.Validador.requerir;
import static servicio.Validador.requerirTexto;
import static servicio.Validador.validarNombreYApellido;

/**
 * Hereda de Sujeto (Observer): avisa cuando se registra, modifica o elimina un paciente,
 * así las ventanas que muestran pacientes se actualizan solas (por ejemplo, "Gestionar
 * Pacientes" del secretario cuando alguien se registra desde la ventana del paciente).
 */
public class ServicioPaciente extends Sujeto {

    private static final String REGEX_DNI = "\\d{7,8}";
    private static final String NO_ENCONTRADO = "El paciente no está en el sistema, intente con otro";

    private final RepositorioPaciente repositorio;

    public ServicioPaciente(RepositorioPaciente repositorio) {
        this.repositorio = repositorio;
    }

    public Paciente registrar(Paciente paciente) {
        validar(paciente);
        if (repositorio.buscarPorDni(paciente.getDni()) != null) {
            throw new DniDuplicadoException("El DNI ya existe en el sistema, intente con otro");
        }
        Paciente guardado = repositorio.registrar(paciente);
        notificarObservadores(Evento.dePaciente(TipoEvento.PACIENTE_REGISTRADO, guardado));
        return guardado;
    }

    public Paciente buscarPorId(Long idPaciente) {
        return requerir(repositorio.buscarPorId(idPaciente), () -> new PacienteNoEncontradoException(NO_ENCONTRADO));
    }

    public Paciente buscarPorDni(String dniPaciente) {
        requerirTexto(dniPaciente, "Intente nuevamente, por favor");
        return requerir(repositorio.buscarPorDni(dniPaciente), () -> new PacienteNoEncontradoException(NO_ENCONTRADO));
    }

    public Paciente modificar(Paciente cambios) {
        Paciente original = buscarPorId(cambios.getId());
        cambios.setDni(original.getDni());
        validar(cambios);
        original.setNombre(cambios.getNombre());
        original.setApellido(cambios.getApellido());
        original.setEmail(cambios.getEmail());
        original.setDomicilio(cambios.getDomicilio());
        original.setObraSocial(cambios.getObraSocial());
        Paciente actualizado = repositorio.actualizar(original);
        notificarObservadores(Evento.dePaciente(TipoEvento.PACIENTE_MODIFICADO, actualizado));
        return actualizado;
    }

    public void eliminar(Long id) {
        Paciente paciente = buscarPorId(id); // lo guardo antes de borrarlo para poder avisar quién era
        repositorio.eliminar(id);
        notificarObservadores(Evento.dePaciente(TipoEvento.PACIENTE_ELIMINADO, paciente));
    }

    public List<Paciente> listarTodos() {
        return repositorio.listarTodos();
    }

    public List<Paciente> listarPorApellido() {
        List<Paciente> pacientes = repositorio.listarTodos();
        Collections.sort(pacientes);
        return pacientes;
    }

    private void validar(Paciente paciente) {
        validarNombreYApellido(paciente);
        if (paciente.getDni() == null || !paciente.getDni().matches(REGEX_DNI)) {
            throw new DatoInvalidoException("Ingrese un DNI válido, por favor");
        }
        String email = paciente.getEmail();
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new DatoInvalidoException("Ingrese un email válido, por favor");
        }
        if (paciente.getObraSocial() == null) { // sin obra social elegida -> particular
            paciente.setObraSocial(ObraSocial.PARTICULAR);
        }
    }
}
