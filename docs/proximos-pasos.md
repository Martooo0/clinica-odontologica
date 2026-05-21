# Estado del TP — Clínica Odontológica

> Última actualización: **21/05/2026** (final de sesión).
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

### ⏳ Próximo paso: Capa Repositorios

Ver sección **"Próximo paso concreto"** más abajo.

### 🔜 Después: Servicios → Controladores → Vistas + Main definitivo

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
└── excepcion/
    ├── ClinicaException.java  ← extends RuntimeException
    ├── PacienteNoEncontradoException.java
    ├── OdontologoNoEncontradoException.java
    ├── TurnoYaReservadoException.java
    ├── TurnoNoEncontradoException.java
    ├── DatoInvalidoException.java
    ├── DniDuplicadoException.java
    └── MatriculaDuplicadaException.java
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

---

## Próximo paso concreto: Capa Repositorios

### Lo que hay que crear

```
src/repositorio/
├── IRepositorio.java               ← interfaz genérica
├── RepositorioPaciente.java        ← implements IRepositorio<Paciente>
├── RepositorioOdontologo.java      ← implements IRepositorio<Odontologo>
└── RepositorioTurno.java           ← implements IRepositorio<Turno>
```

### 3 conceptos nuevos a aprender (juntos)

1. **Genéricos en Java** (`<T>`): cómo escribir código que sirva para cualquier tipo.
2. **Interfaces** (`interface`): definir un contrato sin implementarlo.
3. **`HashMap<K,V>`**: estructura de datos clave-valor para guardar entidades por id.

### Métodos de la interfaz `IRepositorio<T>` (del UML)

```
+ guardar(entidad: T): T
+ buscarPorId(id: Long): T
+ buscarTodos(): List<T>
+ actualizar(entidad: T): T
+ eliminar(id: Long): void
```

### Métodos extra de cada implementación (del UML)

- `RepositorioPaciente`: `buscarPorDni(dni: String): Paciente`
- `RepositorioOdontologo`: `buscarPorMatricula(matricula: String): Odontologo`
- `RepositorioTurno`: `buscarPorPaciente(idPaciente: Long): List<Turno>`, `buscarPorOdontologo(idOdontologo: Long): List<Turno>`, `buscarPorFecha(fecha: LocalDate): List<Turno>`

### Atributos internos de cada implementación

- Un `Map<Long, T>` (HashMap) para guardar las entidades por id.
- Un `Long contadorId` que arranca en 0 y se incrementa con cada `guardar()`.
- En `guardar()`: `entidad.setId(++contadorId); mapa.put(entidad.getId(), entidad);`.

### Orden sugerido (para no abrumarse)

1. Interfaz `IRepositorio<T>` sola (~15-20 min, concepto de genéricos).
2. `RepositorioPaciente` con detalle (HashMap + contadorId + buscarPorDni).
3. `RepositorioOdontologo` y `RepositorioTurno` por analogía (más rápido).

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
