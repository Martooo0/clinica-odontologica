# Trabajo Práctico — Sistema de Gestión de Clínica Odontológica "Sonrisa Feliz"

**Materia:** Programación II — **Carrera:** (UADE)
**Alumno:** Martín Ferreira
**Entrega:** 3 — **Fecha:** 26/05/2026

---

## 1. Introducción

El presente trabajo consiste en el desarrollo de un sistema de gestión para una clínica odontológica, implementado en **Java** bajo el paradigma de **Programación Orientada a Objetos**. El sistema permite administrar **pacientes**, **odontólogos** y **turnos**, ofreciendo operaciones de alta, baja, modificación, búsqueda y listado sobre cada uno, junto con la asignación y el seguimiento del estado de los turnos.

El diseño se organiza en una **arquitectura en capas**, donde cada capa tiene una responsabilidad única y bien delimitada. Esta separación facilita el mantenimiento, permite reemplazar partes del sistema sin afectar al resto (por ejemplo, cambiar la interfaz de consola por una interfaz gráfica en una etapa posterior) y refleja de manera directa los principios de bajo acoplamiento y alta cohesión.

---

## 2. Arquitectura en capas

El sistema se estructura en seis capas. La regla fundamental es que **cada capa solo conoce a la capa inmediatamente inferior**, nunca a la inversa: la información viaja hacia abajo en forma de peticiones y hacia arriba en forma de resultados o excepciones.

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| **Dominio** | `dominio` | Representa las entidades del negocio (Paciente, Odontólogo, Turno, etc.) y su comportamiento propio. |
| **Repositorios** | `repositorio` | Persiste y recupera las entidades. Actualmente trabaja en memoria mediante `HashMap`. |
| **Servicios** | `servicio` | Contiene la lógica y las **reglas de negocio**; coordina repositorios y lanza excepciones de dominio. |
| **Excepciones** | `excepcion` | Jerarquía de errores específicos del negocio. |
| **Controladores** | `controlador` | Adaptador entre la presentación y la lógica: traduce datos primitivos en objetos del dominio. |
| **Vista** | `vista` | Interactúa con el usuario por consola (menús, lectura de datos y mensajes). |

**Flujo general:** `Vista → Controlador → Servicio → Repositorio → Dominio`. Esta dirección única de dependencias es lo que permite, por ejemplo, sustituir la capa de Vista por una interfaz gráfica Swing en el futuro sin modificar las capas inferiores.

---

## 3. Modelo de dominio

Las entidades del negocio fueron modeladas aprovechando la **herencia** para evitar duplicación de código:

- **`Persona`** *(clase abstracta)*: define los atributos comunes `id`, `nombre` y `apellido`, junto con el método `getNombreCompleto()`. No se instancia directamente.
- **`Paciente`** *(extiende `Persona`)*: agrega `dni`, `email`, `domicilio` y `fechaIngreso` (inicializada automáticamente con la fecha actual).
- **`Odontologo`** *(extiende `Persona`)*: agrega `matricula`.
- **`Domicilio`**: clase asociada al paciente con `calle`, `numero`, `localidad` y `provincia`.
- **`Turno`**: relaciona un `Paciente` con un `Odontologo` en una `fecha` y `hora` determinadas, e incluye un `estado`. Posee el método de comportamiento propio `esFuturo()`, que indica si el turno aún no ha ocurrido.
- **`EstadoTurno`** *(enumeración)*: define los estados posibles de un turno: `PENDIENTE`, `CONFIRMADO`, `COMPLETADO` y `CANCELADO`.

Una decisión de diseño relevante es que **los constructores no reciben el `id`**: es el repositorio quien lo asigna al momento de persistir la entidad, garantizando identificadores únicos y secuenciales.

---

## 4. Reglas de negocio implementadas

Las validaciones residen en la **capa de Servicios**, que recibe objetos del dominio ya construidos y verifica que cumplan las reglas antes de delegar la persistencia. Si una regla no se cumple, se lanza una excepción específica.

