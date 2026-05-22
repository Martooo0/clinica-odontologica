# Estado del TP — Clínica Odontológica

> Última actualización: **22/05/2026** (en curso de sesión — cambios de repositorio aún SIN commitear).
>
> Documento de contexto pensado para retomar el TP en otra sesión / otra PC sin tener que reconstruir todo desde cero.

---

## Estado actual: dónde quedamos

### ✅ Completado y commiteado

- **UML de Entrega 3:** completo, exportado a SVG (`uml-poo-tp.svg`) para que sea legible al hacer zoom (el PNG perdía detalle).
- **Capa Dominio (`src/dominio/`)** — 6 archivos:
  - `EstadoTurno.java` (enum: PENDIENTE, CONFIRMADO, COMPLETADO, CANCELADO)
  - `Persona.java` (abstract, atributos `protected`)
  - `Paciente.java` extends Persona
  - `Odontologo.java` extends Persona
  - `Domicilio.java`
  - `Turno.java`
- **Capa Excepciones (`src/excepcion/`)** — 8 archivos:
  - `ClinicaException.java` (extends `RuntimeException`)
  - 7 hijas: `PacienteNoEncontradoException`, `OdontologoNoEncontradoException`, `TurnoYaReservadoException`, `TurnoNoEncontradoException`, `DatoInvalidoException`, `DniDuplicadoException`, `MatriculaDuplicadaException`.
- **Main de prueba (`src/Main.java`)** — valida que el Dominio compila e instancia bien, y que `Turno.esFuturo()` funciona.

### 🛠 En esta sesión (22/05/2026) — Capa Repositorios COMPLETA, SIN commitear todavía

- **Capa Repositorios (`src/repositorio/`)** — los 4 archivos terminados:
  - `IRepositorio.java` — interfaz genérica `<T>` con los 5 métodos del UML (`guardar`, `buscarPorId`, `buscarTodos`, `actualizar`, `eliminar`).
  - `RepositorioPaciente.java` — implementación completa: `Map<Long, Paciente>` + `contadorId` (arranca en 0L, se incrementa con `++contadorId` PREFIJO para que los ids arranquen en 1L). Incluye método extra `buscarPorDni(String dni)` con for-each + `.equals()`.
  - `RepositorioOdontologo.java` — análogo a Paciente. Método extra: `buscarPorMatricula(String matricula)`.
  - `RepositorioTurno.java` — análogo en los 5 del contrato. 3 métodos extra (`buscarPorPaciente`, `buscarPorOdontologo`, `buscarPorFecha`) que devuelven `List<Turno>` usando el patrón "acumular en lista".

### ⏳ Próximo paso: Capa Servicios

Ver sección **"Próximo paso concreto"** más abajo.

### 🔜 Después: Controladores → Vistas + Main definitivo

---

## Estructura actual del código

```
src/
├── Main.java                  ← prueba del Dominio (descartable)
├── dominio/
│   ├── EstadoTurno.java
│   ├── Persona.java           ← abstract
│   ├── Paciente.java          ← extends Persona
│   ├── Odontologo.java        ← extends Persona
│   ├── Domicilio.java
│   └── Turno.java
├── excepcion/
│   ├── ClinicaException.java  ← extends RuntimeException
│   ├── PacienteNoEncontradoException.java
│   ├── OdontologoNoEncontradoException.java
│   ├── TurnoYaReservadoException.java
│   ├── TurnoNoEncontradoException.java
│   ├── DatoInvalidoException.java
│   ├── DniDuplicadoException.java
│   └── MatriculaDuplicadaException.java
└── repositorio/                       ← ✅ COMPLETA (sin commit aún)
    ├── IRepositorio.java              ← interfaz genérica <T>
    ├── RepositorioPaciente.java       ← + buscarPorDni
    ├── RepositorioOdontologo.java     ← + buscarPorMatricula
    └── RepositorioTurno.java          ← + buscarPorPaciente / buscarPorOdontologo / buscarPorFecha
```

---

## Decisiones de diseño tomadas

### Convenciones de paquetes
- Paquetes en **minúscula y singular**, directo bajo `src/` (sin paquete base tipo `clinica` ni dominio invertido `com.uade....`).
- Las clases en `PascalCase`, los paquetes en `minusculas` (regla idiomática de Java).

### Constructores y el `id`
- **Los constructores NO reciben `id`.** El id queda en `null` al crear la entidad.
- El **repositorio** asigna el id cuando se persiste (con `setId()`).
- Cada entidad tiene 2 constructores: vacío + "completo" (sin id).

### Defaults en entidades del Dominio (Opción B)
- `Turno.estado` nace siempre en `EstadoTurno.PENDIENTE` (no se recibe por constructor).
- `Paciente.fechaIngreso = LocalDate.now()` (inicializado directamente en el campo, no se recibe por constructor).
- Si se necesita un valor distinto (ej: cargar paciente histórico desde archivo en Entrega 4), se usa el setter después de crear.

