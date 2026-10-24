package repositorio;

import dominio.Paciente;
import dominio.Domicilio;
import dominio.ObraSocial;

import java.time.LocalDate;

public class RepositorioPaciente extends RepositorioBase<Paciente> {

    private static final String ARCHIVO = "pacientes.txt";
    private static final int ID = 0;
    private static final int NOMBRE = 1;
    private static final int APELLIDO = 2;
    private static final int DNI = 3;
    private static final int EMAIL = 4;
    private static final int FECHA_INGRESO = 5;
    private static final int CALLE = 6;
    private static final int NUMERO = 7;
    private static final int LOCALIDAD = 8;
    private static final int PROVINCIA = 9;
    private static final int OBRA_SOCIAL = 10;

    @Override
    protected String getArchivo() {
        return ARCHIVO;
    }

    public Paciente buscarPorDni(String dni) {
        return buscarPrimero(paciente -> paciente.getDni().equals(dni));
    }

    @Override
    protected String serializar(Paciente paciente) {
        Domicilio domicilio = paciente.getDomicilio();
        return unir(paciente.getId(), paciente.getNombre(), paciente.getApellido(),
                paciente.getDni(), paciente.getEmail(), paciente.getFechaIngreso(),
                domicilio.getCalle(), domicilio.getNumero(), domicilio.getLocalidad(), domicilio.getProvincia(),
                paciente.getObraSocial().name());
    }

    @Override
    protected Paciente parsearLinea(String[] partes) {
        Domicilio domicilio = new Domicilio(partes[CALLE], partes[NUMERO], partes[LOCALIDAD], partes[PROVINCIA]);
        Paciente paciente = new Paciente(partes[NOMBRE], partes[APELLIDO], partes[DNI], partes[EMAIL], domicilio);

        // El id y la fechaIngreso NO van en el constructor: los seteo aparte
        paciente.setId(Long.parseLong(partes[ID]));
        paciente.setFechaIngreso(LocalDate.parse(partes[FECHA_INGRESO])); // pisa el now() del constructor

        // Obra social (campo nuevo, posición 10). Los archivos viejos no lo tienen: en ese caso queda PARTICULAR.
        if (partes.length > OBRA_SOCIAL) {
            paciente.setObraSocial(ObraSocial.valueOf(partes[OBRA_SOCIAL]));
        }
        return paciente;
    }
}
