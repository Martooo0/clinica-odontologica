package servicio;

import dominio.Odontologo;
import excepcion.DatoInvalidoException;
import excepcion.MatriculaDuplicadaException;
import excepcion.OdontologoNoEncontradoException;
import repositorio.RepositorioOdontologo;

import java.util.List;

public class ServicioOdontologo {

    private RepositorioOdontologo repositorio;

    public ServicioOdontologo(RepositorioOdontologo repositorio) {
        this.repositorio = repositorio;
    }

    public Odontologo registrar(Odontologo odontologo) {
        if (odontologo == null) {
            throw new DatoInvalidoException("Ingrese un valor, por favor");
        }
        if (odontologo.getNombre() == null || odontologo.getNombre().isEmpty()) {
            throw new DatoInvalidoException("Ingrese un nombre valido, por favor");
        }
        if (odontologo.getApellido() == null || odontologo.getApellido().isEmpty()) {
            throw new DatoInvalidoException("Ingrese un apellido valido, por favor");
        }
        if (odontologo.getMatricula() == null || odontologo.getMatricula().isEmpty()) {
            throw new DatoInvalidoException("Ingrese una matricula valida, por favor");
        }
        Odontologo existe = repositorio.buscarPorMatricula(odontologo.getMatricula());
        if (existe != null) {
            throw new MatriculaDuplicadaException("La matricula ya se encuentra en el sistema, intente nuevamente: ");
        }
        return repositorio.guardar(odontologo);
    }

    public Odontologo buscarPorMatricula(String matricula) {
        Odontologo odontologo = repositorio.buscarPorMatricula(matricula);
        if (odontologo == null) {
            throw new OdontologoNoEncontradoException("El odontólogo no esta en el sistema, intente con otro: ");
        }
        return odontologo;
    }

    public Odontologo buscarPorId(Long id) {
        Odontologo odontologo = repositorio.buscarPorId(id);
        if (odontologo == null) {
            throw new OdontologoNoEncontradoException("El odontólogo no está en el sistema, intente con otro: ");
        }
        return odontologo;
    }

    public Odontologo modificar(Odontologo odontologo) {
        if (odontologo == null) {
            throw new DatoInvalidoException("Ingrese un valor, por favor");
        }
        if (odontologo.getNombre() == null || odontologo.getNombre().isEmpty()) {
            throw new DatoInvalidoException("Ingrese un nombre valido, por favor");
        }
        if (odontologo.getApellido() == null || odontologo.getApellido().isEmpty()) {
            throw new DatoInvalidoException("Ingrese un apellido valido, por favor");
        }
        if (odontologo.getMatricula() == null || odontologo.getMatricula().isEmpty()) {
            throw new DatoInvalidoException("Ingrese una matricula valida, por favor");
        }
        Odontologo conMismaMatricula = repositorio.buscarPorMatricula(odontologo.getMatricula());
        if (conMismaMatricula != null && !conMismaMatricula.getId().equals(odontologo.getId())) {
            throw new MatriculaDuplicadaException("La matricula ya pertenece a otro odontólogo");
        }
        buscarPorId(odontologo.getId());
        return repositorio.actualizar(odontologo);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        repositorio.eliminar(id);
    }

    public List<Odontologo> listarTodos() {
        return repositorio.buscarTodos();
    }
}