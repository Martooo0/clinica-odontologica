# Estructura del proyecto

Clínica Odontológica – Aplicación de escritorio en Java (Swing). Rama: `code-smells`.

Las rutas son relativas a la raíz del repositorio. Todo el código fuente está en `src/` y el proyecto no usa Maven ni Gradle (es un módulo de IntelliJ IDEA).

## 1. Árbol de carpetas

```
clinica-odontologica/
|__ .gitignore
|__ ClinicaOdontologica.iml            (módulo de IntelliJ)
|__ clinica-odontologica.iml           (módulo de IntelliJ)
|
|__ docs/                              (documentación del TP)
|   |__ CODE_SMELLS.md                 (code smells detectados)
|   |__ DOCUMENTACION_PATRONES.md      (GRASP, SOLID y Observer)
|   |__ ESTRUCTURA_PROYECTO.md         (este documento)
|
|__ src/
    |__ Main.java                      (arranque: crea y conecta todos los objetos)
    |
    |__ vista/                         (CAPA 1: interfaz Swing)
    |   |__ Ventana.java               (base de ventanas: suscripción al Observer)
    |   |__ VentanaABM.java            (base de ventanas Alta / Baja / Modificación)
    |   |__ VentanaSecretario.java     (menú principal del secretario)
    |   |__ VentanaPaciente.java       (portal del paciente)
    |   |__ VentanaGestionPacientes.java  (ABM de pacientes)
    |   |__ VentanaOdontologo.java     (ABM de odontólogos)
    |   |__ VentanaTratamiento.java    (ABM de tratamientos)
    |   |__ VentanaTurno.java          (gestión de turnos)
    |   |__ VentanaPagos.java          (cobros y listado de pagos)
    |   |__ VentanaBusqueda.java       (búsqueda de pacientes y odontólogos)
    |   |__ PanelSolicitudTurno.java   (panel: el paciente pide un turno)
    |   |__ PanelMisTurnos.java        (panel: turnos del paciente)
    |   |__ AreaNotificaciones.java    (panel: avisos y notificaciones)
    |   |__ FormularioPaciente.java    (formulario reutilizable de paciente)
    |   |__ ComboEntidad.java          (combo que muestra entidades)
    |   |__ ModeloTablaSoloLectura.java  (modelo de tabla no editable)
    |   |__ TipoBusqueda.java          (enum: qué se busca)
    |   |__ Dialogos.java              (mensajes y confirmaciones)
    |   |__ Campos.java                (ayudas para campos de texto)
    |   |__ Formato.java               (formato de moneda, fechas, etc.)
    |
    |__ controlador/                   (CAPA 2: recibe los pedidos de la vista)
    |   |__ ControladorObservable.java (base: expone alta/baja de observadores)
    |   |__ ControladorPaciente.java
    |   |__ ControladorOdontologo.java
    |   |__ ControladorTratamiento.java
    |   |__ ControladorTurno.java
    |   |__ ControladorPago.java
    |   |__ Controladores.java         (agrupa los 5 controladores)
    |
    |__ servicio/                      (CAPA 3: reglas de negocio)
    |   |__ ServicioPaciente.java
    |   |__ ServicioOdontologo.java
    |   |__ ServicioTratamiento.java
    |   |__ ServicioTurno.java
    |   |__ ServicioPago.java
    |   |__ Validador.java             (validaciones comunes)
    |
    |__ repositorio/                   (CAPA 4: guardado en memoria y archivos .txt)
    |   |__ IRepositorio.java          (interfaz del CRUD)
    |   |__ RepositorioBase.java       (CRUD y persistencia comunes)
    |   |__ RepositorioPaciente.java
    |   |__ RepositorioOdontologo.java
    |   |__ RepositorioTratamiento.java
    |   |__ RepositorioTurno.java
    |   |__ RepositorioPago.java
    |
    |__ dominio/                       (CAPA 5: entidades del sistema)
    |   |__ Identificable.java         (interfaz: entidades con id)
    |   |__ Persona.java               (base de Paciente y Odontologo)
    |   |__ Paciente.java
    |   |__ Odontologo.java
    |   |__ Tratamiento.java
    |   |__ Turno.java
    |   |__ Pago.java
    |   |__ Cotizacion.java            (precio de lista, descuento y total)
    |   |__ Domicilio.java
    |   |__ ObraSocial.java            (enum con porcentaje de descuento)
    |   |__ EstadoTurno.java           (enum)
    |   |__ EstadoPago.java            (enum)
    |   |__ MedioPago.java             (enum)
    |   |__ Constantes.java
    |
    |__ observador/                    (TRANSVERSAL: patrón Observer)
    |   |__ Observador.java            (interfaz: lo implementan las ventanas)
    |   |__ Sujeto.java                (base de los servicios: notifica)
    |   |__ Evento.java                (mensaje base)
    |   |__ EventoTurno.java
    |   |__ EventoPaciente.java
    |   |__ EventoOdontologo.java
    |   |__ EventoTratamiento.java
    |   |__ TipoEvento.java            (enum con los 19 tipos de evento)
    |
    |__ dto/                           (datos que viajan entre capas)
    |   |__ DatosPaciente.java
    |   |__ SolicitudTurno.java
    |
    |__ excepcion/                     (errores propios del negocio)
        |__ ClinicaException.java      (base: RuntimeException)
        |__ DatoInvalidoException.java
        |__ DniDuplicadoException.java
        |__ MatriculaDuplicadaException.java
        |__ TurnoYaReservadoException.java
        |__ PacienteNoEncontradoException.java
        |__ OdontologoNoEncontradoException.java
        |__ TratamientoNoEncontradoException.java
        |__ TurnoNoEncontradoException.java
        |__ PagoNoEncontradoException.java
        |__ PersistenciaException.java
```

