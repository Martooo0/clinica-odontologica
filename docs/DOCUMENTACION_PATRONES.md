# 4. GRASP, SOLID y Patrones

Las rutas son relativas a `src/`. Los códigos siguen la misma lógica de los RF y CS: **GR** para GRASP, **SO** para SOLID y **PA** para Patrones de diseño.

## 4.1. Expert

- GR-01: ¿Quién sabe cuánto descuenta cada obra social? `ObraSocial` (dominio). Cada constante guarda su porcentaje y `calcularDescuento(precio)` hace la cuenta (precio × porcentaje / 100, redondeado a 2 decimales con `BigDecimal`). Ningún servicio ni vista repite ese cálculo.
- GR-02: ¿Quién calcula cuánto paga el paciente? `Cotizacion` (dominio). Tiene el precio de lista del tratamiento y le pide el descuento a la obra social, así que ella misma resuelve `getTotal()` (precio de lista − descuento).
- GR-03: ¿Quién sabe armar el nombre completo o el nombre del tratamiento? `Persona.getNombreCompleto()` y `Turno.getNombreTratamiento()`. Cada objeto expone un dato derivado de lo que ya tiene, y las vistas no concatenan campos.
- GR-04: ¿Quién sabe describir un evento? Cada subclase de `Evento` (`EventoTurno`, `EventoPaciente`, `EventoOdontologo`, `EventoTratamiento`) implementa `getDetalle()` con los datos de su entidad. `EventoTurno` y `EventoPaciente` también responden `involucraA(Paciente)`.
- GR-05: ¿Quién sabe si un tratamiento se puede borrar? `ServicioTratamiento`, porque es el que consulta los turnos asociados (RF-13). Lo mismo con `ServicioPaciente` para el DNI repetido (RF-03) y `ServicioOdontologo` para la matrícula repetida (RF-09).

## 4.2. Creator

- GR-06: `ServicioPago.generarPago` crea el `Pago` y `ServicioPago.cotizar` crea la `Cotizacion`. Son quienes tienen los datos (turno, tratamiento, paciente) para construirlos. El pago nace PENDIENTE con los montos congelados (RF-35, RF-36).
- GR-07: Cada repositorio crea sus entidades al leer el archivo, en `parsearLinea` (`RepositorioPaciente`, `RepositorioTurno`, etc.). Son quienes conocen el formato en que se guardó la entidad.
- GR-08: `FormularioPaciente.getDatos` arma el `Domicilio` y el `DatosPaciente` a partir de los campos que él mismo contiene.
- GR-09: `ControladorTurno.crearTurno` arma el `Turno` desde la `SolicitudTurno` (parsea la fecha y la hora). Es discutible, porque la creación de entidades podría vivir en el servicio.
- GR-10: `Main` crea todo el grafo de objetos (repositorios, servicios, controladores y ventanas) y los conecta por constructor.

## 4.3. Controller

- GR-11: Hay un controlador por área: `ControladorPaciente`, `ControladorOdontologo`, `ControladorTratamiento`, `ControladorTurno` y `ControladorPago`. Reciben los pedidos de la vista y los delegan al servicio. Las vistas nunca hablan directo con un servicio.
- GR-12: Los controladores traducen lo que escribe el usuario a tipos del sistema. `ControladorTurno.leerFecha` / `leerHora` convierten los textos a `LocalDate` / `LocalTime` y lanzan `DatoInvalidoException` si el formato es inválido (RF-51). `ControladorTratamiento` hace lo mismo con el precio.
- GR-13: `Controladores` agrupa los 5 controladores en un solo objeto, así las ventanas lo reciben en un único parámetro.
- GR-14: A mejorar: los controladores casi solo delegan (Middle Man, CS-47) y `VentanaPaciente` todavía conserva la lógica de la sesión del paciente.

## 4.4. Low Coupling

- GR-15: Las capas solo dependen de la de abajo: vista → controlador → servicio → repositorio → dominio. El dominio no conoce a nadie.
- GR-16: Los servicios no conocen a las ventanas. Avisan los cambios por el patrón Observer (`Sujeto` / `Observador`) y las ventanas se enteran sin que el servicio sepa quiénes son.
- GR-17: Los DTO `DatosPaciente` y `SolicitudTurno` reemplazan las listas largas de parámetros (CS-30, CS-31) y evitan que la vista, el controlador y el servicio se acoplen a cada campo.
- GR-18: Las dependencias entre repositorios, servicios, controladores y ventanas se pasan por constructor y se arman en `Main`, en vez de que cada clase cree a sus colaboradores.
- GR-19: A mejorar: `VentanaPaciente` y `VentanaTurno` conocen varios controladores, y `ServicioTurno` depende de `ServicioPago`.

