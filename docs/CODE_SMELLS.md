# Code smells — clínica odontológica

Revisión de los 52 archivos Java del proyecto. Las rutas son relativas a `../src` y, dentro de cada sección, los puntos van de mayor a menor impacto.

---

## 1. Código duplicado (lo más importante)

### Repositorios
1. **CRUD idéntico en los 5 repositorios.** `guardar`, `buscarPorId`, `buscarTodos`, `actualizar`, `eliminar`, el `Map` + `contadorId` y las constantes `ARCHIVO`/`SEP` son iguales en `RepositorioPaciente`, `RepositorioOdontologo`, `RepositorioTratamiento`, `RepositorioTurno` y `RepositorioPago` (por ejemplo `RepositorioOdontologo.java:18-44`). Se puede sacar a una clase abstracta `RepositorioBase<T>` que implemente `IRepositorio<T>`.
2. **Estructura de `cargar()` copiada en los 5 repos.** Abre el archivo, recorre las líneas, saltea las vacías, hace `split(SEP)`, actualiza `contadorId` y hace `finally { br.close() }` con el mismo "Error al cerrar el archivo" (por ejemplo `RepositorioPaciente.java:74-119`, `RepositorioTurno.java:109-156`). Solo cambia cómo se arma cada objeto: conviene un método plantilla en la base con un `abstract T parsearLinea(String[] partes)`.
3. **`guardarTodos()` repetido** en los 5 repos, con el mismo try/finally. También podría ir a la base con un `abstract String serializar(T)`.
4. Dentro de `RepositorioTurno` hay 6 métodos `buscarPorX` (`:53-87`) que son el mismo stream con otro filtro. Se puede tener un `filtrar(Predicate<Turno>)` privado.

### Servicios
5. **La validación de `registrar` y `modificar` está copiada:**
   - `ServicioOdontologo.java:27-38` contra `:65-76`
   - `ServicioPaciente.java:30-50` contra `:83-100`

   Extraer un `validar(...)` privado, como ya hace bien `ServicioTratamiento.validar()` (`:97-110`).
6. **Validar nombre y apellido se repite entre Paciente y Odontólogo.** Los dos son `Persona`, así que se puede tener un `validarPersona(Persona)` común.
7. **El DNI y el email se validan dos veces con el mismo mensaje** (`ServicioPaciente.java:39-44` y `:45-50`). Basta con una sola condición por campo.
8. **El patrón "buscar y lanzar excepción si es null" se duplica entre servicios.**
   - `ServicioTurno.buscarPaciente` (`:233`) duplica `ServicioPaciente.buscarPorId`.
   - `ServicioTurno.buscarTratamiento` (`:241`) duplica `ServicioTratamiento.buscarPorId`.
   - El odontólogo se busca inline en `ServicioTurno.java:89-92`.

   `ServicioTurno` usa directamente los repositorios de otras entidades en lugar de sus servicios.
9. **`cambiarEstado` y `cancelarPorPaciente` repiten el mismo bloque** de `setEstado` → `actualizar` → `sincronizarConTurno` → `notificar` (`ServicioTurno.java:120-123` y `:136-139`).
10. **`ServicioOdontologo` valida dos veces que el odontólogo exista.** `ControladorOdontologo.modificar` llama a `buscarPorId` (`ControladorOdontologo.java:25`) y después `ServicioOdontologo.modificar` lo vuelve a llamar (`:81`).

### Controladores
11. **`eliminarPorId` con el mismo try/catch → `return false`** en 4 controladores (Paciente, Odontólogo, Tratamiento, Turno).
12. **`agregarObservador`/`quitarObservador` copiados en los 5 controladores.**

### Vistas (la mayor cantidad de duplicación)
13. **`VentanaGestionPacientes`, `VentanaOdontologo` y `VentanaTratamiento` son casi la misma ventana ABM.** Se repiten:
    - `configurarVentana`, `crearBotones` (los 4 botones)
    - `accionModificar`/`accionEliminar` (validar selección, confirmar, mensaje)
    - `cargarFilaEnFormulario`, `limpiarCampos`
    - `hayCamposVacios`/`todosLosCampos` y `agregarCampo`

    Se puede crear una clase base abstracta, por ejemplo `VentanaABM`.