Total: 76 archivos `.java` (1 en la raíz de `src/` y 75 distribuidos en 8 paquetes).

Archivos de datos: al ejecutar, el sistema guarda `pacientes.txt`, `odontologos.txt`, `tratamientos.txt`, `turnos.txt` y `pagos.txt` en el directorio de trabajo (datos separados por `;`). No forman parte del código fuente.

## 2. Resumen por paquete

| Paquete | Archivos | Responsabilidad |
|---|---|---|
| `Main.java` | 1 | Arma el sistema: crea repositorios, servicios, controladores y ventanas y los conecta |
| `vista` | 20 | Pantallas Swing y componentes reutilizables |
| `controlador` | 7 | Reciben los pedidos de la vista y los pasan al servicio |
| `servicio` | 6 | Reglas de negocio y validaciones |
| `repositorio` | 7 | Guardan y buscan datos (memoria + archivos de texto) |
| `dominio` | 14 | Entidades, enums y objetos de valor |
| `observador` | 8 | Infraestructura del patrón Observer |
| `dto` | 2 | Objetos para transportar datos entre capas |
| `excepcion` | 11 | Excepciones propias del sistema |

## 3. Detalle de cada paquete

### 3.1. `vista`

- Es la capa que ve el usuario. Solo habla con los controladores.
- Bases: `Ventana` (suscripción al Observer) y `VentanaABM` (esqueleto de Alta / Modificación / Eliminación, que completan `VentanaGestionPacientes`, `VentanaOdontologo` y `VentanaTratamiento`).
- Ventanas principales: `VentanaSecretario` (menú del secretario) y `VentanaPaciente` (portal del paciente, armado con `PanelSolicitudTurno`, `PanelMisTurnos` y `AreaNotificaciones`).
- Otras ventanas: `VentanaTurno`, `VentanaPagos` y `VentanaBusqueda`.
- Componentes y ayudas: `FormularioPaciente`, `ComboEntidad`, `ModeloTablaSoloLectura`, `TipoBusqueda`, `Dialogos`, `Campos` y `Formato`.
- Observan los cambios: `VentanaPaciente`, `VentanaSecretario`, `VentanaTurno`, `VentanaPagos`, `VentanaBusqueda` y `VentanaGestionPacientes`.

### 3.2. `controlador`

- Un controlador por área: paciente, odontólogo, tratamiento, turno y pago.
- Convierten lo que escribe el usuario (por ejemplo, fecha y hora como texto) a los tipos del sistema y llaman al servicio.
- `ControladorObservable` es la base de los controladores: deja que las ventanas se suscriban a los cambios del servicio.
- `Controladores` agrupa los 5 controladores para pasarlos juntos a las ventanas.

### 3.3. `servicio`

- Tienen las reglas de negocio: DNI y matrícula únicos, turnos sin superposición, generación y cobro de pagos, cálculo de cotizaciones, etc.
- Hablan con los repositorios y avisan los cambios a las ventanas (cada servicio extiende `Sujeto`).
- `Validador` reúne las validaciones comunes.

### 3.4. `repositorio`

