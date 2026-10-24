package dominio;

import java.math.BigDecimal;

public class Tratamiento implements Identificable {

    private Long id;
    private String nombre;
    private BigDecimal precio;

    public Tratamiento() {}

    public Tratamiento(String nombre, BigDecimal precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return nombre + " - $" + precio;
    }
}
