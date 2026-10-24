package repositorio;

import dominio.Odontologo;

public class RepositorioOdontologo extends RepositorioBase<Odontologo> {

    private static final String ARCHIVO = "odontologos.txt";
    private static final int ID = 0;
    private static final int NOMBRE = 1;
    private static final int APELLIDO = 2;
    private static final int MATRICULA = 3;

    @Override
    protected String getArchivo() {
        return ARCHIVO;
    }

    public Odontologo buscarPorMatricula(String matricula) {
        return buscarPrimero(odontologo -> odontologo.getMatricula().equals(matricula));
    }

    @Override
    protected String serializar(Odontologo odontologo) {
        return unir(odontologo.getId(), odontologo.getNombre(), odontologo.getApellido(), odontologo.getMatricula());
    }

    @Override
    protected Odontologo parsearLinea(String[] partes) {
        Odontologo odontologo = new Odontologo(partes[NOMBRE], partes[APELLIDO], partes[MATRICULA]);
        odontologo.setId(Long.parseLong(partes[ID]));
        return odontologo;
    }
}
