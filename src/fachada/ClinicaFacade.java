package fachada;

import dominio.Cotizacion;
import dominio.EstadoTurno;
import dominio.MedioPago;
import dominio.Odontologo;
import dominio.Paciente;
import dominio.Pago;
import dominio.Tratamiento;
import dominio.Turno;
import dto.DatosPaciente;
import dto.SolicitudTurno;
import observador.Observador;
import observador.Sujeto;
import repositorio.RepositorioBase;
import repositorio.RepositorioOdontologo;
import repositorio.RepositorioPaciente;
import repositorio.RepositorioPago;
import repositorio.RepositorioTratamiento;
import repositorio.RepositorioTurno;
import servicio.ServicioOdontologo;
import servicio.ServicioPaciente;
import servicio.ServicioPago;
import servicio.ServicioTratamiento;
import servicio.ServicioTurno;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ClinicaFacade {

    private final RepositorioPaciente repositorioPaciente = new RepositorioPaciente();
    private final RepositorioOdontologo repositorioOdontologo = new RepositorioOdontologo();
    private final RepositorioTratamiento repositorioTratamiento = new RepositorioTratamiento();
    private final RepositorioTurno repositorioTurno =
            new RepositorioTurno(repositorioPaciente, repositorioOdontologo, repositorioTratamiento);
    private final RepositorioPago repositorioPago = new RepositorioPago(repositorioTurno);

    // El orden de carga importa: cada uno necesita que ya existan los que referencia.
    // Turnos -> pacientes, odontólogos y tratamientos. Pagos -> turnos.
    private final List<RepositorioBase<?>> repositorios = List.of(repositorioPaciente, repositorioOdontologo,
            repositorioTratamiento, repositorioTurno, repositorioPago);

    private final ServicioPaciente servicioPaciente = new ServicioPaciente(repositorioPaciente);
    private final ServicioOdontologo servicioOdontologo = new ServicioOdontologo(repositorioOdontologo);
    private final ServicioTratamiento servicioTratamiento = new ServicioTratamiento(repositorioTratamiento, repositorioTurno);
    private final ServicioPago servicioPago = new ServicioPago(repositorioPago);
    private final ServicioTurno servicioTurno = new ServicioTurno(repositorioTurno, servicioPago);

    private final List<Sujeto> sujetos = List.of(servicioPaciente, servicioOdontologo, servicioTratamiento,
            servicioTurno, servicioPago);

    public void cargarTodo() {
        for (RepositorioBase<?> repositorio : repositorios) {
            repositorio.cargar();
        }
        servicioTratamiento.inicializarCatalogo(); // primera vez: catálogo de ejemplo
    }

    public void guardarTodo() {
        for (RepositorioBase<?> repositorio : repositorios) {
            repositorio.guardarTodos();
        }
    }

    public void agregarObservador(Observador observador) {
        for (Sujeto sujeto : sujetos) {
            sujeto.agregarObservador(observador);
        }
    }

    public void quitarObservador(Observador observador) {
        for (Sujeto sujeto : sujetos) {
            sujeto.quitarObservador(observador);
        }
    }

    public Paciente registrarPaciente(DatosPaciente datos) {
        return servicioPaciente.registrar(crearPaciente(datos));
    }

    public Paciente modificarPaciente(Long id, DatosPaciente datos) {
        Paciente cambios = crearPaciente(datos);
        cambios.setId(id);
        return servicioPaciente.modificar(cambios);
    }

    public void eliminarPaciente(Long id) {
        servicioPaciente.eliminar(id);
    }

    public Paciente buscarPaciente(Long id) {
        return servicioPaciente.buscarPorId(id);
    }

    public Paciente buscarPacientePorDni(String dni) {
        return servicioPaciente.buscarPorDni(dni);
    }

    public List<Paciente> listarPacientes() {
        return servicioPaciente.listarTodos();
    }

    public List<Paciente> listarPacientesPorApellido() {
        return servicioPaciente.listarPorApellido();
    }

    public Odontologo registrarOdontologo(String nombre, String apellido, String matricula) {
        return servicioOdontologo.registrar(new Odontologo(nombre, apellido, matricula));
    }

    public Odontologo modificarOdontologo(Long id, String nombre, String apellido, String matricula) {
        Odontologo cambios = new Odontologo(nombre, apellido, matricula);
        cambios.setId(id);
        return servicioOdontologo.modificar(cambios);
    }

    public void eliminarOdontologo(Long id) {
        servicioOdontologo.eliminar(id);
    }

    public Odontologo buscarOdontologo(Long id) {
        return servicioOdontologo.buscarPorId(id);
    }

    public List<Odontologo> listarOdontologos() {
        return servicioOdontologo.listarTodos();
    }

    public Tratamiento registrarTratamiento(String nombre, BigDecimal precio) {
        return servicioTratamiento.registrar(new Tratamiento(nombre, precio));
    }

    public Tratamiento modificarTratamiento(Long id, String nombre, BigDecimal precio) {
        Tratamiento cambios = new Tratamiento(nombre, precio);
        cambios.setId(id);
        return servicioTratamiento.modificar(cambios);
    }

    public void eliminarTratamiento(Long id) {
        servicioTratamiento.eliminar(id);
    }

    public Tratamiento buscarTratamiento(Long id) {
        return servicioTratamiento.buscarPorId(id);
    }

    public List<Tratamiento> listarTratamientos() {
        return servicioTratamiento.listarTodos();
    }

    public Turno solicitarTurno(SolicitudTurno solicitud) {
        return servicioTurno.solicitar(crearTurno(solicitud));
    }

    public Turno asignarTurno(SolicitudTurno solicitud) {
        return servicioTurno.asignar(crearTurno(solicitud));
    }

    public Turno confirmarTurno(Long idTurno) {
        return servicioTurno.confirmar(idTurno);
    }

    public Turno cancelarTurno(Long idTurno) {
        return servicioTurno.cancelar(idTurno);
    }

    public Turno cancelarTurnoPorPaciente(Long idTurno, Long idPaciente) {
        return servicioTurno.cancelarPorPaciente(idTurno, idPaciente);
    }

    public Turno cambiarEstadoTurno(Long idTurno, EstadoTurno estado) {
        return servicioTurno.cambiarEstado(idTurno, estado);
    }

    public Turno reprogramarTurno(Long idTurno, LocalDate fecha, LocalTime hora) {
        return servicioTurno.reprogramar(idTurno, fecha, hora);
    }

    public void eliminarTurno(Long idTurno) {
        servicioTurno.eliminar(idTurno);
    }

    public Turno buscarTurno(Long idTurno) {
        return servicioTurno.buscarPorId(idTurno);
    }

    public List<Turno> listarTurnos() {
        return servicioTurno.listarTodos();
    }

    public List<Turno> listarTurnosPorPaciente(Long idPaciente) {
        return servicioTurno.listarPorPaciente(idPaciente);
    }

    public List<Turno> listarTurnosPorEstado(EstadoTurno estado) {
        return servicioTurno.listarPorEstado(estado);
    }

    public Cotizacion cotizar(Tratamiento tratamiento, Paciente paciente) {
        return servicioPago.cotizar(tratamiento, paciente);
    }

    public Pago registrarCobro(Long idPago, MedioPago medioPago) {
        return servicioPago.registrarCobro(idPago, medioPago);
    }

    public Pago buscarPagoPorTurno(Long idTurno) {
        return servicioPago.buscarPorTurno(idTurno);
    }

    public List<Pago> listarPagos() {
        return servicioPago.listarTodos();
    }

    public BigDecimal totalCobrado() {
        return servicioPago.totalCobrado();
    }

    private Paciente crearPaciente(DatosPaciente datos) {
        return new Paciente(datos.getNombre(), datos.getApellido(), datos.getDni(), datos.getEmail(),
                datos.getDomicilio(), datos.getObraSocial());
    }

    private Turno crearTurno(SolicitudTurno solicitud) {
        return new Turno(solicitud.getFecha(), solicitud.getHora(),
                servicioPaciente.buscarPorId(solicitud.getIdPaciente()),
                servicioOdontologo.buscarPorId(solicitud.getIdOdontologo()),
                servicioTratamiento.buscarPorId(solicitud.getIdTratamiento()));
    }
}