14. **`DefaultTableModel` anónimo con `isCellEditable → false` copiado 8 veces** (Busqueda:84, GestionPacientes:119, Odontologo:70, Paciente:181, Pagos:63, Secretario:90, Tratamiento:61, Turno:191). Conviene un helper `crearModeloSoloLectura(String[] columnas)`.
15. **`JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", ERROR_MESSAGE)` aparece unas 15 veces.** Crear helpers `mostrarError` / `mostrarExito` / `confirmar`.
16. **El parseo de fecha y hora, con el mismo mensaje "Fecha u hora con formato inválido…", está tres veces:**
    - `VentanaTurno.java:244-252`
    - `VentanaTurno.java:261-269`
    - `VentanaPaciente.java:319-331`
17. **El llenado de combos se repite dentro de `VentanaTurno`.** `crearFormulario` (`:149-165`) hace lo mismo que `recargarComboPacientes/Odontologos/Tratamientos` (`:97-131`); `crearFormulario` podría llamar a los métodos `recargar...`. `VentanaPaciente.cargarCombos` (`:378-409`) repite la misma lógica de "recordar el id elegido y volver a seleccionarlo".
18. **El formulario de paciente está duplicado.** `VentanaPaciente.accionRegistrarme` (`:261-280`) arma los mismos 9 campos que `VentanaGestionPacientes.crearFormulario`.
19. **Se repite crear el área de notificaciones y la secuencia append + caret + beep:**
    - `VentanaPaciente.java:202-205` y `:240-242`
    - `VentanaSecretario.java:114-117` y `:135-137`
20. **Se repiten la suscripción a observadores y la baja en `windowClosed`** en `VentanaBusqueda`, `VentanaGestionPacientes`, `VentanaPagos` y `VentanaTurno`.
21. **`pago == null ? "-" : Formato.moneda(pago.getMontoFinal())` se repite** en `VentanaPaciente:439` y `VentanaSecretario:186`.

---

## 2. Nombres

22. **Nombres de métodos demasiado largos:**
    - `cargarCatalogoInicialSiEstaVacio` (`ServicioTratamiento.java:85`, `Main.java:62`) → `inicializarCatalogo`.
    - `listarOrdenadosPorApellido` → `listarPorApellido`.
23. **Nombres que esconden efectos secundarios:**
    - `hayCamposVacios()` parece una pregunta, pero además pinta los bordes en rojo (GestionPacientes:257, Odontologo:188). Mejor `marcarCamposVacios()`.
    - `idSeleccionado()` en `VentanaSecretario.java:164` además muestra un diálogo, y comparte nombre con el campo `idSeleccionado` de las otras ventanas.
24. **Sobrecarga confusa:** en `VentanaBusqueda` hay un `actualizar()` privado y un `actualizar(Evento)` del Observer (`:49` y `:96`). Renombrar el privado a `filtrar()` o `refrescarTabla()`.
25. **El nombre no coincide con lo que devuelve:** `crearPanelNotificaciones()` devuelve un `JScrollPane`, no un panel (`VentanaPaciente.java:201`).
26. **Nombres crípticos o abreviados:**
    - `iTra`, `iOdo`, `iPac` (`VentanaPaciente:312`, `VentanaTurno:231`)
    - `c` para la `Cotizacion` (`VentanaPaciente:366`)
    - `fw`, `pw`, `br`, `SEP`, `confirm`
    - variables de una letra (`p`, `o`, `t`) en métodos largos
27. **Vocabulario inconsistente para la misma acción:**
    - `buscarTodos` (repo) vs `listarTodos` (servicio/controlador)
    - `eliminar` vs `eliminarPorId`
    - `ControladorTurno.asignar` llama a `ServicioTurno.reservar`, y el parámetro se llama `fechaInicio` en uno y `fecha` en el otro (`ControladorTurno.java:23`)
    - `registrar` vs `guardar`
28. **Mensajes de excepción inconsistentes:** "no esta" vs "no está", y varios terminan en `": "` sin sentido (`ServicioPaciente:56,66,73`, `ServicioOdontologo:41,51,59`).
29. **Typo en un comentario:** "miod" (`VentanaPaciente.java:219`).

---

## 3. Listas de parámetros largas / Data Clumps

30. **`ControladorPaciente.registrar` y `modificar` reciben 9 parámetros** (`:20`, `:26`). `calle, numero, localidad, provincia` viajan siempre juntos: pasar un `Domicilio` o un DTO.
31. **`(idPaciente, idOdontologo, idTratamiento, fecha, hora)` se repite en 4 firmas** (`ControladorTurno.asignar`/`solicitar`, `ServicioTurno.solicitar`/`reservar`/`crearTurno`).
32. **Constructores de ventanas con 5 controladores** (`VentanaPaciente:63`, `VentanaSecretario:32`) y `ServicioTurno` con 5 dependencias (`:47`).
33. **Líneas larguísimas:**
    - `RepositorioPaciente.java:65` (serialización en una sola línea)
    - `VentanaGestionPacientes.java:167` y `:185`

