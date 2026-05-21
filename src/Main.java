import dominio.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {

        // 1. Crear domicilio
        Domicilio dom = new Domicilio("Av. Peron", "742",
                "Springfield", "Buenos Aires");
        System.out.println("Domicilio: " + dom);

        // 2. Crear paciente
        Paciente p = new Paciente("Juan", "Pérez", "30123456",
                "juan@mail.com", dom);
        System.out.println("Paciente: " + p);

        // 3. Crear odontólogo
        Odontologo o = new Odontologo("María", "Gómez", "MAT-1234");
        System.out.println("Odontólogo: " + o);

        // 4. Turno futuro
        Turno turnoFuturo = new Turno(LocalDate.of(2026, 12, 25),
                LocalTime.of(14, 30), p, o);
        System.out.println("\nTurno futuro: " + turnoFuturo);
        System.out.println("¿Es futuro? " + turnoFuturo.esFuturo());

        // 5. Turno pasado
        Turno turnoPasado = new Turno(LocalDate.of(2020, 1, 1),
                LocalTime.of(10, 0), p, o);
        System.out.println("\nTurno pasado: " + turnoPasado);
        System.out.println("¿Es futuro? " + turnoPasado.esFuturo());
    }
}
