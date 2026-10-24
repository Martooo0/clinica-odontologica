package repositorio;

import dominio.Tratamiento;

import java.math.BigDecimal;

public class RepositorioTratamiento extends RepositorioBase<Tratamiento> {

    private static final String ARCHIVO = "tratamientos.txt";
    private static final int ID = 0;
    private static final int NOMBRE = 1;
    private static final int PRECIO = 2;

    @Override
    protected String getArchivo() {
        return ARCHIVO;
    }

    // Busca sin importar mayúsculas: "limpieza" y "Limpieza" son el mismo tratamiento
    public Tratamiento buscarPorNombre(String nombre) {
        return buscarPrimero(tratamiento -> tratamiento.getNombre().equalsIgnoreCase(nombre.trim()));
    }

    // Formato: id;nombre;precio
    @Override
    protected String serializar(Tratamiento tratamiento) {
        return unir(tratamiento.getId(), tratamiento.getNombre(), tratamiento.getPrecio().toPlainString());
    }

    @Override
    protected Tratamiento parsearLinea(String[] partes) {
        Tratamiento tratamiento = new Tratamiento(partes[NOMBRE], new BigDecimal(partes[PRECIO]));
        tratamiento.setId(Long.parseLong(partes[ID]));
        return tratamiento;
    }
}
