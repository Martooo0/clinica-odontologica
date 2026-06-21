# Estado del TP — Clínica Odontológica

> Última actualización: **23/05/2026** — TP completo y funcionando, falta probar manualmente y pushear.
>
> **Entrega: martes 26/05/2026.**
>
> Documento de contexto pensado para retomar el TP en otra sesión / otra PC sin tener que reconstruir todo desde cero.

---

## Estado actual: dónde quedamos

### ✅ Completado y commiteado

- **UML de Entrega 3:** completo, en `uml-poo-tp.svg` (PNG viejo borrado).
- **Capa Dominio (`src/dominio/`)** — 6 archivos: `EstadoTurno`, `Persona` (abstract), `Paciente`, `Odontologo`, `Domicilio`, `Turno`.
- **Capa Excepciones (`src/excepcion/`)** — 8 archivos: `ClinicaException` (extends `RuntimeException`) + 7 hijas (`PacienteNoEncontradoException`, `OdontologoNoEncontradoException`, `TurnoYaReservadoException`, `TurnoNoEncontradoException`, `DatoInvalidoException`, `DniDuplicadoException`, `MatriculaDuplicadaException`).
- **Capa Repositorios (`src/repositorio/`)** — 4 archivos: `IRepositorio<T>` + `RepositorioPaciente` + `RepositorioOdontologo` + `RepositorioTurno`. Commit: `06e4ed7`.
- **Capa Servicios (`src/servicio/`)** — 3 archivos completos. Commit: `1898d7f`. Refactor para DI: ver más abajo.
  - `ServicioPaciente` — 6/6 métodos: `registrar`, `buscarPorId`, `buscarPorDni`, `modificar`, `eliminar`, `listarTodos`.
  - `ServicioOdontologo` — 6/6 métodos: ídem con matrícula y excepciones de odontólogo.
  - `ServicioTurno` — 10/10 métodos: `reservar`, `buscarPorId`, `confirmar`, `cancelar`, `modificar`, `eliminar` (agregado después), `listarTodos`, `listarPorPaciente`, `listarPorOdontologo`, `listarPorFecha`.
- **Capa Controladores (`src/controlador/`)** — 3 archivos completos. Commit: `bbf6af4`.
  - `ControladorPaciente` — 6 métodos, recibe `ServicioPaciente` por constructor. `registrar` arma `Domicilio` + `Paciente` desde Strings; `modificar` trae el paciente y pisa campos con setters (DNI no se modifica); `eliminarPorId` con try-catch devuelve boolean.
  - `ControladorOdontologo` — 6 métodos. Detalle: `modificar` SÍ permite cambiar matrícula (no como DNI).
  - `ControladorTurno` — 11 métodos. `reprogramar` y `cambiarEstado` siguen el patrón "buscar + setters + modificar".
- **Capa Vista (`src/vista/`)** — 4 archivos completos. Commit: `c5f209a`.
  - `VistaPaciente`, `VistaOdontologo`, `VistaTurno` — cada una con su menú (`mostrarMenu` público, resto de métodos privados). Usan `Scanner` compartido + `try-catch` sobre `ClinicaException` para mostrar errores amigables. `VistaTurno` además captura `DateTimeParseException` (parseo de fecha/hora) y `IllegalArgumentException` (parseo de enum).
  - `MenuPrincipal` — orquestador: crea repositorios → servicios → controladores → vistas y muestra el menú raíz.
- **Main definitivo (`src/Main.java`)** — `new MenuPrincipal().iniciar();`. El Main viejo (prueba del dominio) se borró.

### 🐛 Bug encontrado en test manual + refactor (23/05/2026)

Al correr la app por primera vez, **al asignar un turno daba "Paciente no encontrado"** aunque el paciente sí estaba creado.

Causa: cada servicio creaba su propia instancia de repositorio en el campo (`= new RepositorioPaciente()`). `ServicioPaciente` guardaba en su mapa, `ServicioTurno` buscaba en otro mapa distinto.

