package repositorio;

import dominio.Odontologo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioOdontologo implements IRepositorio<Odontologo> {

    private Map<Long, Odontologo> odontologos = new HashMap<>();
    private Long contadorId = 0L;
    private static final String ARCHIVO = "odontologos.txt";
    private static final String SEP = ";";

    @Override
    public Odontologo guardar(Odontologo entidad) {
        entidad.setId(++contadorId);
        odontologos.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Odontologo buscarPorId(Long id) {
        return odontologos.get(id);
    }

    @Override
    public List<Odontologo> buscarTodos() {
        return new ArrayList<>(odontologos.values());
    }

    @Override
    public Odontologo actualizar(Odontologo entidad) {
        odontologos.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public void eliminar(Long id) {
        odontologos.remove(id);
    }

    public Odontologo buscarPorMatricula(String matricula) {
        return odontologos.values().stream()
                .filter(o -> o.getMatricula().equals(matricula))
                .findFirst()
                .orElse(null);
    }

    public void guardarTodos() {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(ARCHIVO);
            pw = new PrintWriter(fw);
            for (Odontologo o : odontologos.values()) {
                pw.println(o.getId() + SEP + o.getNombre() + SEP + o.getApellido() + SEP + o.getMatricula());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar odontólogos: " + e.getMessage());
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
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(SEP);

                Odontologo odontologo = new Odontologo(partes[1], partes[2], partes[3]);
                odontologo.setId(Long.parseLong(partes[0]));

                odontologos.put(odontologo.getId(), odontologo);

                if (odontologo.getId() > contadorId) {
                    contadorId = odontologo.getId();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer odontólogos: " + e.getMessage());
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