---

## 4. Métodos y clases largas

34. **`VentanaPaciente` (444 líneas) hace de todo:** identificación, registro, solicitud, cotización, mis turnos y notificaciones. Es candidata a dividirse en paneles.
35. **`VentanaPaciente.actualizar(Evento)` (`:212-244`)** tiene muchas ramas `if` por tipo de evento.
36. **`VentanaPaciente.accionRegistrarme` (`:259-301`)** arma la UI y además tiene el loop de reintento.
37. **`Evento` hace "type checking" con campos nulos.** Tiene 4 constructores y 4 campos opcionales, y `getMensaje()` (`Evento.java:79-95`) es una cadena de `if (esDeX())`. Esto se resuelve con polimorfismo (subclases de Evento).

---

## 5. Código muerto / no usado

38. **Variables `FileWriter fw` y `FileReader fr` asignadas pero nunca cerradas ni usadas** después de envolverlas (`RepositorioOdontologo:54,73`, `RepositorioPaciente:59,78`, `RepositorioTurno:90,113`). Conviene usar try-with-resources.
39. **Métodos que nadie llama:**
    - `Turno.esFuturo()` (además devuelve `Boolean` en vez de `boolean`)
    - `ServicioTurno.modificar()`
    - `ControladorOdontologo.buscarPorMatricula`
    - `ControladorPaciente.listarOrdenadosPorApellido`
    - `ControladorTurno.listarPorOdontologo` / `listarPorFecha` / `listarPorRangoFechas`
    - `ControladorPago.listarPorPaciente`
40. **`Domicilio.id`** con getter y setter, pero nunca se usa.
41. **`RepositorioPago.buscarPorPaciente`** solo lo usa `ControladorPago.listarPorPaciente`, que tampoco se usa, así que toda esa cadena es código muerto.

---

## 6. Magic numbers / strings

42. **`"-"` como valor vacío está hardcodeado en muchos lugares:** `Formato:14`, `Turno:54`, `RepositorioTurno:99,131`, `RepositorioPago` (ahí sí es constante `VACIO`) y las vistas. Unificar en una constante.
43. **Índices de columnas del archivo sin nombre** (`partes[6]`, `partes[10]`…) en todos los `cargar()`.
44. **La columna del ID (`getValueAt(fila, 0)`) está hardcodeada** en 6 ventanas.
45. **`"Odontólogos".equals(tipo)` compara contra el texto del combo** (`VentanaBusqueda.java:100`). Si cambia la etiqueta, se rompe la lógica.
46. **`"TextField.border"`, los tamaños de ventana y las regex de DNI y precio** están sueltos en el código.

---

## 7. Otros (diseño)

47. **Middle Man:** los 5 controladores casi solo delegan al servicio sin agregar nada.
48. **`eliminarPorId` mezcla dos estilos de manejo de errores.** Devuelve `boolean` para "no encontrado" pero deja pasar otras `ClinicaException` (por ejemplo, "hay turnos con este tratamiento"). Además `VentanaTratamiento.accionEliminar` (`:141`) ignora el `boolean` que devuelve.
49. **`Persona` tiene campos `protected` y también getters y setters,** lo que rompe el encapsulamiento (`Persona.java:5-7`).
50. **Errores de I/O que solo se imprimen con `System.err`:** en todos los repositorios el usuario nunca se entera de que falló el guardado.
51. **`catch (Exception e) {}` vacío** en `Main.java:33` (tiene comentario, pero igual traga cualquier excepción).

---

## Verificación
Después de aplicar los cambios, compilá y probá los flujos principales:
- alta, modificación y baja de paciente, odontólogo y tratamiento;
- pedir, confirmar, cancelar y reprogramar un turno;
- registrar un cobro;
- cerrar y reabrir la app para verificar que se guarda y carga bien.

---

## Conclusión: patrones de diseño aplicables

### Lo que el proyecto ya usa
- **Observer:** `Sujeto` / `Observador` / `Evento`. Los servicios avisan y las ventanas se refrescan solas.
- **Repository:** `IRepositorio<T>` y sus 5 implementaciones.
- **MVC en capas:** vista → controlador → servicio → repositorio.