Fix: refactor a inyección por constructor. `MenuPrincipal` crea **una sola instancia de cada repositorio** y la pasa a los servicios. Cambios:

- `ServicioPaciente`, `ServicioOdontologo` ahora reciben su repo por constructor.
- `ServicioTurno` recibe los 3 repos por constructor.
- `MenuPrincipal` arma toda la cadena de dependencias: 3 repos → 3 servicios → 3 controladores → 3 vistas.

Esto era lo que decía la nota original del doc: "Composición vía atributo, inicializado en el campo... Más adelante se va a pasar por constructor / inyección. Por ahora simple." Llegamos a "más adelante".

### ✅ Estado: TP COMPLETO Y FUNCIONANDO

Probado end-to-end con un flujo realista (crear paciente → buscar por ID/DNI → listar → actualizar → crear odontólogo → asignar turno → cambiar estado). Todo OK.

### 🔜 Lo que queda

1. ✅ **Probado manualmente** — Martin lo corrió en IntelliJ y anduvo bien.
2. **Implementar reglas de negocio que faltan** (ver siguiente sección). NO hacerlo todavía — para mañana.
3. **Revisar conceptos Java que Claude usó sin avisar** (ver más abajo).
4. **Pushear** una vez que esté todo verificado.
5. **Entregar** martes 26/05/2026.

---

## 📋 Reglas de negocio a implementar (PENDIENTE — mañana)

Documento fuente: `C:\Users\ruizv\Downloads\Reglas de Negocio.md` (lo mandó el profe). Algunas ya están implementadas, faltan otras. Hay que revisar una por una y agregar lo que falte. **Esto se hace en la capa Servicio.**

### Paciente
- ⏳ El objeto paciente no puede ser nulo *(falta — agregar `if (paciente == null) throw new DatoInvalidoException("...")` al inicio de `registrar` y `modificar`)*
- ⏳ El nombre no puede estar vacío ni ser solo espacios *(falta — usar `.trim().isEmpty()`)*
- ⏳ El apellido no puede estar vacío ni ser solo espacios *(falta)*
- ⏳ El DNI debe contener únicamente dígitos y tener entre 7 y 8 caracteres *(falta — regex o `.matches("\\d{7,8}")`)*
- ⏳ El email debe contener @ y . *(falta — `.contains("@") && .contains(".")`)*
- ✅ No pueden existir dos pacientes con el mismo DNI *(ya implementado en `registrar`)*

### Odontólogo
- ⏳ El objeto odontólogo no puede ser nulo *(falta)*
- ⏳ El nombre no puede estar vacío ni ser solo espacios *(falta)*
- ⏳ El apellido no puede estar vacío ni ser solo espacios *(falta)*
- ⏳ La matrícula no puede estar vacía ni ser solo espacios *(falta — solo chequea null/empty, falta espacios)*
- ✅ No pueden existir dos odontólogos con la misma matrícula *(ya implementado en `registrar`)*
- ⏳ Al actualizar, la nueva matrícula no puede coincidir con la de otro odontólogo distinto *(falta — chequear que el dueño de esa matrícula sea el mismo id que estoy modificando)*

### Turno
- ✅ El id del paciente debe corresponder a un paciente registrado *(ya implementado en `reservar`)*
- ✅ El id del odontólogo debe corresponder a un odontólogo registrado *(ya implementado en `reservar`)*
- ⏳ La fecha y la hora no pueden ser nulas *(falta — chequear al inicio de `reservar`)*
- ⏳ La fecha del turno no puede ser anterior a la fecha actual *(falta — `if (fecha.isBefore(LocalDate.now())) throw ...`)*
- ⏳ Al reprogramar, la nueva fecha tampoco puede ser anterior a la fecha actual *(falta — agregar método `reprogramar` en `ServicioTurno` o validar en el controlador. **OJO:** ahora la lógica está en el Controlador, no en el Servicio. Discutir mañana si la validación va en Servicio o se baja desde el Controlador.)*
- ✅ Al cambiar estado, el id debe corresponder a un turno que exista *(ya implementado — `confirmar`/`cancelar` llaman a `buscarPorId`)*
- ✅ Al reprogramar, el id debe corresponder a un turno que exista *(ya implementado en el Controlador — `buscarPorId` antes de modificar)*

