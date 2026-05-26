package dominio;

import java.time.LocalDate;

public class Paciente extends Persona implements Comparable<Paciente>{

    private String dni;
    private String email;
    private LocalDate fechaIngreso =  LocalDate.now();
    private Domicilio domicilio;

    public Paciente() {}

    public Paciente(String nombre, String apellido, String dni, String email, Domicilio domicilio) {
        super(nombre, apellido);
        this.dni = dni;
        this.email = email;
        this.domicilio = domicilio;
    }

    @Override
    public int compareTo(Paciente otro) {
        return this.getApellido().compareTo(otro.getApellido());
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    @Override
    public String toString() {
        return super.toString() + " DNI: " + dni + " email: " + email + " con domicilio en: " + domicilio + ". Ingreso: " + fechaIngreso;
    }
}
