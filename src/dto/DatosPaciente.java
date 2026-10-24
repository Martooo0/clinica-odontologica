package dto;

import dominio.Domicilio;
import dominio.ObraSocial;

public class DatosPaciente {

    private final String nombre;
    private final String apellido;
    private final String dni;
    private final String email;
    private final Domicilio domicilio;
    private final ObraSocial obraSocial;

    public DatosPaciente(String nombre, String apellido, String dni, String email,
                         Domicilio domicilio, ObraSocial obraSocial) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.domicilio = domicilio;
        this.obraSocial = obraSocial;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public ObraSocial getObraSocial() {
        return obraSocial;
    }
}
