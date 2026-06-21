# Arquitectura en Capas — Clínica Odontológica "Sonrisa Feliz"

Apuntes propios sobre la arquitectura en capas del sistema. Pensado para entender el "por qué" de cada parte, no solo el "qué".

---

## La idea general: ¿por qué dividir en capas?

Imaginate la clínica real. Si **una sola persona** fuera la recepcionista, la administradora, la encargada de la base de datos, la contadora y la enfermera, todo se mezclaría: cuando reciba un paciente nuevo, no sabría si está haciendo el alta administrativa, validando el seguro o registrándolo en la historia clínica. Errores garantizados.

La solución del mundo real: **dividir responsabilidades**. La recepcionista atiende, la administradora valida, la base de datos guarda. Cada una tiene un rol claro y se comunican entre sí siguiendo un protocolo.

En software pasa lo mismo. **Cada capa tiene UNA responsabilidad clara** y se comunica solo con la capa de al lado. Esto se llama **arquitectura en capas** y es la idea más importante del proyecto.

Las capas, de abajo hacia arriba:

1. **dominio** — los objetos del negocio
2. **Repositorios** — persistencia
3. **Servicios** — reglas de negocio
4. **Excepciones** — errores de dominio
5. **Controladores** — adaptadores entre presentación y lógica
6. **Vista** — interacción con el usuario

---

## Capa 1 — dominio (modelo)

### ¿Qué es?
Las **entidades del negocio**: los conceptos del mundo real que el sistema maneja. En este TP son `Paciente`, `Odontologo`, `Domicilio`, `Turno` y `EstadoTurno`.

### ¿De qué se encarga?
- **Representar los datos.** Un Paciente tiene nombre, dni, fechaIngreso, etc.
- **Comportamiento propio del concepto.** Ejemplo: `Turno.esFuturo()` — el propio turno sabe si está en el futuro, no se lo hay que preguntar a nadie más.

### ¿De qué NO se encarga?
- **NO sabe cómo se guarda.** Un Paciente no tiene idea si está en un HashMap, en un archivo o en una base de datos.
- **NO sabe quién lo llama.** No sabe si lo está usando la consola, una GUI o un test.
- **NO tiene validaciones de negocio complejas.** "El DNI no puede repetirse" no es responsabilidad del Paciente — es del Servicio.

### Analogía
Es como una **ficha clínica de papel**. Solo es información. La ficha no se guarda sola, no se busca sola, no se valida sola.

---

## Capa 2 — Repositorios (persistencia)

### ¿Qué es?
La capa que **guarda y recupera** las entidades. Es el "depósito" de los datos.

### ¿De qué se encarga?
- **Guardar** una entidad (en memoria por ahora, después en archivo).
- **Buscar** una entidad por id, por dni, por matrícula, etc.
- **Listar** todas las entidades.
- **Actualizar** una entidad existente.
- **Eliminar** una entidad por id.

### ¿De qué NO se encarga?
- **NO valida reglas de negocio.** Si le piden guardar un paciente con DNI duplicado, lo guarda. Validar es responsabilidad del Servicio.
- **NO lanza errores de negocio.** Si no encuentra el paciente, devuelve `null`. La excepción `PacienteNoEncontradoException` la dispara el Servicio.
- **NO interactúa con el usuario.** No hay Scanner, no hay System.out.

### Analogía
Es el **archivero** de la clínica. Si la administradora le pide "guardame esta ficha" o "buscame la ficha del paciente 1234", la guarda o la busca, sin opinar. Si el cajón está vacío y le piden una ficha, dice "no la tengo" — pero no decide qué hacer al respecto.

---

## Capa 3 — Servicios (lógica de negocio)

### ¿Qué es?
La capa que **orquesta las operaciones del sistema** aplicando las reglas del negocio. Es el "cerebro" de la aplicación.

### ¿De qué se encarga?
- **Coordinar** repositorios para llevar a cabo una operación.
- **Validar reglas de negocio.** Ejemplo: antes de reservar un turno, verificar que el odontólogo no tenga otro turno en esa fecha/hora.
- **Lanzar excepciones de dominio.** Si algo no cumple las reglas, lanza una excepción específica (`DniDuplicadoException`, `TurnoYaReservadoException`, etc.).
- **Trabajar con objetos del dominio** (Paciente, Turno, Odontologo) — NO con primitivos sueltos del usuario.