## 4.5. High Cohesion

- GR-20: `Validador` reúne las validaciones comunes de los servicios (`requerir`, `requerirTexto`, `validarNombreYApellido`).
- GR-21: `Dialogos`, `Campos` y `Formato` agrupan cada uno un solo tipo de ayuda para las vistas (mensajes, campos de texto y formato de datos).
- GR-22: `VentanaPaciente` se dividió en `PanelSolicitudTurno`, `PanelMisTurnos`, `AreaNotificaciones` y `FormularioPaciente`. Cada pieza hace una sola cosa.
- GR-23: Cada repositorio solo sabe de su entidad (`serializar`, `parsearLinea`, `getArchivo` y sus búsquedas propias). La persistencia común está en `RepositorioBase`.
- GR-24: A mejorar: `ServicioTurno` y `ServicioPago` tienen varias responsabilidades (ver SO-04).

## 4.6. Polymorphism

- GR-25: `Evento` define el método abstracto `getDetalle()` y cada subclase lo resuelve a su manera, sin `if` por tipo de entidad (antes era CS-37).
- GR-26: `RepositorioBase` deja tres métodos abstractos (`getArchivo`, `serializar`, `parsearLinea`) y cada repositorio los implementa. `Main` guarda y carga todos los repositorios con una sola lista `List<RepositorioBase<?>>`.
- GR-27: `VentanaABM` define el esqueleto de Agregar / Modificar / Eliminar / Limpiar y las ventanas `VentanaGestionPacientes`, `VentanaOdontologo` y `VentanaTratamiento` completan los métodos abstractos (antes era CS-13).
- GR-28: `Observador.actualizar(Evento)` es implementado por 6 ventanas distintas y cada una reacciona a su manera.
- GR-29: `Persona` es la base de `Paciente` y `Odontologo`. `Validador.validarNombreYApellido(Persona)` funciona con ambos.
- GR-30: A mejorar: quedan `switch` por estado (`ServicioTurno.eventoPara`, `ServicioPago.sincronizarConTurno`), por categoría de evento (`VentanaPaciente`, `VentanaTurno`) y por tipo de búsqueda (`VentanaBusqueda`).

## 4.7. Pure Fabrication

- GR-31: `Validador` no representa nada del mundo real. Se inventó para concentrar las validaciones y que no se repitan en los servicios.
- GR-32: Los repositorios (`RepositorioBase` y sus 5 subclases) no son entidades del consultorio: se crearon para separar la persistencia de las reglas de negocio.
- GR-33: Los servicios y controladores tampoco existen en el dominio. Son clases de fabricación para ordenar las responsabilidades.
- GR-34: `Dialogos`, `Campos`, `Formato`, `Constantes`, `ModeloTablaSoloLectura` y `ComboEntidad` son clases de apoyo de las vistas, creadas para eliminar código duplicado (CS-14, CS-15).

## 4.8. Indirection

- GR-35: `ControladorObservable` está entre la ventana y el servicio que emite eventos. La ventana se suscribe al controlador, y el controlador delega al `Sujeto` del servicio.
- GR-36: Los controladores se interponen entre la vista y los servicios, y los repositorios entre los servicios y los archivos. Si cambia el medio de guardado, los servicios no se enteran.
- GR-37: `Ventana.suscribir(...)` es el único punto por el que las ventanas se registran y se dan de baja (antes estaba copiado en 4 ventanas, CS-20).

## 4.9. Protected Variations

- GR-38: `Sujeto` y `Observador` protegen a los servicios de los cambios en la interfaz. Se puede agregar, sacar o rehacer una ventana sin tocar la lógica de negocio.
- GR-39: La jerarquía `ClinicaException` (con una subclase por caso) permite que las vistas capturen un único tipo de error y muestren su mensaje (RF-51).
- GR-40: `Pago` congela precio de lista, obra social, porcentaje, descuento y total al crear el turno. Los cambios posteriores de tarifa o de obra social no lo afectan (RF-36, RF-37).
- GR-41: Los errores de lectura o escritura de archivos se transforman en `PersistenciaException` dentro de `RepositorioBase`, para que el resto del sistema no dependa de `IOException`.
- GR-42: `RepositorioPaciente.parsearLinea` tolera archivos viejos sin la columna de obra social y asigna Particular (RF-04).