---

## 🤔 Conceptos Java que Claude usó sin avisar (revisar mañana)

Cuando Claude escribió la capa Vista, usó varias cosas que no vimos antes. Martin pidió que se las anote para repasarlas:

### `LocalDate.parse("2026-12-25")` y `LocalTime.parse("14:30")`
Método estático que **convierte un String a un objeto** `LocalDate` / `LocalTime`. La Vista lee un String del teclado (`scanner.nextLine()`) y necesita convertirlo a fecha/hora para pasárselo al controlador. Si el formato es incorrecto, lanza `DateTimeParseException`.

```java
String fechaStr = scanner.nextLine();      // "2026-12-25"
LocalDate fecha = LocalDate.parse(fechaStr); // objeto LocalDate
```

### `EstadoTurno.valueOf("CONFIRMADO")`
Método estático que viene "gratis" con todos los `enum`. **Convierte un String al valor del enum** que coincida exactamente con el nombre. Si el String no coincide con ninguna constante del enum, lanza `IllegalArgumentException`. Por eso en `cambiarEstado` se usa `estadoStr.toUpperCase()` antes — para que el usuario pueda escribir "confirmado" en minúscula.

```java
EstadoTurno e = EstadoTurno.valueOf("CONFIRMADO");  // → EstadoTurno.CONFIRMADO
```

### Múltiples `catch` en un mismo `try`
Hasta ahora veníamos con un solo `catch` por `try`. Pero un mismo `try` puede tener varios `catch` para distintos tipos de excepción:
```java
try {
    // código
} catch (ClinicaException e) {
    // errores del dominio (DNI duplicado, etc.)
} catch (DateTimeParseException e) {
    // formato de fecha inválido
}
```
Java va probando los catch en orden y entra al primero que coincida con el tipo de la excepción.

### `.toUpperCase()` y `.trim()` en String
Métodos comunes de `String` que vamos a necesitar para las reglas de negocio:
- `"abc".toUpperCase()` → `"ABC"`
- `"  hola  ".trim()` → `"hola"` (saca espacios al principio y al final). **Importante para las reglas: "no puede ser solo espacios" se chequea con `.trim().isEmpty()`.**

### `.isBefore(...)` en `LocalDate`
Para comparar fechas. Lo vamos a usar en la regla "no se pueden asignar turnos en el pasado":
```java
if (fecha.isBefore(LocalDate.now())) {
    throw new DatoInvalidoException("La fecha no puede ser anterior a hoy");
}
```

---

## Estructura actual del código

```
src/
├── Main.java                          ← prueba del Dominio (descartable)
├── dominio/                           ← ✅ commiteado
│   ├── EstadoTurno.java
│   ├── Persona.java                   ← abstract
│   ├── Paciente.java
│   ├── Odontologo.java
│   ├── Domicilio.java
│   └── Turno.java
├── excepcion/                         ← ✅ commiteado
│   ├── ClinicaException.java          ← extends RuntimeException
│   └── 7 excepciones hijas
├── repositorio/                       ← ✅ commiteado (06e4ed7)
│   ├── IRepositorio.java              ← interfaz genérica <T>
│   ├── RepositorioPaciente.java       ← + buscarPorDni
│   ├── RepositorioOdontologo.java     ← + buscarPorMatricula
│   └── RepositorioTurno.java          ← + buscarPorPaciente / buscarPorOdontologo / buscarPorFecha
└── servicio/                          ← 🛠 EN PROGRESO
    └── ServicioPaciente.java          ← 3/6 métodos (registrar, buscarPorId, buscarPorDni)
```

---

## Métodos del UML — `ServicioPaciente`

(Confirmado por screenshot que Martin pasó del UML — la consulta por grep al SVG dio resultados parciales, ver nota más abajo.)

