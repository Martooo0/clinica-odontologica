package servicio;

import dominio.Odontologo;
import excepcion.MatriculaDuplicadaException;
import excepcion.OdontologoNoEncontradoException;
import observador.Evento;
import observador.Sujeto;
import observador.TipoEvento;
import repositorio.RepositorioOdontologo;

import java.util.List;

import static servicio.Validador.requerir;
import static servicio.Validador.requerirTexto;
import static servicio.Validador.validarNombreYApellido;

/**
 * Hereda de Sujeto (Observer): avisa cuando se agrega, modifica o elimina un odontólogo,
 * así el desplegable de la ventana del paciente (y el de Gestionar Turnos) se actualiza solo.
 */
public class ServicioOdontologo extends Sujeto {

    private final RepositorioOdontologo repositorio;

    public ServicioOdontologo(RepositorioOdontologo repositorio) {
        this.repositorio = repositorio;
    }

    public Odontologo registrar(Odontologo odontologo) {
        validar(odontologo);
        if (repositorio.buscarPorMatricula(odontologo.getMatricula()) != null) {
            throw new MatriculaDuplicadaException("La matrícula ya se encuentra en el sistema, intente nuevamente");
        }
        Odontologo guardado = repositorio.registrar(odontologo);
        notificarObservadores(Evento.deOdontologo(TipoEvento.ODONTOLOGO_REGISTRADO, guardado));
        return guardado;
    }

    public Odontologo buscarPorId(Long id) {
        return requerir(repositorio.buscarPorId(id),
                () -> new OdontologoNoEncontradoException("El odontólogo no está en el sistema, intente con otro"));
    }

    public Odontologo modificar(Odontologo cambios) {
        validar(cambios);
        Odontologo conMismaMatricula = repositorio.buscarPorMatricula(cambios.getMatricula());
        if (conMismaMatricula != null && !conMismaMatricula.getId().equals(cambios.getId())) {
            throw new MatriculaDuplicadaException("La matrícula ya pertenece a otro odontólogo");
        }
        Odontologo original = buscarPorId(cambios.getId());
        original.setNombre(cambios.getNombre());
        original.setApellido(cambios.getApellido());
        original.setMatricula(cambios.getMatricula());
        Odontologo actualizado = repositorio.actualizar(original);
        notificarObservadores(Evento.deOdontologo(TipoEvento.ODONTOLOGO_MODIFICADO, actualizado));
        return actualizado;
    }

    public void eliminar(Long id) {
        Odontologo odontologo = buscarPorId(id); // lo guardo antes de borrarlo para poder avisar cuál era
        repositorio.eliminar(id);
        notificarObservadores(Evento.deOdontologo(TipoEvento.ODONTOLOGO_ELIMINADO, odontologo));
    }

    public List<Odontologo> listarTodos() {
        return repositorio.listarTodos();
    }

    private void validar(Odontologo odontologo) {
        validarNombreYApellido(odontologo);
        requerirTexto(odontologo.getMatricula(), "Ingrese una matrícula válida, por favor");
    }
}