## 4.10. SRP – Responsabilidad única

- SO-01: Cada clase de apoyo tiene una sola razón de cambio: `Validador` (validaciones), `Dialogos` (mensajes), `Campos` (campos de texto), `Formato` (formateo) y `Constantes` (valores comunes).
- SO-02: `RepositorioBase` tiene la persistencia común y cada repositorio solo define su entidad. Los controladores solo traducen datos de entrada, y los servicios solo tienen reglas de negocio.
- SO-03: `VentanaPaciente` se partió en `PanelSolicitudTurno`, `PanelMisTurnos`, `AreaNotificaciones` y `FormularioPaciente`.
- SO-04: A mejorar: `ServicioTurno` valida fechas, controla la disponibilidad de agenda, genera los pagos, decide qué evento corresponde por estado y notifica. `ServicioPago` cotiza, genera, cobra, sincroniza con el turno y calcula totales. `Main` arma el grafo, posiciona ventanas y guarda al cerrar. `ServicioTratamiento.inicializarCatalogo` tiene los datos iniciales dentro del servicio.

## 4.11. OCP – Abierto/Cerrado

- SO-05: Agregar un repositorio nuevo solo requiere extender `RepositorioBase` e implementar `getArchivo`, `serializar` y `parsearLinea`, sin tocar la clase base.
- SO-06: Agregar una ventana ABM nueva solo requiere extender `VentanaABM` y completar sus métodos abstractos.
- SO-07: Agregar una obra social nueva es sumar una constante a `ObraSocial` con su porcentaje. `calcularDescuento` no cambia (RF-14).
- SO-08: Agregar un tipo de evento nuevo es sumar una constante a `TipoEvento` o una subclase de `Evento`. Agregar un observer nuevo no obliga a modificar ningún servicio.
- SO-09: A mejorar: `ServicioTurno.eventoPara` y `ServicioPago.sincronizarConTurno` usan `switch` / `if` por `EstadoTurno`; un estado nuevo obligaría a modificarlos. Lo mismo pasa con los `switch` por categoría de evento en `VentanaPaciente` y `VentanaTurno`. Un patrón State resolvería el primer caso.

## 4.12. LSP – Sustitución de Liskov

- SO-10: `Paciente` y `Odontologo` pueden usarse donde se espera una `Persona` (por ejemplo en `Validador.validarNombreYApellido`).
- SO-11: Todos los repositorios pueden tratarse como `RepositorioBase<?>`. `Main` llama a `cargar()` y `guardarTodos()` sin saber de qué entidad se trata.
- SO-12: Todas las subclases de `Evento` cumplen el contrato de `getMensaje()` / `getDetalle()`, y cualquier `Observador` puede recibir cualquiera de ellas.
- SO-13: A mejorar: `ComboEntidad` extiende `JComboBox<String>` y un `addItem` externo desincroniza sus datos internos. Sería más seguro usar composición. Además `Paciente.compareTo` compara solo por apellido y no hay `equals`.

## 4.13. ISP – Segregación de interfaces

- SO-14: `Observador` tiene un solo método (`actualizar`), así que quien lo implementa no depende de nada que no use.
- SO-15: `Identificable` (`getId` / `setId`) e `IRepositorio` (`registrar`, `buscarPorId`, `listarTodos`, `actualizar`, `eliminar`) son interfaces chicas y cohesivas.
- SO-16: A mejorar: `Controladores` se pasa entero a ventanas que usan uno o dos controladores. `Evento.involucraA(Paciente)` obliga a `EventoOdontologo` y `EventoTratamiento` a heredar un método que no les sirve.

## 4.14. DIP – Inversión de dependencias

- SO-17: `Sujeto` depende de la interfaz `Observador`, no de ventanas concretas. `Ventana.suscribir` y `ControladorObservable` también trabajan contra abstracciones.
- SO-18: `RepositorioBase<T extends Identificable>` depende de la interfaz `Identificable`, no de las entidades concretas.
- SO-19: Las dependencias entran por constructor (inyección manual en `Main`), lo que facilita reemplazarlas.
- SO-20: A mejorar (parcial): existe `IRepositorio<T>`, pero los servicios no la usan y reciben el repositorio concreto (`RepositorioPaciente`, `RepositorioTurno`, etc.). Tampoco los controladores dependen de interfaces de servicio. Para cumplir el principio, los servicios deberían recibir `IRepositorio<T>`.

## 4.15. Observer

