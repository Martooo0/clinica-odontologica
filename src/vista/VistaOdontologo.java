package vista;

import controlador.ControladorOdontologo;
import dominio.Odontologo;
import excepcion.ClinicaException;

import java.util.List;
import java.util.Scanner;

public class VistaOdontologo {

    private ControladorOdontologo controlador;

    private Scanner scanner;

    public VistaOdontologo(ControladorOdontologo controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("Bienvenido al Menu de Odontólogos");
            System.out.println("1. Crear Odontólogo");
            System.out.println("2. Buscar Odontólogo por su ID");
            System.out.println("3. Buscar Odontólogo por su Matricula");
            System.out.println("4. Listar a Todos los Odontólogos");
            System.out.println("5. Actualizar Datos de un Odontólogo");
            System.out.println("6. Eliminar Odontólogo");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1: registrar(); break;
                case 2: buscarPorId(); break;
                case 3: buscarPorMatricula(); break;
                case 4: listar(); break;
                case 5: actualizar(); break;
                case 6: eliminar(); break;
                case 0: break;
                default:
                    System.out.println("Opción Invalida");
            }
        } while (opcion != 0);
    }

    private void registrar() {
        System.out.println(" - Registrar Odontólogo - ");
        System.out.println("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.println("Matricula: ");
        String matricula = scanner.nextLine();

        try {
            Odontologo odontologo = controlador.registrar(nombre, apellido, matricula);
            System.out.println("Odontólogo registrado: " + odontologo);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorId() {
        System.out.println(" - Buscar Odontólogo por ID - ");
        System.out.println("ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        try {
            Odontologo odontologo = controlador.buscarPorId(id);
            System.out.println(odontologo);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorMatricula() {
        System.out.println(" - Buscar Odontólogo por Matricula - ");
        System.out.println("Matricula: ");
        String matricula = scanner.nextLine();
        try {
            Odontologo odontologo = controlador.buscarPorMatricula(matricula);
            System.out.println(odontologo);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listar() {
        System.out.println(" - Lista de Odontólogos - ");
        List<Odontologo> odontologos = controlador.listarTodos();
        mostrarLista(odontologos);
    }

    private void actualizar() {
        System.out.println(" - Actualizar Odontólogo - ");
        System.out.println("ID del odontólogo: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.println("Matricula: ");
        String matricula = scanner.nextLine();

        try {
            Odontologo odontologo = controlador.modificar(id, nombre, apellido, matricula);
            System.out.println("Odontólogo actualizado: " + odontologo);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminar() {
        System.out.println(" - Eliminar Odontólogo - ");
        System.out.println("ID del odontólogo: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        boolean eliminado = controlador.eliminarPorId(id);
        if (eliminado) {
            System.out.println("Odontólogo eliminado correctamente.");
        } else {
            System.out.println("No se encontró un odontólogo con ese ID.");
        }
    }

    private void mostrarLista(List<Odontologo> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay odontólogos registrados.");
            return;
        }
        for (Odontologo odontologo : lista) {
            System.out.println(odontologo);
        }
    }
}