### Paciente
- El objeto paciente no puede ser nulo.
- El nombre y el apellido no pueden estar vacíos.
- El DNI debe contener únicamente dígitos y tener entre 7 y 8 caracteres.
- El email debe contener los caracteres `@` y `.`.
- No pueden existir dos pacientes con el mismo DNI.

### Odontólogo
- El objeto odontólogo no puede ser nulo.
- El nombre, el apellido y la matrícula no pueden estar vacíos.
- No pueden existir dos odontólogos con la misma matrícula.
- Al modificar, la nueva matrícula no puede coincidir con la de **otro** odontólogo distinto.

### Turno
- El paciente y el odontólogo indicados deben existir previamente en el sistema.
- La fecha y la hora no pueden ser nulas.
- La fecha del turno no puede ser anterior a la fecha actual (tanto al asignar como al reprogramar).
- Un odontólogo no puede tener dos turnos en la misma fecha y hora.

---

## 5. Manejo de excepciones

El sistema define una jerarquía de excepciones de negocio que parte de la clase base **`ClinicaException`** (que extiende `RuntimeException`). De ella derivan errores específicos:

`PacienteNoEncontradoException`, `OdontologoNoEncontradoException`, `TurnoNoEncontradoException`, `TurnoYaReservadoException`, `DniDuplicadoException`, `MatriculaDuplicadaException` y `DatoInvalidoException`.

El criterio de diseño es claro: el **Servicio lanza** las excepciones cuando detecta una violación de reglas, estas **suben** a través de las capas (el Controlador no las intercepta) y la **Vista las captura** con bloques `try-catch`, traduciéndolas en mensajes comprensibles para el usuario. De esta forma, los mensajes técnicos quedan separados de los mensajes de interfaz, y cada error tiene un nombre y un significado propios en lugar de devolver valores genéricos como `null` o `-1`.

---

## 6. Conceptos de POO aplicados

| Concepto | Aplicación en el proyecto |
|----------|---------------------------|
| **Encapsulamiento** | Todos los atributos son privados y se acceden mediante getters y setters. |
| **Herencia** | `Paciente` y `Odontologo` extienden la clase abstracta `Persona`. |
| **Abstracción** | `Persona` es abstracta; `IRepositorio` define un contrato sin implementación. |
| **Polimorfismo** | Sobrescritura de `toString()` en cada entidad; cada subtipo de `Persona` presenta su información de forma propia. |
| **Composición** | `Paciente` contiene un `Domicilio`; cada `Servicio` contiene sus `Repositorios`. |
| **Genéricos** | La interfaz `IRepositorio<T>` permite reutilizar el contrato de persistencia para cualquier entidad. |
| **Inyección de dependencias** | Los repositorios se crean una sola vez y se pasan a los servicios por constructor, compartiendo el mismo origen de datos. |

---

## 7. Principios SOLID y patrones GRASP

El diseño del sistema responde tanto a los principios **SOLID** como a los patrones **GRASP** de asignación de responsabilidades, en buena medida como consecuencia natural de la arquitectura en capas adoptada.

### Principios SOLID

| Principio | Aplicación en el proyecto |
|-----------|---------------------------|
| **Responsabilidad Única (SRP)** | Cada capa y cada clase tienen una única responsabilidad: el Servicio valida reglas, el Repositorio persiste, la Vista interactúa con el usuario. |
| **Abierto/Cerrado (OCP)** | El sistema se extiende sin modificar lo existente: se pueden incorporar nuevas entidades o reemplazar la Vista de consola por una interfaz gráfica sin alterar las capas inferiores. |
| **Sustitución de Liskov (LSP)** | `Paciente` y `Odontologo` pueden utilizarse en cualquier contexto que espere un `Persona` sin alterar el comportamiento esperado. |
| **Segregación de Interfaces (ISP)** | `IRepositorio<T>` define únicamente las operaciones genéricas de persistencia; las búsquedas específicas residen en las clases concretas, evitando que alguna clase deba implementar métodos que no utiliza. |
| **Inversión de Dependencias (DIP)** | Las dependencias se inyectan por constructor: `MenuPrincipal` actúa como punto de composición y construye la cadena repositorios → servicios → controladores → vistas. La persistencia se apoya en la abstracción `IRepositorio<T>`. |