- PA-01: Problema que resuelve: mantener sincronizadas las ventanas abiertas sin que nadie apriete "actualizar", y cumplir las notificaciones de los RF-44 a RF-47. Ejemplos: el paciente pide un turno y la ventana del secretario se refresca sola con un aviso (RF-44); el secretario confirma o cancela y el paciente lo ve en "Mis turnos" (RF-45); el secretario modifica los datos del paciente y se actualiza su obra social y descuento (RF-46); si se elimina al paciente con la sesión abierta, su ventana se cierra (RF-47). Los servicios no conocen ninguna ventana: solo publican eventos.
- PA-02: Observador (interfaz), `observador/Observador.java`. Tiene un único método `void actualizar(Evento evento)`. Lo implementan las ventanas que quieren enterarse de los cambios.
- PA-03: Sujeto (clase base), `observador/Sujeto.java`. Guarda la lista de observadores y ofrece `agregarObservador`, `quitarObservador` y `notificarObservadores(Evento)`.
    - `agregarObservador` ignora `null` y duplicados.
    - `notificarObservadores` es `protected`: solo los servicios pueden emitir eventos, las vistas no.
    - Recorre una copia de la lista, así un observer puede darse de baja mientras se notifica (por ejemplo, una ventana que se cierra al recibir el evento).
- PA-04: Sujetos concretos: los 5 servicios (`ServicioPaciente`, `ServicioOdontologo`, `ServicioTratamiento`, `ServicioTurno` y `ServicioPago`) extienden `Sujeto`. Cada vez que algo cambia, llaman a `notificarObservadores(new EventoXxx(...))` después de guardar el cambio.
    - `ServicioPaciente`: `PACIENTE_REGISTRADO`, `PACIENTE_MODIFICADO`, `PACIENTE_ELIMINADO`.
    - `ServicioOdontologo`: `ODONTOLOGO_REGISTRADO`, `ODONTOLOGO_MODIFICADO`, `ODONTOLOGO_ELIMINADO`.
    - `ServicioTratamiento`: `TRATAMIENTO_REGISTRADO`, `TRATAMIENTO_MODIFICADO`, `TRATAMIENTO_ELIMINADO`.
    - `ServicioTurno`: `TURNO_SOLICITADO` (lo pide el paciente), `TURNO_ASIGNADO` (lo asigna el secretario), `TURNO_CONFIRMADO`, `TURNO_CANCELADO`, `TURNO_CANCELADO_POR_PACIENTE`, `TURNO_COMPLETADO`, `TURNO_PENDIENTE`, `TURNO_REPROGRAMADO` y `TURNO_ELIMINADO`.
    - `ServicioPago`: `PAGO_REGISTRADO` (se envía como `EventoTurno`).
- PA-05: Evento (el mensaje), `observador/Evento.java` y sus subclases `EventoTurno`, `EventoPaciente`, `EventoOdontologo` y `EventoTratamiento`. Lleva toda la información que el observer necesita (modelo push), así que no tiene que consultarle nada al servicio.
    - Usa Template Method: `getMensaje()` arma el texto "[hora] descripción — detalle" y el `getDetalle()` lo completa cada subclase.
    - `involucraA(Paciente)` permite que `VentanaPaciente` sepa si el evento es sobre el paciente que tiene la sesión iniciada.
- PA-06: `TipoEvento` (enum) define los 19 tipos de evento. Cada uno trae su descripción, su `Categoria` (TURNO, PAGO, PACIENTE, ODONTOLOGO o TRATAMIENTO) y el indicador `esAccionDelPaciente()`, que es verdadero para `TURNO_SOLICITADO`, `TURNO_CANCELADO_POR_PACIENTE` y `PACIENTE_REGISTRADO`. Con eso la ventana del secretario solo avisa cuando el cambio lo hizo un paciente, y la del paciente no se avisa a sí misma por sus propias acciones.
- PA-07: `ControladorObservable` (clase base de los controladores). Envuelve al `Sujeto` del servicio y expone `agregarObservador` y `quitarObservador`. Así las vistas se suscriben a través del controlador y nunca tocan el servicio.
- PA-08: `Ventana.suscribir(observador, controladores...)` es el método para registrarse. Hace el alta en cada controlador indicado y deja registrada la baja automática en `windowClosed`, de modo que al cerrar la ventana deja de recibir eventos.
- PA-09: Observadores concretos y qué hacen en `actualizar`:
    - `VentanaPaciente` (se suscribe a turno, pago, paciente, odontólogo y tratamiento): recarga los combos si cambian los odontólogos o tratamientos; si el evento es sobre el paciente de la sesión, la cierra si lo eliminaron o la refresca si lo modificaron; refresca "Mis turnos" y notifica (con aviso sonoro) solo si el cambio no fue una acción propia.
    - `VentanaSecretario` (turno y paciente): refresca la lista de solicitudes pendientes y notifica si `esAccionDelPaciente()`.
    - `VentanaTurno` (turno, paciente, odontólogo y tratamiento): recarga los combos y la tabla de turnos.
    - `VentanaPagos` (pago y turno): refresca la tabla de pagos.
    - `VentanaBusqueda` (paciente y odontólogo): vuelve a ejecutar la búsqueda actual.
    - `VentanaGestionPacientes` (paciente): refresca la tabla de pacientes.