### ¿De qué NO se encarga?
- **NO sabe cómo se guardan los datos.** Le pide al repositorio que guarde, sin preocuparse del cómo.
- **NO interactúa con el usuario directamente.** No tiene Scanner ni System.out.
- **NO arma objetos del dominio a partir de strings y primitivos.** Eso es trabajo del Controlador.

### Analogía
Es la **administradora de la clínica**. Cuando le presentan una ficha lista, ella decide: *"Primero chequeo si ya está registrado (le pregunta al archivero). Si no está, le hago el alta. Si el DNI ya existía, le digo 'paciente duplicado'."* Ella **piensa**, el archivero **solo guarda**. Pero ella **NO** se ocupa de pasarle el formulario al cliente ni de leer su letra: eso lo hace la recepcionista (vista) y la encargada de cargar el formulario al sistema (controlador).

---

## Capa 4 — Excepciones (dominio)

### ¿Qué es?
Una **jerarquía de errores específicos del negocio**. Cada situación de error tiene su propio tipo de excepción.

### ¿De qué se encarga?
- **Comunicar errores de forma estructurada.** En vez de devolver `null` o `false` o un código mágico (`-1 = no encontrado`), lanza una excepción con nombre claro.
- **Permitir manejar cada error de forma distinta.** El código que la usa puede decidir cómo reaccionar según el tipo.

### ¿De qué NO se encarga?
- **NO son errores técnicos** como `NullPointerException` o `IOException`. Son **errores de negocio**: "DNI duplicado", "Turno ya reservado", "Paciente no encontrado".

### Analogía
Son **avisos formales escritos** de la clínica. *"Estimado: el paciente que busca no existe en nuestros registros"* es muy distinto de *"Estimado: ese odontólogo ya tiene un turno a esa hora"*. Cada problema tiene su nombre, su tono, su explicación. No son gritos genéricos de "¡error!".

---

## Capa 5 — Controladores (adaptador)

### ¿Qué es?
La capa **intermedia** entre la Vista y los Servicios. Recibe datos crudos del usuario (Strings, Longs, primitivos) y los **traduce** en operaciones sobre objetos del dominio.

### ¿De qué se encarga?
- **Recibir parámetros primitivos** desde la Vista (`registrar(nombre: String, apellido: String, dni: String, ...)`).
- **Armar los objetos del dominio** a partir de esos primitivos (crear el `Paciente`, el `Domicilio` asociado, etc.).
- **Llamar al Servicio correspondiente** pasándole los objetos ya construidos.
- **Devolver el resultado a la Vista** (un objeto, una lista, un booleano).

### ¿De qué NO se encarga?
- **NO valida reglas de negocio** (DNI duplicado, etc.) — eso es del Servicio.
- **NO accede a repositorios directamente** — siempre pasa por el Servicio.
- **NO usa Scanner ni System.out** — los datos le llegan ya leídos por la Vista.

### ¿Por qué existe esta capa?
Es la respuesta a la pregunta: *"¿quién arma el objeto Paciente cuando el usuario ingresa nombre, apellido y DNI por consola?"*
- **No puede ser la Vista**, porque la Vista debería saber lo mínimo posible del dominio (idealmente solo trabaja con strings).
- **No puede ser el Servicio**, porque el Servicio quiere recibir objetos del dominio limpios para validar reglas, no preocuparse de cómo se armaron.
- **El Controlador es el lugar correcto** para esa "traducción". Es el adaptador entre los dos mundos.

Además, esta capa es la que permite que **cambies la Vista sin tocar el Servicio**. Cuando en Entrega 4 reemplaces la consola por Swing, las Vistas cambian completamente — pero los Controladores y Servicios siguen iguales. Eso es OCP (Open/Closed Principle).

### Analogía
Es la **secretaria administrativa** que se sienta entre la recepcionista y la administradora. La recepcionista le dice *"vino un paciente nuevo, se llama Juan Pérez, DNI 30.123.456, vive en Av. Siempre Viva 742"*. La secretaria **toma esos datos**, **arma la ficha del paciente con sus campos**, y se la presenta a la administradora diciendo *"acá tengo este paciente para dar de alta"*. La administradora toma la ficha (ya bien armada) y aplica las reglas.

---

## Capa 6 — Vista (presentación / UI)

### ¿Qué es?
La capa que **habla con el usuario**. En Entrega 3 es consola (Scanner + System.out). En Entrega 4 será GUI Swing.

### ¿De qué se encarga?
- **Mostrar menús e información al usuario.**
- **Leer la entrada del usuario** con Scanner.
- **Llamar al Controlador** pasándole los datos primitivos leídos.
- **Capturar las excepciones** que vienen del Controlador/Servicio y mostrarlas como mensajes amigables.

