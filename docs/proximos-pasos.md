# Próximos pasos del TP — Sesión del 21/05/2026

Contexto para retomar el trabajo. Pensado para que Claude lo lea al inicio de la próxima sesión y entienda dónde quedamos sin tener que reconstruirlo desde cero.

---

## Estado actual

**UML de Entrega 3: COMPLETO.**

Archivo: `C:\Users\marti\Downloads\uml-poo-tp (1).excalidraw` (Martin lo va a guardar también en el proyecto eventualmente).

El UML tiene 6 capas codificadas por color:

| Color | Capa | Contenido |
|---|---|---|
| Naranja | Vistas | MenuPrincipal, VistaPaciente, VistaOdontologo, VistaTurno |
| Violeta | Controladores | ControladorPaciente, ControladorOdontologo, ControladorTurno |
| Rojo | Servicios | ServicioPaciente, ServicioOdontologo, ServicioTurno |
| Verde | Repositorios | IRepositorio\<T\> (interfaz) + 3 implementaciones |
| Azul | Dominio | Persona (abstract), Paciente, Odontologo, Domicilio, Turno, EstadoTurno |
| Amarillo | Excepciones | ClinicaException + 7 hijas |

**Total:** 28 clases + 1 interfaz + 1 enum.

**Único error pendiente en el UML:** `VistaTurno.mostrarLista` tiene parámetro `List<Odontologo>` — debe ser `List<Turno>`. Copy-paste error.

---

## Decisiones de diseño tomadas

1. **Arquitectura del profe:** capa Controller como adaptador entre Vista y Servicio (NO es Spring MVC; es MVC adaptado para consola). Justificación: la Vista trabaja con primitivos del usuario; el Controlador empaqueta esos primitivos en objetos del dominio; el Servicio aplica reglas de negocio. Documentado en `docs/arquitectura-en-capas.md`.

2. **Herencia:** Persona (abstract) ← Paciente, Odontologo. Atributos en `protected` (`#`). Lo pide el cronograma explícitamente (Clase 6).

3. **Relación Turno↔Paciente y Turno↔Odontologo:** **agregación** (rombo vacío), no asociación. **El profe lo enseña así** — la consigna dice "asociación" pero hay que seguir al profe.

4. **Persistencia: PENDIENTE.** No vamos a usar Serializable (el profe no lo explicó todavía). Cuando llegue ese tema, decidimos entre CSV o serialización Java.

5. **Comparable / Comparator: PENDIENTE.** Tampoco se vio. Si se necesita ordenar pacientes por apellido antes de que el profe lo explique, usaremos `Collections.sort` con lambda en el servicio (sin implementar Comparable).

6. **Sin Optional\<T\>:** Por simplicidad, los métodos `buscarPorId` devuelven `T` directamente (`null` si no existe). El Servicio se encarga de lanzar excepción si corresponde.

---

## Pendientes administrativos

- **Confirmar fecha real de Entrega 3 con el profe.** Hay inconsistencia:
  - Cronograma oficial: Clase 11 = 26/05/2026
  - Consigna detallada: Clase 14 = 16/06/2026

  Esto cambia si llegaremos a ver Comparable (Clase 12, 02/06) y Serialización (Clase 14, 16/06) antes de entregar.

---

## Próximo paso concreto: empezar a implementar el código

El UML está listo. Ahora toca traducirlo a Java SE puro.

**Orden sugerido para la implementación (de adentro hacia afuera):**

1. **Capa Dominio** (lo más independiente):
   - Crear paquete `modelo/`
   - Implementar `Persona` (abstract), `Paciente`, `Odontologo`, `Domicilio`, `Turno`, `EstadoTurno`
   - Probarlo con un `main` mínimo que cree instancias e imprima.

2. **Capa Excepciones:**
   - Crear paquete `excepcion/`
   - Implementar `ClinicaException` (extends RuntimeException) y las 7 hijas.

3. **Capa Repositorios:**
   - Crear paquete `repositorio/`
   - Implementar `IRepositorio<T>` (interfaz genérica)
   - Implementar los 3 repos concretos con HashMap.

4. **Capa Servicios:**
   - Crear paquete `servicio/`
   - Implementar los 3 servicios con validaciones y lanzando excepciones.

5. **Capa Controladores:**
   - Crear paquete `controlador/`
   - Implementar los 3 controladores.

6. **Capa Vistas + Main:**
   - Crear paquete `vista/`
   - Implementar las 3 vistas + MenuPrincipal.
   - El `Main` arma todo y llama a `menuPrincipal.iniciar()`.

---

## Estilo de trabajo a respetar

- **Martin pidió ser guiado paso a paso, NO recibir código terminado.**
- Explicar conceptos antes de tocar código.
- Proponer 1 paso a la vez y esperar a que él lo implemente.
- Revisar el código que él escriba; explicar mejoras, no reescribirlo entero.
- Excepción: andamiajes mecánicos (imports, getters/setters obvios) sí pueden mostrarse enteros si él los pide.

---

## Archivos clave del proyecto

- `docs/arquitectura-en-capas.md` — apuntes completos sobre la arquitectura de las 6 capas (sirve para defender el TP).
- `docs/proximos-pasos.md` — este archivo.
- `src/consignaTP.md` — consigna oficial del TP.
- `C:\Users\marti\Downloads\consignadelTP.md` — versión más larga.
- `C:\Users\marti\Downloads\Proyecto_Clinica_Odontologica_POO_Refactorizado.pdf` — doc general.
- `C:\Users\marti\Downloads\Cronograma_POO_2026.pdf` — cronograma de clases.
- `C:\Users\marti\Downloads\uml-poo-tp (1).excalidraw` — UML final de Entrega 3.

---

## Para retomar mañana

Empezá la próxima sesión diciéndome:

> *"Hola, retomemos el TP de Clínica Odontológica. Leé `docs/proximos-pasos.md` para tener contexto y arranquemos por la Capa de Dominio."*

Yo voy a leer este archivo, voy a entender dónde quedamos, y vamos a arrancar por implementar el Dominio paso a paso.