- PA-10: Flujo completo cuando el paciente solicita un turno:
    1. `PanelSolicitudTurno` arma la `SolicitudTurno` y llama a `ControladorTurno.solicitar`.
    2. El controlador convierte fecha y hora, crea el `Turno` y llama a `ServicioTurno.solicitar`.
    3. El servicio valida, registra el turno y genera el pago PENDIENTE con `ServicioPago.generarPago`.
    4. El servicio llama a `notificarObservadores(new EventoTurno(TURNO_SOLICITADO, turno))`.
    5. `Sujeto` recorre sus observadores y llama a `actualizar` de cada uno: `VentanaSecretario` refresca y avisa, `VentanaPaciente` refresca "Mis turnos" sin avisar, y `VentanaTurno` y `VentanaPagos` refrescan sus tablas.
    - La notificación es sincrónica (corre en el hilo de Swing) y el pago se genera antes de notificar, para que `VentanaPagos` ya lo encuentre al refrescar.
- PA-11: Cómo extenderlo: para un observer nuevo se implementa `Observador` y se llama a `suscribir(this, ...)` en el constructor de la ventana, sin tocar ningún servicio. Para un evento nuevo se agrega la constante en `TipoEvento` y el servicio lo emite con `notificarObservadores`.
- PA-12: A mejorar:
    - `Sujeto.notificarObservadores` no captura excepciones: si un observer falla, los siguientes no se enteran.
    - Los servicios extienden `Sujeto`, lo que gasta su única herencia. Con composición sería más flexible.
    - `VentanaPaciente` y `VentanaTurno` usan un `switch` por categoría, que hay que tocar cuando aparece una categoría nueva.
    - `PAGO_REGISTRADO` viaja en un `EventoTurno` porque no existe `EventoPago`.
    - `Evento.involucraA(Paciente)` acopla la clase base al dominio y no tiene sentido para odontólogos ni tratamientos.

## 4.16. Otros patrones de diseño

- PA-13: Template Method. `RepositorioBase` define `guardarTodos()` y `cargar()` y delega los pasos específicos a `getArchivo`, `serializar` y `parsearLinea`. `VentanaABM` define el flujo de agregar, modificar y eliminar, y las ventanas hijas completan los pasos. `Evento.getMensaje` delega en `getDetalle`.
- PA-14: Repository. `IRepositorio<T>`, `RepositorioBase` y los repositorios concretos esconden dónde y cómo se guardan los datos (archivos .txt separados por `;`). Los servicios piden y guardan entidades sin saber el formato.
- PA-15: Arquitectura en capas (MVC): vista → controlador → servicio → repositorio → dominio, como se describe en el punto 1.
- PA-16: DTO / Parameter Object. `DatosPaciente` y `SolicitudTurno` agrupan datos que viajan juntos entre la vista, el controlador y el servicio.
- PA-17: Inyección de dependencias por constructor. `Main` es el único lugar donde se crean y conectan los objetos.
- PA-18: Strategy funcional. `ComboEntidad` recibe una `Function` para armar el texto de cada ítem, `RepositorioBase.filtrar` y `buscarPrimero` reciben un `Predicate`, y `Validador.requerir` recibe un `Supplier` que crea la excepción a lanzar.
- PA-19: Excepciones propias. `ClinicaException` (no verificada) y una subclase por caso de negocio (`DniDuplicadoException`, `TurnoYaReservadoException`, `PersistenciaException`, etc.).
- PA-20: No se usan Factory ni Singleton ni State. State sería la mejor opción para ordenar los estados del turno (PENDIENTE, CONFIRMADO, COMPLETADO y CANCELADO) y las transiciones permitidas.
