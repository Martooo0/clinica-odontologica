package vista;

public enum TipoBusqueda {
    PACIENTES("Pacientes"),
    ODONTOLOGOS("Odontólogos");

    private final String etiqueta;

    TipoBusqueda(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
