package vista;

import controlador.ControladorTurno;
import dominio.EstadoTurno;
import dominio.Turno;
import excepcion.ClinicaException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class VistaTurno {

    private ControladorTurno controlador;

    private Scanner scanner;

    public VistaTurno(ControladorTurno controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("Bienvenido al Menu de Turnos");
            System.out.println("1. Asignar Turno");
            System.out.println("2. Buscar Turno por ID");
            System.out.println("3. Listar Todos los Turnos");
            System.out.println("4. Listar Turnos por Paciente");
            System.out.println("5. Listar Turnos Por Odontólogo");
            System.out.println("6. Cambiar Estado del Turno");
            System.out.println("7. Reprogramar Turno");
            System.out.println("8. Ver Turnos en un Rango de Fechas");
            System.out.println("9. Eliminar Turno");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1: asignar(); break;
                case 2: buscarPorId(); break;
                case 3: listar(); break;
                case 4: listarPorPaciente(); break;
                case 5: listarPorOdontologo(); break;
                case 6: cambiarEstado(); break;
                case 7: reprogramar(); break;
                case 8: listarPorRangoFechas(); break;
                case 9: eliminar(); break;
                case 0: break;
                default:
                    System.out.println("Opción Invalida");
            }
        } while (opcion != 0);
    }

    private void asignar() {
        System.out.println(" - Asignar Turno - ");
        System.out.println("ID del paciente: ");
        Long idPaciente = scanner.nextLong();
        scanner.nextLine();
        System.out.println("ID del odontólogo: ");
        Long idOdontologo = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Fecha (YYYY-MM-DD): ");
        String fechaStr = scanner.nextLine();
        System.out.println("Hora (HH:MM): ");
        String horaStr = scanner.nextLine();

        try {
            LocalDate fecha = LocalDate.parse(fechaStr);
            LocalTime hora = LocalTime.parse(horaStr);
            Turno turno = controlador.asignar(idPaciente, idOdontologo, fecha, hora);
            System.out.println("Turno asignado: " + turno);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Error: Formato de fecha u hora invalido.");
        }
    }

    private void buscarPorId() {
        System.out.println(" - Buscar Turno por ID - ");
        System.out.println("ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        try {
            Turno turno = controlador.buscarPorId(id);
            System.out.println(turno);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listar() {
        System.out.println(" - Lista de Turnos - ");
        List<Turno> turnos = controlador.listarTodos();
        mostrarLista(turnos);
    }

    private void listarPorPaciente() {
        System.out.println(" - Turnos por Paciente - ");
        System.out.println("ID del paciente: ");
        Long idPaciente = scanner.nextLong();
        scanner.nextLine();
        List<Turno> turnos = controlador.listarPorPaciente(idPaciente);
        mostrarLista(turnos);
    }

    private void listarPorOdontologo() {
        System.out.println(" - Turnos por Odontólogo - ");
        System.out.println("ID del Odontólogo: ");
        Long idOdontologo = scanner.nextLong();
        scanner.nextLine();
        List<Turno> turnos = controlador.listarPorOdontologo(idOdontologo);
        mostrarLista(turnos);
    }

    private void cambiarEstado() {
        System.out.println(" - Cambiar Estado del Turno - ");
        System.out.println("ID del turno: ");
        Long idTurno = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Nuevo estado (PENDIENTE / CONFIRMADO / COMPLETADO / CANCELADO): ");
        String estadoStr = scanner.nextLine();

        try {
            EstadoTurno nuevoEstado = EstadoTurno.valueOf(estadoStr.toUpperCase());
            Turno turno = controlador.cambiarEstado(idTurno, nuevoEstado);
            System.out.println("Estado actualizado: " + turno);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Estado invalido.");
        }
    }

    private void reprogramar() {
        System.out.println(" - Reprogramar Turno - ");
        System.out.println("ID del turno: ");
        Long idTurno = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Nueva fecha (YYYY-MM-DD): ");
        String fechaStr = scanner.nextLine();
        System.out.println("Nueva hora (HH:MM): ");
        String horaStr = scanner.nextLine();

        try {
            LocalDate nuevaFecha = LocalDate.parse(fechaStr);
            LocalTime nuevaHora = LocalTime.parse(horaStr);
            Turno turno = controlador.reprogramar(idTurno, nuevaFecha, nuevaHora);
            System.out.println("Turno reprogramado: " + turno);
        } catch (ClinicaException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Error: Formato de fecha u hora invalido.");
        }
    }

    private void listarPorRangoFechas() {
        System.out.println(" - Ver Turnos en un Rango de Fechas - ");
        System.out.println(" Fecha Desde (YYYY-MM-DD): ");
        String desde = scanner.nextLine();
        System.out.println(" Fecha Hasta (YYYY-MM-DD): ");
        String hasta = scanner.nextLine();

        try {
            LocalDate fechaDesde = LocalDate.parse(desde);
            LocalDate fechaHasta = LocalDate.parse(hasta);
            List<Turno> turnos = controlador.listarPorRangoFechas(fechaDesde, fechaHasta);
            mostrarLista(turnos);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Formato de fecha invalido.");
        }
    }


    private void eliminar() {
        System.out.println(" - Eliminar Turno - ");
        System.out.println("ID del turno: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        boolean eliminado = controlador.eliminarPorId(id);
        if (eliminado) {
            System.out.println("Turno eliminado correctamente.");
        } else {
            System.out.println("No se encontró un turno con ese ID.");
        }
    }

    private void mostrarLista(List<Turno> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay turnos registrados.");
            return;
        }
        for (Turno turno : lista) {
            System.out.println(turno);
        }
    }
}
