# Informe — Entrega Final — Clínica Odontológica "Sonrisa Feliz"

**Materia:** Programación II — **Carrera:** (UADE)
**Alumno:** Martín Ferreira
**Entrega:** Final — Interfaz gráfica (Swing) + Persistencia en archivos

---

## 1. Objetivo de la etapa

La entrega final reemplaza la interfaz de consola por una **interfaz gráfica completa con Java
Swing** e incorpora **persistencia en archivos**, de modo que la información sobrevive entre
ejecuciones. Es la entrega integradora: todo lo construido antes (dominio, repositorios,
servicios, excepciones, controladores) se conecta detrás de una GUI.

## 2. Qué cambió respecto a la Entrega 3 (y por qué es importante)

La arquitectura en capas permitió que el cambio fuera **quirúrgico**:

| Capa | Cambio en esta entrega |
|------|------------------------|
| Dominio | **Sin cambios.** |
| Excepciones | **Sin cambios.** |
| Servicios | **Sin cambios** (las reglas de negocio se reutilizan tal cual). |
| Controladores | **Sin cambios.** |
| Repositorios | Se agregó **persistencia en archivos** (`guardarTodos()` / `cargar()`). |
| Vista | Se **reemplazó la consola por Swing**. |

El hecho de poder cambiar **toda** la presentación (de consola a gráfica) **sin tocar las capas
inferiores** es la demostración concreta del **Principio Abierto/Cerrado (OCP)** y del bajo
acoplamiento entre capas. La GUI se comunica únicamente con los **Controladores**, nunca con los
servicios o repositorios directamente, respetando el patrón **MVC**.

## 3. Estructura de la interfaz gráfica

La aplicación es una **ventana principal con botones** (`vista.VentanaPrincipal`), donde cada
botón abre la ventana de su sección. `VentanaPrincipal` cumple además dos roles:

- **Punto de composición:** arma la cadena de dependencias repositorios → servicios →
  controladores (el rol que en consola tenía `MenuPrincipal`).
- **Ciclo de vida:** carga los datos desde archivo al iniciar y los guarda al cerrar.

Ventanas de sección:

- `VentanaPacientes` — ABM de pacientes (con domicilio).
- `VentanaOdontologos` — ABM de odontólogos.
- `VentanaTurnos` — asignación de turnos (selección de paciente y odontólogo por `JComboBox`),
  reprogramación, cambio de estado y baja.
- `VentanaBusquedas` — búsqueda en vivo de pacientes/odontólogos.

Cada ventana de ABM sigue la misma plantilla: **formulario (arriba) + tabla (centro) + botones
(abajo)**, lo que da un código modular y reutilizable.

## 4. Componentes y conceptos de Swing aplicados

| Categoría | Elementos usados |
|-----------|------------------|
| Contenedores y controles | `JFrame`, `JPanel`, `JTable`, `JButton`, `JTextField`, `JLabel`, `JComboBox`, `JScrollPane`, `JOptionPane` |
| Layouts | `BorderLayout` (estructura de cada ventana), `GridBagLayout` (formularios), `FlowLayout` (botones), `GridLayout` (menú principal) |
| Modelo de tabla | `DefaultTableModel` (con `isCellEditable` en `false` para tablas de solo lectura) |
| Renderizado | `DefaultListCellRenderer` personalizado en los `JComboBox` de turnos (muestra "Nombre Apellido" en vez del `toString()` completo) |
| **Eventos** | `ActionListener` (botones), `MouseListener` (clic en fila de la tabla → carga el formulario), `KeyListener` (búsqueda en vivo en `VentanaBusquedas`), `WindowListener` (guardado automático al cerrar) |

## 5. Manejo de eventos (los 4 tipos)

- **`ActionListener`** — en todos los botones (alta, baja, modificación, búsqueda) y en los
  combos. Implementado con expresiones lambda.
- **`MouseListener`** (`MouseAdapter`) — al hacer clic en una fila de la tabla, se cargan los
  datos de ese registro en el formulario para editarlos.
- **`KeyListener`** (`KeyAdapter`) — en la ventana de Búsquedas, cada tecla refiltra la tabla
  de resultados en vivo.
- **`WindowListener`** (`WindowAdapter`) — al cerrar la ventana principal, se guardan todos los
  datos a archivo antes de salir.

## 6. Persistencia en archivos

Cada entidad se guarda en un archivo de texto, con un registro por línea y campos separados por
`;`:

- `pacientes.txt` → `id;nombre;apellido;dni;email;fechaIngreso;calle;numero;localidad;provincia`
- `odontologos.txt` → `id;nombre;apellido;matricula`
- `turnos.txt` → `id;pacienteId;odontologoId;fecha;hora;estado`

Decisiones de diseño:

- **Modelo cargar-al-abrir / guardar-al-cerrar.** La aplicación trabaja en memoria mientras está
  abierta; carga los archivos al iniciar y los vuelca al cerrar (vía `WindowListener`).
- **Los turnos guardan los IDs** de paciente y odontólogo (no el objeto completo), para no
  duplicar datos. Al cargar, esos IDs se **resuelven** a los objetos reales consultando los otros
  repositorios. Por eso el **orden de carga importa**: primero pacientes y odontólogos, luego
  turnos.
- **Restauración del contador de IDs.** Al cargar, cada repositorio deja su contador en el ID más
  alto leído, para que los nuevos registros no pisen a los existentes.
- La persistencia es responsabilidad de la **capa Repositorios**, coherente con su rol.

## 7. Flujo completo de una operación

Ejemplo "asignar un turno": el usuario elige paciente y odontólogo en los combos, escribe fecha y
hora, y presiona *Asignar* → la **Vista** captura el evento y llama al **Controlador** →
el **Controlador** llama al **Servicio** → el **Servicio** valida las reglas de negocio (fecha no
pasada, odontólogo libre, etc.) y, si todo está bien, crea el `Turno` y se lo pasa al
**Repositorio**, que lo guarda en memoria. Al **cerrar** la aplicación, el repositorio vuelca todo
a `turnos.txt`. Si una regla falla, el servicio lanza una excepción que la Vista atrapa y muestra
con un `JOptionPane`.

## 8. Validaciones

- **Validación visual:** los campos obligatorios vacíos se marcan con **borde rojo** antes de
  enviar.
- **Reglas de negocio:** las valida el **Servicio** (DNI con formato correcto, email con `@`,
  unicidad de DNI/matrícula, fecha del turno no anterior a hoy, etc.). Si algo no cumple, lanza la
  excepción correspondiente y la GUI la muestra como mensaje de error.
- **Formato de fecha/hora:** se captura `DateTimeParseException` para avisar al usuario si el
  formato es inválido.

## 9. Ejecución

La aplicación se ejecuta desde la clase `Main`. Las instrucciones para compilar y ejecutar desde
la línea de comandos están en `COMO-EJECUTAR.md`.

## 10. Cumplimiento de los criterios de evaluación

- **Funcionalidad:** la GUI permite alta, baja, modificación, búsqueda y listado de las tres
  entidades, con persistencia que funciona de punta a punta.
- **Diseño OO:** separación estricta GUI → controladores → servicios → repositorios; la GUI no
  conoce las capas internas. Eventos organizados por método. Ventanas modulares con una plantilla
  común.
- **Calidad de código:** nombres descriptivos, métodos cortos por responsabilidad, componentes
  reutilizables (la misma plantilla de ABM en cada sección).

## 11. Alcance no implementado

Los **hilos** (tema opcional de la consigna) no se incorporaron, por no ser requeridos para la
funcionalidad pedida.