### Herencia y `toString()`
- `Persona` es `abstract`, atributos `protected` (lo pide el cronograma — Clase 6).
- Las subclases hacen `super.toString() + " - DNI: ..."` para evitar repetir lógica.
- Siempre `@Override` cuando se hereda un método (`toString()`, etc.).

### Excepciones
- Todas extienden `RuntimeException` (unchecked). Razón: errores de negocio se dejan **subir** Servicio → Controlador → Vista, donde se atrapan. Si fueran checked sería ruido de `throws` por todos lados.
- Las 7 hijas no agregan nada — solo cambia el nombre del tipo (eso ya comunica el problema).

### Decisiones del profe / consigna (de UML)
- Capa Controller como adaptador entre Vista y Servicio (NO es Spring MVC; es MVC adaptado a consola).
- Turno↔Paciente y Turno↔Odontologo es **agregación** (rombo vacío), no asociación. Así lo enseña el profe.

### Pendientes técnicos del diseño
- **Persistencia:** PENDIENTE. Decidir CSV o serialización Java cuando el profe explique el tema (Clase 14).
- **Comparable/Comparator:** PENDIENTE. Si se necesita ordenar antes de que se vea (Clase 12), usar `Collections.sort` con lambda en el servicio (sin implementar Comparable).
- **Sin `Optional<T>`:** los `buscarPorId` devuelven `T` directamente (`null` si no existe). El Servicio decide si lanzar excepción.

### Decisiones del Repositorio consolidadas en la sesión del 22/05/2026
- **Repo "tonto":** el repositorio NO chequea existencia ni lanza excepciones de negocio. `actualizar(entidad)` con id inexistente simplemente lo agrega (es `Map.put`); `eliminar(id)` con id inexistente no hace nada (es `Map.remove`); `buscarPorId` y `buscarPorDni`/`buscarPorMatricula` devuelven `null` si no encuentran. Es el **Servicio** quien chequea con `buscarPorId` antes y lanza `PacienteNoEncontradoException` / etc. Mantiene la responsabilidad de validación en un solo lugar y al repo sin lógica de negocio.
- **`guardar()` devuelve `T`** (no `void`) porque después de asignar el id, el llamador necesita la entidad de vuelta para conocer el id que le tocó.
- **`++contadorId` (prefijo), no `contadorId++` (postfijo):** los ids tienen que arrancar en 1L, no en 0L. Con postfijo el primer paciente quedaría con id 0.
- **`buscarTodos()` devuelve una COPIA:** `new ArrayList<>(mapa.values())`. Dos razones: (1) la firma exige `List`, no `Collection`; (2) encapsulación — si devolvieras `values()` directo, modificar la lista afectaría el Map interno.
- **Métodos extra fuera del contrato NO llevan `@Override`** (no están sobrescribiendo nada). Solo los 5 de la interfaz lo llevan.

### Conceptos Java vistos por Martin en esta sesión
- `interface` vs `class`; `implements` lo usan solo las clases.
- Genéricos `<T>` — declarar interfaz genérica e instanciarla con `IRepositorio<Paciente>`.
- `Map<K,V>` vs `HashMap<K,V>`: declarar con la interfaz, instanciar con la implementación.
- Métodos de `Map`: `put`, `get`, `remove`, `containsKey`, `values()`.
- `++` prefijo vs postfijo.
- `Collection<T>` vs `List<T>` — supertipos, copia con `new ArrayList<>(...)`.
- `==` vs `.equals()` — para Strings, para `Long` (objetos wrapper) y para cualquier objeto.
- `for-each` sobre `mapa.values()`.
- Diamante `<>` en `new HashMap<>()` (Java infiere los tipos).
- Patrón "buscar uno y cortar" (`return p` dentro del if) vs "acumular en lista" (`resultado.add(t)` sin cortar) — el segundo lo vamos a ver al hacer `RepositorioTurno`.
- Convención: una colección sin resultados se devuelve vacía, no `null`.

### Estilo de comentarios que Martin viene usando
Mientras aprende, escribe comentarios "para él" en las líneas que le costaron (ej: explicar prefijo vs postfijo, explicar la decisión del repo tonto en `actualizar`). Está bien por ahora. En revisión final se podrán borrar los que repiten el nombre del método o del parámetro; los que explican un **why** no obvio (como el comentario del `++contadorId`) se quedan.

---

## Próximo paso concreto: Capa Servicios

### Lo que hay que crear

```
src/servicio/
├── ServicioPaciente.java
├── ServicioOdontologo.java
└── ServicioTurno.java
```

