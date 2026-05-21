package dominio;

public class Odontologo extends Persona {

    private String matricula;

    public Odontologo() {}

    public Odontologo(String nombre, String apellido, String matricula) {
        super(nombre, apellido);
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public String toString() {
        return super.toString() + ", matricula: " + matricula;
    }
}