package dominio;

public abstract class Persona implements Identificable {

    private Long id;
    private String nombre;
    private String apellido;

    public Persona() {}

    public Persona(String nombre, String apellido) {

        this.nombre = nombre;
        this.apellido = apellido;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombreCompleto() {
        return nombre +  " " + apellido;
    }

    @Override
    public String toString() {
        return "[id=" + id + "] " + getNombreCompleto();
    }


}
