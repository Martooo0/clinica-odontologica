package vista;

import dominio.Paciente;
import fachada.ClinicaFacade;
import observador.Evento;
import observador.Observador;
import observador.TipoEvento;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class VentanaGestionPacientes extends VentanaABM implements Observador {

    private static final Dimension TAMANIO = new Dimension(820, 520);

    private final ClinicaFacade clinica;
    private final FormularioPaciente formulario = new FormularioPaciente();

    public VentanaGestionPacientes(ClinicaFacade clinica) {
        super("Gestión de Pacientes", TAMANIO, "paciente");
        this.clinica = clinica;
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del paciente"));
        add(formulario, BorderLayout.NORTH);
        agregarTabla("Pacientes registrados", "ID", "Nombre", "Apellido", "DNI", "Email", "Obra social", "Domicilio");
        refrescarTabla();

        // Me suscribo a los cambios de pacientes y me doy de baja al cerrar
        suscribir(clinica, this);
    }

    // Observer: se registró, modificó o eliminó un paciente (desde cualquier ventana) -> recargo la tabla
    @Override
    public void actualizar(Evento evento) {
        if (evento.getCategoria() == TipoEvento.Categoria.PACIENTE) {
            refrescarTabla();
        }
    }

    @Override
    protected void agregar() {
        clinica.registrarPaciente(formulario.getDatos());
    }

    @Override
    protected void modificar(Long id) {
        clinica.modificarPaciente(id, formulario.getDatos());
    }

    @Override
    protected void eliminar(Long id) {
        clinica.eliminarPaciente(id);
    }

    @Override
    protected void mostrarEnFormulario(Long id) {
        formulario.mostrar(clinica.buscarPaciente(id));
    }

    @Override
    protected void limpiarFormulario() {
        formulario.limpiar();
    }

    @Override
    protected JTextField[] camposObligatorios() {
        return formulario.getCamposDeTexto();
    }

    @Override
    protected List<Object[]> filas() {
        List<Object[]> filas = new ArrayList<>();
        for (Paciente paciente : clinica.listarPacientesPorApellido()) {
            filas.add(new Object[]{
                    paciente.getId(), paciente.getNombre(), paciente.getApellido(), paciente.getDni(),
                    paciente.getEmail(), paciente.getObraSocial().getNombre(), paciente.getDomicilio()
            });
        }
        return filas;
    }
}
