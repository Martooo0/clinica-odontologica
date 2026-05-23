package controlador;

import dominio.Odontologo;
import excepcion.OdontologoNoEncontradoException;
import servicio.ServicioOdontologo;

import java.util.List;

public class ControladorOdontologo {

    private ServicioOdontologo servicioOdontologo;

    public ControladorOdontologo(ServicioOdontologo servicioOdontologo){
        this.servicioOdontologo = servicioOdontologo;
    }

    public Odontologo registrar(String nombre, String apellido, String matricula) {
        Odontologo odontologo = new Odontologo(nombre, apellido, matricula);
        return servicioOdontologo.registrar(odontologo);
    }

    public Odontologo modificar(Long id, String nombre, String apellido, String matricula) {
        servicioOdontologo.buscarPorId(id); // valida que exista; lanza OdontologoNoEncontradoException si no
        Odontologo odontologo = new Odontologo(nombre, apellido, matricula);
        odontologo.setId(id);
        return servicioOdontologo.modificar(odontologo);
    }

    public boolean eliminarPorId(Long id) {
        try {
            servicioOdontologo.eliminar(id);
        } catch (OdontologoNoEncontradoException e) {
            return false;
        }
        return true;
    }

    public Odontologo buscarPorId(Long id) { return servicioOdontologo.buscarPorId(id); }

    public Odontologo buscarPorMatricula(String matricula) { return servicioOdontologo.buscarPorMatricula(matricula); }

    public List<Odontologo> listarTodos() { return servicioOdontologo.listarTodos();}
}