- `IRepositorio<T>` define el CRUD (`registrar`, `buscarPorId`, `listarTodos`, `actualizar`, `eliminar`).
- `RepositorioBase<T>` tiene el CRUD en memoria y la lectura / escritura de archivos. Los repositorios concretos solo definen el archivo, cómo guardar y cómo leer su entidad.
- `RepositorioTurno` y `RepositorioPago` necesitan que otros repositorios ya estén cargados, por eso `Main` los carga en un orden fijo.

### 3.5. `dominio`

- Entidades: `Paciente` y `Odontologo` (heredan de `Persona`), `Tratamiento`, `Turno` y `Pago`.
- Objetos de valor: `Domicilio` y `Cotizacion`.
- Enums: `ObraSocial` (con su descuento), `EstadoTurno`, `EstadoPago` y `MedioPago`.
- `Identificable` es la interfaz de las entidades que tienen id, y `Constantes` guarda valores compartidos.
- No conoce a ninguna otra capa.

### 3.6. `observador`

- `Observador` (interfaz) y `Sujeto` (clase base) forman el patrón Observer.
- `Evento` y sus subclases `EventoTurno`, `EventoPaciente`, `EventoOdontologo` y `EventoTratamiento` llevan la información del cambio.
- `TipoEvento` lista los 19 tipos de evento (turno solicitado, paciente registrado, pago registrado, etc.).
- Detalle completo en `DOCUMENTACION_PATRONES.md`, punto 4.15.

### 3.7. `dto`

- `DatosPaciente`: datos del formulario de paciente.
- `SolicitudTurno`: paciente, odontólogo, tratamiento, fecha y hora de un pedido de turno.

### 3.8. `excepcion`

- `ClinicaException` es la base (no verificada) y todas las demás extienden de ella.
- Por caso de negocio: dato inválido, DNI duplicado, matrícula duplicada, turno ya reservado, entidad no encontrada (paciente, odontólogo, tratamiento, turno y pago) y error de persistencia.
- Las vistas capturan `ClinicaException` y muestran el mensaje al usuario.

## 4. Cómo se comunican las capas

```
   [ vista ]  ->  [ controlador ]  ->  [ servicio ]  ->  [ repositorio ]  ->  [ archivos .txt ]
       ^                                    |
       |                                    |  usa
       |                                    v
       |                               [ dominio ]
       |
       |__ eventos (Observer) <-- [ observador ] <-- los servicios los emiten
```

- Cada capa solo habla con la de abajo: la vista nunca llama a un servicio, ni el servicio a una vista.
- `dominio`, `dto` y `excepcion` los usan varias capas, pero no dependen de ninguna.
- Las ventanas se enteran de los cambios por el Observer: el servicio emite un evento y las ventanas suscriptas se refrescan.

## 5. Ejemplo de recorrido: el paciente solicita un turno

1. `vista`: `PanelSolicitudTurno` arma una `SolicitudTurno` (`dto`) con lo que eligió el paciente.
2. `controlador`: `ControladorTurno.solicitar` convierte la fecha y la hora y crea el `Turno` (`dominio`).
3. `servicio`: `ServicioTurno.solicitar` valida, y `ServicioPago.generarPago` genera el pago pendiente con una `Cotizacion`.
4. `repositorio`: `RepositorioTurno` y `RepositorioPago` guardan los datos.
5. `observador`: `ServicioTurno` emite un `EventoTurno` con el tipo `TURNO_SOLICITADO`.
6. `vista`: las ventanas suscriptas se refrescan (la del secretario avisa que hay un turno nuevo).
7. `excepcion`: si algo falla (fecha pasada, odontólogo ocupado, etc.) se lanza una `ClinicaException` y la vista muestra el mensaje.

## 6. Dónde agregar cosas nuevas

- **Nueva entidad** (por ejemplo, Consultorio):
    - `dominio`: la clase (implementando `Identificable`).
    - `repositorio`: un repositorio que extienda `RepositorioBase`.
    - `servicio`: un servicio con sus reglas.
    - `controlador`: un controlador, y sumarlo a `Controladores`.
    - `Main`: crear y conectar los nuevos objetos.
- **Nueva ventana ABM:** extender `VentanaABM` en `vista` y completar sus métodos.
- **Nuevo evento:** agregar el tipo en `TipoEvento` y emitirlo desde el servicio con `notificarObservadores`.
- **Nuevo error de negocio:** crear una clase que extienda `ClinicaException` en `excepcion`.
- **Nueva obra social:** sumar una constante a `ObraSocial` con su porcentaje.
