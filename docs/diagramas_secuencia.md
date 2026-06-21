# Diagramas de Secuencia — Clínica Odontológica

---

## Caso 1: Alta de un nuevo turno

Muestra el flujo completo desde que el usuario ingresa los datos hasta que el turno queda guardado, incluyendo todas las validaciones y los caminos de excepción.

```mermaid
sequenceDiagram
    actor Usuario
    participant Vista as VistaTurno
    participant Ctrl as ControladorTurno
    participant Serv as ServicioTurno
    participant RepoPac as RepositorioPaciente
    participant RepoOdo as RepositorioOdontologo
    participant RepoTur as RepositorioTurno

    Usuario->>Vista: Selecciona "Asignar Turno"
    Vista->>Usuario: Solicita idPaciente, idOdontólogo, fecha, hora
    Usuario->>Vista: Ingresa los datos

    Vista->>Vista: Parsea fecha (LocalDate.parse) y hora (LocalTime.parse)

    Vista->>Ctrl: asignar(idPaciente, idOdontólogo, fecha, hora)
    Ctrl->>Serv: reservar(idPaciente, idOdontólogo, fecha, hora)

    Note over Serv: Validación: fecha y hora no nulas
    Note over Serv: Validación: fecha no anterior a hoy

    alt Fecha anterior a hoy
        Serv-->>Ctrl: lanza DatoInvalidoException
        Ctrl-->>Vista: propaga la excepción
        Vista->>Usuario: "Error: La fecha del turno no puede ser anterior a hoy"
    end

    Serv->>RepoPac: buscarPorId(idPaciente)
    RepoPac-->>Serv: Paciente o null

    alt Paciente no encontrado (null)
        Serv-->>Ctrl: lanza PacienteNoEncontradoException
        Ctrl-->>Vista: propaga la excepción
        Vista->>Usuario: "Error: Paciente no encontrado"
    end

    Serv->>RepoOdo: buscarPorId(idOdontólogo)
    RepoOdo-->>Serv: Odontólogo o null

    alt Odontólogo no encontrado (null)
        Serv-->>Ctrl: lanza OdontologoNoEncontradoException
        Ctrl-->>Vista: propaga la excepción
        Vista->>Usuario: "Error: Odontólogo no encontrado"
    end

    Serv->>RepoTur: buscarPorOdontologo(idOdontólogo)
    RepoTur-->>Serv: List de Turnos del odontólogo

    Note over Serv: Recorre la lista verificando<br/>coincidencia de fecha y hora

    alt Turno ya reservado en esa fecha/hora
        Serv-->>Ctrl: lanza TurnoYaReservadoException
        Ctrl-->>Vista: propaga la excepción
        Vista->>Usuario: "Error: Turno ya reservado"
    end

    Note over Serv: Crea new Turno(fecha, hora, paciente, odontólogo)<br/>Estado inicial: PENDIENTE

    Serv->>RepoTur: guardar(turno)
    Note over RepoTur: Asigna ID (++contadorId)<br/>Guarda en HashMap
    RepoTur-->>Serv: Turno (con ID asignado)

    Serv-->>Ctrl: Turno
    Ctrl-->>Vista: Turno
    Vista->>Usuario: "Turno asignado: [datos del turno]"
```

---

## Caso 2: Búsqueda de paciente por DNI

Muestra el caso exitoso (se encuentra al paciente) y el caso de excepción (no existe).

```mermaid
sequenceDiagram
    actor Usuario
    participant Vista as VistaPaciente
    participant Ctrl as ControladorPaciente
    participant Serv as ServicioPaciente
    participant Repo as RepositorioPaciente

    Usuario->>Vista: Selecciona "Buscar por DNI"
    Vista->>Usuario: Solicita el DNI
    Usuario->>Vista: Ingresa el DNI

    Vista->>Ctrl: buscarPorDni(dni)
    Ctrl->>Serv: buscarPorDni(dni)

    Note over Serv: Validación: dni no nulo ni vacío

    alt DNI nulo o vacío
        Serv-->>Ctrl: lanza DatoInvalidoException
        Ctrl-->>Vista: propaga la excepción
        Vista->>Usuario: "Error: Intente nuevamente, por favor"
    end

    Serv->>Repo: buscarPorDni(dni)
    Note over Repo: Recorre HashMap buscando<br/>paciente con ese DNI
    Repo-->>Serv: Paciente o null

    alt Caso exitoso (Paciente encontrado)
        Serv-->>Ctrl: Paciente
        Ctrl-->>Vista: Paciente
        Vista->>Usuario: Muestra datos del paciente (toString)
    end

    alt Caso fallido (null → no encontrado)
        Serv-->>Ctrl: lanza PacienteNoEncontradoException
        Ctrl-->>Vista: propaga la excepción
        Vista->>Usuario: "Error: El paciente no está en el sistema"
    end
```