### ¿De qué NO se encarga?
- **NO arma objetos del dominio.** Le pasa strings y números al Controlador.
- **NO valida reglas de negocio.** Solo valida formato superficial (por ejemplo: "este número no puede ser texto").
- **NO accede a Servicios ni Repositorios directamente.** Siempre pasa por el Controlador.

### Analogía
Es la **recepcionista**. Saluda, escucha al paciente, le hace preguntas, anota lo que dice en un papel **bruto**. Cuando algo se complica, no improvisa: le entrega el papel a la secretaria administrativa (controlador). Si la administradora le dice "el paciente ya estaba registrado", ella se lo comunica al cliente con tacto.

---

## El flujo completo: "Reservar un turno"

Acá es donde se ve cómo todo se conecta. Mirá cómo viaja la información:

**1. Vista (`VistaTurno`):**
> Imprime el menú. Usuario elige "Reservar turno". La vista le pide al usuario el DNI del paciente, la matrícula del odontólogo, la fecha y la hora — todo como Strings.

**2. Vista llama al Controlador:**
> `controladorTurno.asignar(idPaciente, idOdontologo, fecha, hora)`. La vista ya parseó los datos (LocalDate.parse, Long.parseLong, etc.) y se los pasa al controlador en su forma "casi correcta".

**3. Controlador (`ControladorTurno`):**
> Recibe los IDs y la fecha/hora. Llama al Servicio: `servicioTurno.reservar(idPaciente, idOdontologo, fecha, hora)`.

**4. Servicio (`ServicioTurno`) coordina:**
> *"Voy a validar todo y crear el turno"*. Le pregunta al `repositorioPaciente` si el paciente existe. Le pregunta al `repositorioOdontologo` si el odontólogo existe. Le pregunta al `repositorioTurno` si ese odontólogo ya tiene turno a esa hora.

**5. Repositorios responden:**
> *"Sí, ese paciente está", "Sí, ese odontólogo está", "No, no hay conflicto"*.

**6. Servicio crea el Turno (entidad del dominio):**
> Instancia un `Turno` nuevo con estado `PENDIENTE`.

**7. Servicio le pide al Repositorio que lo guarde:**
> `repositorioTurno.guardar(nuevoTurno)`.

**8. Repositorio guarda y devuelve el turno con su ID asignado.**

**9. El Turno sube de vuelta:** Servicio → Controlador → Vista.

**10. Vista le muestra al usuario:**
> *"Turno reservado correctamente. ID: 42. Fecha: 25/05/2026 a las 14:30."*

### ¿Y si algo falla?
Por ejemplo, el odontólogo ya tenía un turno a esa hora. Entonces:

- **Paso 4.b** — El servicio detecta el conflicto y **lanza** `TurnoYaReservadoException`.
- **Paso 10.b** — La excepción "sube" por las capas. El Controlador no la maneja (la deja pasar). La Vista la atrapa con un `try-catch` y muestra:
  > *"No se pudo reservar: el odontólogo ya tiene un turno a esa hora."*

---

## La regla de oro

**Cada capa solo conoce a las capas que están "debajo" de ella.** Nunca al revés.

```
Vista          ──conoce a──→  Controlador
Controlador    ──conoce a──→  Servicio
Servicio       ──conoce a──→  Repositorio
Servicio       ──conoce a──→  dominio
Repositorio    ──conoce a──→  dominio
Excepciones    ──son usadas por──→  Servicio (lanzadas) y Vista (atrapadas)
dominio        ──no conoce nada de las capas superiores──
```

Si un día se quiere cambiar la presentación de consola a GUI Swing, **solo se tocan las Vistas**. Los Controladores, Servicios y Repositorios siguen funcionando igual. Ese es el poder de esta separación.

---

## Cómo justificar la capa de Controladores (cuando te pregunten)

> *"Aplico una variante del patrón MVC adaptada a aplicación de consola. La Vista maneja la interacción con el usuario (Scanner, System.out). El Controlador es el adaptador entre la presentación y la lógica de negocio: recibe datos primitivos de la Vista y los traduce a operaciones sobre objetos del dominio. El Servicio queda enfocado únicamente en reglas de negocio. Esto cumple SRP (cada capa tiene una responsabilidad clara), prepara el sistema para cambiar la UI sin tocar el resto (OCP), y mantiene el bajo acoplamiento entre capas."*