```
+ registrar(paciente: Paciente): Paciente             ← ✅ hecho
+ buscarPorId(id: Long): Paciente                     ← ✅ hecho
+ buscarPorDni(dni: String): Paciente                 ← ✅ hecho
+ listarTodos(): List<Paciente>                       ← ⏳ pendiente
+ modificar(paciente: Paciente): Paciente             ← ⏳ pendiente
+ eliminar(id: Long): void                            ← ⏳ pendiente
```

**Nombres importantes a respetar (NO inventar):** `modificar` (no "actualizar"), `listarTodos` (no "listar").

---

## Decisiones de diseño tomadas

### Convenciones de paquetes
- Paquetes en **minúscula y singular**, directo bajo `src/`.

### Constructores y el `id`
- Los constructores NO reciben `id`. El **repositorio** asigna el id con `setId()` al persistir.

### Defaults en entidades (Opción B)
- `Turno.estado` = `EstadoTurno.PENDIENTE` siempre al crear.
- `Paciente.fechaIngreso = LocalDate.now()` inicializado en el campo.

### Excepciones
- Todas extienden `RuntimeException` (unchecked). Suben Servicio → Controlador → Vista.

### Repositorio "tonto"
- NO chequea existencia ni lanza excepciones de negocio. `actualizar` con id inexistente lo agrega (`Map.put`); `eliminar` con id inexistente no hace nada; `buscarPor*` devuelven `null` si no encuentran. Es el **Servicio** el que chequea y lanza excepciones.
- `guardar()` devuelve `T` (no `void`) porque el llamador necesita el id asignado.
- `++contadorId` PREFIJO para que los ids arranquen en 1L (no 0L).
- `buscarTodos()` devuelve copia: `new ArrayList<>(mapa.values())`.

### Decisiones de la capa Servicios (consolidadas 22/05 noche)

- **Composición vía atributo**, inicializado en el campo:
  ```java
  private RepositorioPaciente repositorio = new RepositorioPaciente();
  ```
  Más adelante (Controlador) se va a pasar por constructor / inyección. Por ahora simple.
- **El Servicio endurece el contrato del Repo:** si el repo devuelve `null` en `buscarPorId`/`buscarPorDni`, el servicio lanza excepción. El llamador ya no tiene que chequear null.
- **Patrón "validar → chequear unicidad → delegar"** en los `registrar`. Cada paso con su propio `if` + `throw`.
- **Patrón "early throw" / guard clauses:** sin `else`, ifs planos. Cuando un `if` lanza excepción, el flujo se corta solo — no hace falta `else`.
- **Validación de entrada:**
  - `String` → siempre chequear `== null || .isEmpty()` (cortocircuito en este orden).
  - `Long` → sólo `== null` (los números no están "vacíos").
- **Mensajes de excepción:** describen el problema técnico (`"El DNI no puede estar vacío"`), NO son mensajes de UI. La Vista los traduce al usuario.
- **Reusar el resultado del repo:** si ya pediste algo y lo guardaste en variable, no vuelvas a pedirlo. (Hoy es gratis con `HashMap`, pero el día de DB/archivo cada viaje cuesta.)
- **Nombres de variables:** `existe` o `pacienteEncontrado` mejor que `tieneId` (que parece boolean).

### Pendientes técnicos del diseño
- **Persistencia:** PENDIENTE (CSV o serialización, depende de Clase 14).
- **Comparable/Comparator:** PENDIENTE. Si hace falta ordenar antes de Clase 12, usar `Collections.sort` con lambda en el servicio.
- **Sin `Optional<T>`:** los repos devuelven `T` o `null`.

### Conceptos Java vistos por Martin en esta sesión (Servicios)
- **Composición** (Servicio TIENE un Repositorio como atributo).
- **Lanzar excepciones:** `throw new XxxException("mensaje")`. Como son `RuntimeException`, no hace falta `throws` en la firma.
- **Cortocircuito de `||`:** el null check va PRIMERO; si la primera condición da `true`, la segunda no se evalúa → evita NPE.
- **NullPointerException:** llamar un método sobre `null` explota.
- **`isEmpty()` es de `String`, NO de `Long`:** los números no se chequean con `isEmpty`.
- **Guard clauses / early throw:** preferir varios `if`+`throw` planos en vez de `if/else if` anidados.
- **Endurecer contrato:** un servicio puede prometer más fuerte que su repo (devolver siempre algo válido o explotar).

