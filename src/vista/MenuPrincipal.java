package vista;

import controlador.ControladorOdontologo;
import controlador.ControladorPaciente;
import controlador.ControladorTurno;
import servicio.ServicioOdontologo;
import servicio.ServicioPaciente;
import servicio.ServicioTurno;

import java.util.Scanner;

public class MenuPrincipal {

    private VistaPaciente vistaPaciente;
    private VistaOdontologo vistaOdontologo;
    private VistaTurno vistaTurno;
    private Scanner scanner;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);

        ServicioPaciente servicioPaciente = new ServicioPaciente();
        ServicioOdontologo servicioOdontologo = new ServicioOdontologo();
        ServicioTurno servicioTurno = new ServicioTurno();

        ControladorPaciente controladorPaciente = new ControladorPaciente(servicioPaciente);
        ControladorOdontologo controladorOdontologo = new ControladorOdontologo(servicioOdontologo);
        ControladorTurno controladorTurno = new ControladorTurno(servicioTurno);

        this.vistaPaciente = new VistaPaciente(controladorPaciente, scanner);
        this.vistaOdontologo = new VistaOdontologo(controladorOdontologo, scanner);
        this.vistaTurno = new VistaTurno(controladorTurno, scanner);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== Clinica Odontologica ===");
            System.out.println("1. Gestionar Pacientes");
            System.out.println("2. Gestionar Odontologos");
            System.out.println("3. Gestionar Turnos");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1: vistaPaciente.mostrarMenu(); break;
                case 2: vistaOdontologo.mostrarMenu(); break;
                case 3: vistaTurno.mostrarMenu(); break;
                case 0:
                    System.out.println("Hasta luego.");
                    break;
                default:
                    System.out.println("Opción Invalida");
            }
        } while (opcion != 0);
    }
}
