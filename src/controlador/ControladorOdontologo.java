package controlador;

import dominio.Odontologo;
import servicio.ServicioOdontologo;

import java.util.List;

public class ControladorOdontologo extends ControladorObservable {

    private final ServicioOdontologo servicioOdontologo;

    public ControladorOdontologo(ServicioOdontologo servicioOdontologo){
        super(servicioOdontologo);
        this.servicioOdontologo = servicioOdontologo;
    }

    public Odontologo registrar(String nombre, String apellido, String matricula) {
        return servicioOdontologo.registrar(new Odontologo(nombre, apellido, matricula));
    }

    public Odontologo modificar(Long id, String nombre, String apellido, String matricula) {
        Odontologo cambios = new Odontologo(nombre, apellido, matricula);
        cambios.setId(id);
        return servicioOdontologo.modificar(cambios);
    }

    public void eliminar(Long id) {
        servicioOdontologo.eliminar(id);
    }

    public Odontologo buscarPorId(Long id) { return servicioOdontologo.buscarPorId(id); }

    public List<Odontologo> listarTodos() { return servicioOdontologo.listarTodos();}
}
