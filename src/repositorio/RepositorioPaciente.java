package repositorio;

import dominio.Paciente;
import dominio.Domicilio;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioPaciente implements IRepositorio<Paciente> {

    private Map<Long, Paciente> pacientes = new HashMap<>();
    private Long contadorId = 0L;
    private static final String ARCHIVO = "pacientes.txt";
    private static final String SEP = ";";

    // Le damos el id al paciente
    @Override
    public Paciente guardar(Paciente entidad) {
        entidad.setId(++contadorId); // Primero aumenta el contador, luego asigna.
        pacientes.put(entidad.getId(), entidad); // clave = id | valor = lo demás
        return entidad;
    }

    @Override
    public Paciente buscarPorId(Long id) {
        return pacientes.get(id);
    }

    // Devuelvo todos los pacientes como una List. Por eso los meto adentro de un ArrayList nuevo: lo convierte a List y además hace una copia, así si alguien toca la lista que devuelvo, no debería pasar nada con el HashMap.
    @Override
    public List<Paciente> buscarTodos() {
        return new ArrayList<>(pacientes.values());
    }

    @Override
    public Paciente actualizar(Paciente entidad) {
        pacientes.put(entidad.getId(), entidad); // Si existe, lo reemplaza. Si no, la agrega. Igual service no lo va a dejar por PacienteNoEncontrado.
        return entidad;
    }

    @Override
    public void eliminar(Long id) {
        pacientes.remove(id);
    }

    public Paciente buscarPorDni(String dni) {
        return pacientes.values().stream()
                .filter(p -> p.getDni().equals(dni))
                .findFirst()
                .orElse(null);
    }

    // Parte nueva de Archivos. Esta función trae el archivo y los ";" para, con un try-catch-finally, ir metiendo a todos los pacientes con su domicilio incluido.

    public void guardarTodos() {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(ARCHIVO);
            pw = new PrintWriter(fw);
            for (Paciente p : pacientes.values()) {
                pw.println(p.getId() + SEP + p.getNombre() + SEP + p.getApellido() + SEP + p.getDni() + SEP + p.getEmail() + SEP + p.getFechaIngreso() + SEP + p.getDomicilio().getCalle() + SEP + p.getDomicilio().getNumero() + SEP + p.getDomicilio().getLocalidad() + SEP + p.getDomicilio().getProvincia());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar pacientes: " + e.getMessage());
        } finally {
            if (pw != null) pw.close();
        }
    }

    public void cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return;

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(ARCHIVO);
            br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;                 // ignora líneas vacías
                String[] partes = linea.split(SEP);                   // corta la línea por ';'

                // Reconstruyo domicilio y paciente con los constructores que ya tenía
                Domicilio domicilio = new Domicilio(partes[6], partes[7], partes[8], partes[9]);
                Paciente paciente = new Paciente(partes[1], partes[2], partes[3], partes[4], domicilio);

                // El id y la fechaIngreso NO van en el constructor: los seteo aparte
                paciente.setId(Long.parseLong(partes[0]));            // texto "1" -> Long 1
                paciente.setFechaIngreso(LocalDate.parse(partes[5])); // pisa el now() del constructor

                pacientes.put(paciente.getId(), paciente);            // al HashMap, con su id como clave

                // Restauro el contador al id más alto leído (si no, el próximo nuevo pisaría uno existente)
                if (paciente.getId() > contadorId) {
                    contadorId = paciente.getId();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer pacientes: " + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el archivo: " + e.getMessage());
                }
            }
        }
    }
}