package vista;

import controlador.ControladorPaciente;
import dominio.Paciente;
import excepcion.ClinicaException;

import java.util.List;
import java.util.Scanner;

public class VistaPaciente {

    private ControladorPaciente controlador;

    private Scanner scanner;

    public VistaPaciente(ControladorPaciente controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("Bienvenido al Menu de Pacientes");
            System.out.println("1. Crear Paciente");
            System.out.println("2. Buscar Paciente por su ID");
            System.out.println("3. Buscar Paciente por su DNI");
            System.out.println("4. Listar a Todos los Pacientes");
            System.out.println("5. Listar a los Pacientes por su Apellido");
            System.out.println("6. Actualizar Datos de un Paciente");
            System.out.println("7. Eliminar Paciente");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1: registrar(); break;
                case 2: buscarPorId(); break;
                case 3: buscarPorDni(); break;
                case 4: listar(); break;
                case 5: listarOrdenados(); break;
                case 6: actualizar(); break;
                case 7: eliminar ();  break;
                case 0: break;
                default:
                    System.out.println("Opción Invalida");
            }
        } while (opcion != 0);
    }

    private void registrar() {
        System.out.println(" - Registrar Paciente - ");
        System.out.println("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.println("DNI: ");
        String dni = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println(" - Dirección - ");
        System.out.println("Calle: ");
        String calle = scanner.nextLine();
        System.out.println("Numero: ");
        String numero = scanner.nextLine();
        System.out.println("Localidad: ");
        String localidad = scanner.nextLine();
        System.out.println("Provincia: ");
        String provincia = scanner.nextLine();

        try {
            Paciente paciente = controlador.registrar(nombre, apellido, dni, email, calle, numero, localidad, provincia);
            System.out.println("Paciente registrado: " + paciente);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void buscarPorId() {
        System.out.println(" - Buscar Paciente por ID - ");
        System.out.println("ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        try {
            Paciente paciente = controlador.buscarPorId(id);
            System.out.println(paciente);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarPorDni() {
        System.out.println(" - Buscar Paciente por DNI - ");
        System.out.println("DNI: ");
        String dni = scanner.nextLine();
        try {
            Paciente paciente = controlador.buscarPorDni(dni);
            System.out.println(paciente);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listar() {
        System.out.println(" - Lista de Pacientes - ");
        List<Paciente> pacientes = controlador.listarTodos();
        mostrarLista(pacientes);
    }

    private void listarOrdenados() {
        System.out.println(" - Pacientes ordenados por apellido - ");
        List<Paciente> pacientes = controlador.listarOrdenadosPorApellido();
        mostrarLista(pacientes);
    }

    private void actualizar() {
        System.out.println(" - Actualizar Paciente - ");
        System.out.println("ID del paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println(" - Dirección - ");
        System.out.println("Calle: ");
        String calle = scanner.nextLine();
        System.out.println("Numero: ");
        String numero = scanner.nextLine();
        System.out.println("Localidad: ");
        String localidad = scanner.nextLine();
        System.out.println("Provincia: ");
        String provincia = scanner.nextLine();

        try {
            Paciente paciente = controlador.modificar(id, nombre, apellido, email, calle, numero, localidad, provincia);
            System.out.println("Paciente actualizado: " + paciente);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminar() {
        System.out.println(" - Eliminar Paciente - ");
        System.out.println("ID del paciente: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        boolean eliminado = controlador.eliminarPorId(id);
        if (eliminado) {
            System.out.println("Paciente eliminado correctamente.");
        } else {
            System.out.println("No se encontró un paciente con ese ID.");
        }
    }

    private void mostrarLista(List<Paciente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        for (Paciente paciente : lista) {
            System.out.println(paciente);
        }
    }


}
