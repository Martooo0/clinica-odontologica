package vista;

import controlador.ControladorPaciente;
import dominio.Paciente;
import observador.Evento;
import observador.Observador;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class VentanaGestionPacientes extends VentanaABM implements Observador {

    private static final Dimension TAMANIO = new Dimension(820, 520);

    private final ControladorPaciente controladorPaciente;
    private final FormularioPaciente formulario = new FormularioPaciente();

    public VentanaGestionPacientes(ControladorPaciente controladorPaciente) {
        super("Gestión de Pacientes", TAMANIO, "paciente");
        this.controladorPaciente = controladorPaciente;
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del paciente"));
        add(formulario, BorderLayout.NORTH);
        agregarTabla("Pacientes registrados", "ID", "Nombre", "Apellido", "DNI", "Email", "Obra social", "Domicilio");
        refrescarTabla();

        // Me suscribo a los cambios de pacientes y me doy de baja al cerrar
        suscribir(this, controladorPaciente);
    }

    // Observer: se registró, modificó o eliminó un paciente (desde cualquier ventana) -> recargo la tabla
    @Override
    public void actualizar(Evento evento) {
        refrescarTabla();
    }

    @Override
    protected void agregar() {
        controladorPaciente.registrar(formulario.getDatos());
    }

    @Override
    protected void modificar(Long id) {
        controladorPaciente.modificar(id, formulario.getDatos());
    }

    @Override
    protected void eliminar(Long id) {
        controladorPaciente.eliminar(id);
    }

    @Override
    protected void mostrarEnFormulario(Long id) {
        formulario.mostrar(controladorPaciente.buscarPorId(id));
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
        for (Paciente paciente : controladorPaciente.listarPorApellido()) {
            filas.add(new Object[]{
                    paciente.getId(), paciente.getNombre(), paciente.getApellido(), paciente.getDni(),
                    paciente.getEmail(), paciente.getObraSocial().getNombre(), paciente.getDomicilio()
            });
        }
        return filas;
    }
}
