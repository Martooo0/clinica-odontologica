# Estado del TP — Clínica Odontológica

> Última actualización: **22/05/2026 (noche)** — cierre de sesión antes de viaje.
>
> **Entrega: martes 26/05/2026.** Martin se va de viaje el fin de semana con la notebook para seguir trabajando.
>
> Documento de contexto pensado para retomar el TP en otra sesión / otra PC sin tener que reconstruir todo desde cero.

---

## Estado actual: dónde quedamos

### ✅ Completado y commiteado

- **UML de Entrega 3:** completo, en `uml-poo-tp.svg` (PNG viejo borrado).
- **Capa Dominio (`src/dominio/`)** — 6 archivos: `EstadoTurno`, `Persona` (abstract), `Paciente`, `Odontologo`, `Domicilio`, `Turno`.
- **Capa Excepciones (`src/excepcion/`)** — 8 archivos: `ClinicaException` (extends `RuntimeException`) + 7 hijas (`PacienteNoEncontradoException`, `OdontologoNoEncontradoException`, `TurnoYaReservadoException`, `TurnoNoEncontradoException`, `DatoInvalidoException`, `DniDuplicadoException`, `MatriculaDuplicadaException`).
- **Capa Repositorios (`src/repositorio/`)** — 4 archivos: `IRepositorio<T>` + `RepositorioPaciente` + `RepositorioOdontologo` + `RepositorioTurno`. Commit: `06e4ed7`.
- **Main de prueba (`src/Main.java`)** — valida que Dominio compila e instancia. Es descartable.

### 🛠 En esta sesión (22/05/2026 noche) — Capa Servicios EN PROGRESO, SIN commitear todavía

**`src/servicio/ServicioPaciente.java`** — 3 de 6 métodos hechos:

- ✅ `registrar(Paciente paciente): Paciente` — valida DNI (no null/vacío), chequea duplicado por DNI, delega al repo.
- ✅ `buscarPorId(Long id): Paciente` — pide al repo, si null lanza `PacienteNoEncontradoException`.
- ✅ `buscarPorDni(String dni): Paciente` — valida DNI de entrada, pide al repo, si null lanza `PacienteNoEncontradoException`.

**Faltan en `ServicioPaciente` (en este orden propuesto para mañana):**

- ⏳ `modificar(Paciente paciente): Paciente` — chequear que exista, delegar al `repo.actualizar()`.
- ⏳ `eliminar(Long id): void` — chequear que exista, delegar al `repo.eliminar()`.
- ⏳ `listarTodos(): List<Paciente>` — delegar directo a `repo.buscarTodos()`.

### 🔜 Después de cerrar `ServicioPaciente`

1. `ServicioOdontologo` — por analogía (más rápido, mismo patrón con `MatriculaDuplicadaException`/`OdontologoNoEncontradoException`).
2. `ServicioTurno` — el más interesante, combina 3 repos. Regla clave: "no hay 2 turnos al mismo odontólogo a la misma hora" → `TurnoYaReservadoException`.
3. **Controladores** → **Vistas** → **Main definitivo**.

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

---

## Para retomar mañana

Copy-paste a Claude:

> *"Hola, retomemos el TP de Clínica Odontológica. Leé `docs/proximos-pasos.md` para ponerte al día, revisá el estado actual de `src/servicio/ServicioPaciente.java` y arrancamos con el método `modificar` siguiendo el orden de mañana propuesto (modificar → eliminar → listarTodos). Recordá: guía paso a paso, no escribas el código por mí. Cuando necesites mirar algún método puntual del UML, pedime un screenshot."*