(El UML muestra que NO hay una interfaz genérica `IServicio<T>` — cada servicio tiene su propia API, con reglas de negocio distintas.)

### Rol del Servicio (vs Repositorio)

El **Repositorio** es tonto: solo guarda, busca, actualiza, elimina. El **Servicio** es donde vive la **lógica de negocio**:

- Validar datos antes de guardar (DNI no vacío, fecha no pasada, etc.).
- Chequear unicidad y lanzar excepciones (`DniDuplicadoException`, `MatriculaDuplicadaException`, `TurnoYaReservadoException`).
- Chequear existencia y lanzar excepciones (`PacienteNoEncontradoException`, etc.) en operaciones que requieren que la entidad ya exista (actualizar, eliminar, buscar).
- Combinar varios repositorios cuando una operación toca más de una entidad (ej: dar de alta un Turno necesita validar que el Paciente y el Odontologo existan).

### Atributos típicos de un servicio

```java
public class ServicioPaciente {
    private RepositorioPaciente repositorio = new RepositorioPaciente();
    // ...
}
```

Después en el Controlador, en lugar de instanciar el repo, se va a inyectar (o pasarse por constructor). Eso lo vemos cuando llegue.

### Métodos típicos (del UML)

Cada servicio espeja los 5 del repo pero con validaciones y excepciones encima. Ejemplo `ServicioPaciente`:

- `registrar(Paciente p): Paciente` → valida + chequea DNI único + delega `repo.guardar(p)`.
- `buscarPorId(Long id): Paciente` → si `repo.buscarPorId(id) == null`, lanza `PacienteNoEncontradoException`.
- `actualizar(Paciente p): Paciente` → chequea que exista, luego `repo.actualizar(p)`.
- `eliminar(Long id): void` → chequea que exista, luego `repo.eliminar(id)`.
- `listar(): List<Paciente>` → delega a `repo.buscarTodos()`.

(Confirmar nombres exactos contra el UML antes de tipear.)

### Conceptos nuevos a aprender en esta capa

1. **Composición:** un Servicio TIENE un Repositorio (lo guarda como atributo).
2. **Validaciones y lanzamiento de excepciones:** usar las 8 clases de `excepcion/` que ya existen.
3. **Orquestación de múltiples repos:** `ServicioTurno` necesita `RepositorioTurno` + `RepositorioPaciente` + `RepositorioOdontologo`.

### Orden sugerido

1. `ServicioPaciente` con detalle (introducir composición + primeras validaciones + uso de excepciones).
2. `ServicioOdontologo` por analogía (más rápido).
3. `ServicioTurno` al final — el más interesante porque combina 3 repos y tiene la regla "no hay 2 turnos al mismo odontólogo a la misma hora" (`TurnoYaReservadoException`).

---

## Pendientes administrativos

- **Confirmar fecha real de Entrega 3 con el profe.** Sigue la inconsistencia:
  - Cronograma oficial: Clase 11 = 26/05/2026
  - Consigna detallada: Clase 14 = 16/06/2026
  - Esto cambia si llegamos a ver Comparable (Clase 12, 02/06) y Serialización (Clase 14, 16/06) antes de entregar.

---

## Estilo de trabajo a respetar

- **Martin pidió ser guiado paso a paso, NO recibir código terminado.**
- Explicar conceptos **antes** de tocar código.
- Proponer 1 paso a la vez y esperar a que él lo implemente.
- Revisar el código que él escriba; explicar mejoras, no reescribirlo entero.
- **Excepción:** andamiajes mecánicos (imports, getters/setters obvios, toString con muchos campos) sí pueden mostrarse enteros si él los pide.
- Martin no siempre pega el código que escribió — abrir los archivos en `src/` con la herramienta Read para verificar el estado real.
- Cuando él avisa "ya hice X, sigamos", siempre revisar primero antes de avanzar.

---

## Archivos clave del proyecto

- `docs/arquitectura-en-capas.md` — apuntes completos sobre las 6 capas (sirve para defender el TP).
- `docs/proximos-pasos.md` — este archivo (estado y próximo paso).
- `uml-poo-tp.svg` — UML completo en SVG (legible al hacer zoom). **Usar esta versión.**
- `uml-poo-tp.excalidraw.png` — versión PNG anterior (más borrosa, evitar).

---

## Para retomar en la próxima sesión / otra PC

Decile a Claude (copy-paste):

> *"Hola, retomemos el TP de Clínica Odontológica. Leé `docs/proximos-pasos.md` para ponerte al día, revisá rápido el estado del código en `src/` y arrancamos por la **Capa Repositorios**, empezando por la interfaz `IRepositorio<T>`."*

Claude va a leer este archivo, mirar los archivos `.java` en `src/` para confirmar el estado real, y arrancar con la interfaz `IRepositorio<T>` paso a paso.