### Lo que se podría agregar

#### 1. Facade (resuelve 12, 32 y 47)
Crear una clase `ClinicaFacade` (o `SistemaClinica`) que sea la única puerta de entrada de la vista al sistema. Tendría que:
- agrupar las operaciones de pacientes, odontólogos, tratamientos, turnos y pagos;
- tener `agregarObservador` / `quitarObservador` en un solo lugar;
- tener `guardarTodo()`, que reemplaza las 5 llamadas a `guardarTodos()` de `Main.java:84-88`;
- tener `cargarTodo()`, que esconde el orden de carga de `Main.java:49-53`.

Con esto, `VentanaPaciente` y `VentanaSecretario` reciben **un** objeto en vez de 5 controladores, y desaparecen los controladores que solo delegan (Middle Man). Es el patrón que más simplifica el proyecto.

#### 2. Template Method (resuelve 1, 2, 3 y 13)
- **`RepositorioBase<T>` abstracta:** implementa el CRUD, `cargar()` y `guardarTodos()` una sola vez. Cada repositorio solo implementa:
  - `T parsearLinea(String[] partes)`
  - `String serializar(T entidad)`
  - `String getArchivo()`
- **`VentanaABM` abstracta:** arma el formulario, la tabla y los botones Agregar/Modificar/Eliminar/Limpiar. `VentanaGestionPacientes`, `VentanaOdontologo` y `VentanaTratamiento` solo definen sus columnas, sus campos y qué método del controlador llamar.

#### 3. State (resuelve 9 y la lógica de estados de `ServicioTurno`)
Hoy las reglas de transición están dispersas en `ServicioTurno`:
- `cambiarEstado` (`:111-125`);
- `cancelarPorPaciente` (`:133`), que dice qué estados se pueden cancelar;
- `eventoPara` (`:252-259`), un `switch` sobre el estado.

A eso se suma `ServicioPago.sincronizarConTurno` (`:74-85`). Con State, cada estado (`Pendiente`, `Confirmado`, `Completado`, `Cancelado`) sabe:
- a qué estados puede pasar;
- si el paciente lo puede cancelar;
- qué `TipoEvento` dispara;
- qué le pasa al pago.

Así se eliminan los `if`/`switch` sobre `EstadoTurno`.

#### 4. Builder / Parameter Object (resuelve 30 y 31)
- **Builder (`Paciente.builder()...build()`):** evita los 9 parámetros de `ControladorPaciente.registrar` / `modificar`. Como mínimo, pasar un `Domicilio` armado en lugar de calle, número, localidad y provincia sueltos.
- **Parameter Object `SolicitudTurno`:** un objeto con `idPaciente, idOdontologo, idTratamiento, fecha, hora` que reemplaza esos 5 parámetros en las 4 firmas donde se repiten.

#### 5. Factory Method / polimorfismo en Evento (resuelve 37)
Reemplazar los 4 constructores y los campos nulos de `Evento` por subclases:
- `EventoTurno`
- `EventoPaciente`
- `EventoOdontologo`
- `EventoTratamiento`

Cada una implementa su propio `getMensaje()`. Se crean con métodos fábrica (`Evento.deTurno(...)`, `Evento.dePaciente(...)`), y así desaparecen la cadena de `if (esDeX())` y los chequeos de null.

#### 6. Strategy (opcional)
Hoy el descuento de la obra social es un porcentaje fijo en el enum `ObraSocial`, y para eso alcanza. Si en algún momento hubiera reglas distintas (tope máximo, descuento por tratamiento, coseguro), se puede pasar a una interfaz `PoliticaDescuento` con una implementación por obra social. Lo mismo vale para las validaciones (`ValidadorPaciente`, `ValidadorOdontologo`).

### Lo que **no** conviene agregar
- **Singleton** para servicios o repositorios: hoy se crean una vez en `Main` y se pasan por constructor, que es más testeable. Convertirlos en Singleton empeoraría el diseño.
- **Command** para los botones: con lambdas en `addActionListener` alcanza, y no hay deshacer/rehacer que lo justifique.

### Prioridad sugerida
1. **Facade:** el de mayor impacto y el más fácil de explicar.
2. **Template Method:** elimina la mayor cantidad de código duplicado.
3. **State:** ordena la lógica de negocio de los turnos.
4. Builder / Parameter Object y Evento polimórfico.
5. Strategy, solo si cambian las reglas de descuento.