### Estilo de comentarios de Martin
Mientras aprende, escribe comentarios "para él" en líneas que le costaron. Está bien por ahora. **OJO con dejar comentarios desfasados** cuando se cambia la lógica (ya pasó una vez: comentario decía "si la variable está vacía" después de que la condición cambió a `!= null`). Cuando cambies un `if`, releé el comentario.

---

## Próximo paso concreto: terminar `ServicioPaciente`

Orden sugerido para mañana:

### 1. `modificar(Paciente paciente): Paciente`
- Reusar `buscarPorId(paciente.getId())` (¡aprovechar que ya existe en el servicio!) → si el paciente no existe, ese método ya lanza `PacienteNoEncontradoException` solo.
- Delegar a `repositorio.actualizar(paciente)` y devolver lo que devuelve.
- Concepto nuevo: **un método del servicio reusando otro método del mismo servicio**.

### 2. `eliminar(Long id): void`
- Mismo patrón que `modificar`: chequear existencia con `buscarPorId(id)`, después `repositorio.eliminar(id)`.
- Concepto nuevo: método con retorno `void`.

### 3. `listarTodos(): List<Paciente>`
- Una sola línea: `return repositorio.buscarTodos();`
- Sin validación, sin chequeo. Es delegación pura. (Si la lista está vacía, devolvés lista vacía — no excepción.)
- Concepto nuevo: cuándo NO hace falta lógica en el servicio (cuando no hay reglas que aplicar).

### Después de terminar `ServicioPaciente`
- Empezar `ServicioOdontologo` (analogía directa: mismo patrón con DNI → matrícula, y excepciones equivalentes).

---

## Pendientes administrativos

- **Entrega 3: martes 26/05/2026.** Confirmado por Martin.
- Cronograma oficial decía Clase 11 = 26/05 y la consigna detallada Clase 14 = 16/06. Confirmar fecha real con el profe el día martes.
- **Viaje fin de semana 23-24/05:** Martin se lleva la notebook para seguir.

---

## Estilo de trabajo a respetar

- **Martin pidió ser guiado paso a paso, NO recibir código terminado.**
- Explicar conceptos **antes** de tocar código.
- Proponer 1 paso a la vez y esperar a que él lo implemente.
- Revisar el código que él escriba; explicar mejoras, no reescribirlo entero.
- **Excepción:** andamiajes mecánicos (imports, getters/setters obvios) sí pueden mostrarse enteros si los pide.
- Martin no siempre pega el código que escribió — abrir los archivos en `src/` con `Read` para verificar el estado real antes de cualquier consejo o commit.
- Cuando avisa "ya hice X, sigamos", siempre revisar primero.

---

## Nota técnica: leer el UML

**Claude puede ver imágenes PNG/JPG con visión (multimodal), pero NO renderiza SVG.** El `uml-poo-tp.svg` se lee como XML (texto), y cuando se hace `grep` los métodos largos quedan fragmentados en varios `<text>` tags. Por eso una consulta automática puede "ver mal" algunos métodos (ej: detectar `listar()` cuando en realidad es `listarTodos(): List<Paciente>`).

**Solución práctica:** cuando haga falta confirmar un método puntual del UML, Martin pasa un screenshot PNG del UML — eso Claude lo lee bien con visión. Funcionó perfecto con el screenshot de `ServicioPaciente`.

---

## Archivos clave del proyecto

- `docs/arquitectura-en-capas.md` — apuntes completos sobre las 6 capas (para defender el TP).
- `docs/proximos-pasos.md` — este archivo.
- `uml-poo-tp.svg` — UML completo en SVG (legible al hacer zoom). Para Claude: pedirle screenshot a Martin si hace falta confirmar un método.