### Patrones GRASP

| Patrón | Aplicación en el proyecto |
|--------|---------------------------|
| **Controlador** | La capa de Controladores recibe las operaciones de la interfaz y las coordina, sin contener lógica de negocio. |
| **Experto en Información** | Cada clase resuelve lo que le corresponde según los datos que posee: `Turno.esFuturo()` determina por sí mismo si el turno aún no ocurrió y `Persona.getNombreCompleto()` arma el nombre completo. |
| **Fabricación Pura** | Los Repositorios son clases artificiales —no representan un concepto del mundo real de la clínica— creadas para aislar la persistencia y mantener limpio el modelo de dominio. |
| **Indirección** | El Controlador media entre la Vista y el Servicio, y el Servicio entre el Controlador y el Repositorio, evitando el acoplamiento directo entre extremos. |
| **Alta Cohesión** | Cada clase agrupa un conjunto de responsabilidades estrechamente relacionadas entre sí. |
| **Bajo Acoplamiento** | Cada capa conoce únicamente a la capa inmediatamente inferior y recibe sus colaboradores por inyección. |
| **Variaciones Protegidas** | La Vista captura la excepción base `ClinicaException`, de modo que la incorporación de nuevos errores de negocio no obliga a modificar el manejo de excepciones existente. |

---

## 8. Flujo de ejemplo: asignación de un turno

El siguiente recorrido ilustra cómo colaboran las capas en una operación concreta:

1. La **Vista** muestra el menú, solicita al usuario los datos del turno (IDs de paciente y odontólogo, fecha y hora) y parsea las cadenas a `LocalDate` y `LocalTime`.
2. Llama al **Controlador**, que reenvía la petición al **Servicio**.
3. El **Servicio** valida las reglas: verifica que la fecha no sea nula ni anterior a hoy, consulta al `RepositorioPaciente` y al `RepositorioOdontologo` que ambos existan, y al `RepositorioTurno` que el odontólogo no tenga otro turno en ese horario.
4. Si todo es correcto, crea un `Turno` nuevo con estado `PENDIENTE` y le pide al repositorio que lo guarde.
5. El turno, ya con su `id` asignado, **sube** de regreso hasta la Vista, que confirma la operación al usuario.

Si alguna regla falla —por ejemplo, el horario ya está ocupado— el Servicio lanza `TurnoYaReservadoException`, que la Vista captura y presenta como un mensaje claro, sin interrumpir la ejecución del programa.

---

## 9. Ejecución y pruebas

El punto de entrada del sistema es la clase **`Main`**, que instancia un **`MenuPrincipal`**. Este se encarga de construir toda la cadena de dependencias (repositorios → servicios → controladores → vistas) y de mostrar el menú raíz, desde el cual se accede a la gestión de pacientes, odontólogos y turnos.

El sistema fue probado de forma integral ejecutando un flujo completo de operaciones: alta válida e inválida de cada entidad, búsquedas por identificador y por DNI/matrícula, listados, modificaciones, eliminaciones y la gestión de turnos (asignación, cambio de estado, reprogramación y eliminación). Se verificó especialmente el correcto disparo de **todas las reglas de negocio**, comprobando que cada validación rechaza los datos inválidos con la excepción y el mensaje correspondientes.

---

## 10. Conclusión

El sistema cumple con los objetivos planteados para esta etapa: implementa una arquitectura en capas coherente, un modelo de dominio que aplica los pilares de la Programación Orientada a Objetos y un conjunto completo de reglas de negocio centralizadas en la capa de Servicios. La separación de responsabilidades logra que el código sea ordenado, extensible y preparado para futuras mejoras —como la incorporación de persistencia en archivos y una interfaz gráfica— sin necesidad de reescribir la lógica ya construida.
