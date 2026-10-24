package vista;

import excepcion.ClinicaException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class VentanaABM extends Ventana {

    private final String entidad;
    private ModeloTablaSoloLectura modelo;
    private JTable tabla;

    private Long idSeleccionado = null;

    protected VentanaABM(String titulo, Dimension tamanio, String entidad) {
        super(titulo, tamanio);
        this.entidad = entidad;
        add(crearBotones(), BorderLayout.SOUTH);
    }

    protected abstract void agregar();

    protected abstract void modificar(Long id);

    protected abstract void eliminar(Long id);

    protected abstract void mostrarEnFormulario(Long id);

    protected abstract void limpiarFormulario();

    protected abstract List<Object[]> filas();

    protected JTextField[] camposObligatorios() {
        return new JTextField[0];
    }

    protected String mensajeModificado() {
        return nombreEntidad() + " modificado correctamente.";
    }

    protected void agregarTabla(String titulo, String... columnas) {
        modelo = new ModeloTablaSoloLectura(columnas);
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarFilaEnFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnAgregar.addActionListener(e -> accionAgregar());
        btnModificar.addActionListener(e -> accionModificar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panel.add(btnAgregar);
        panel.add(btnModificar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        return panel;
    }

    private void accionAgregar() {
        if (Campos.marcarVacios(camposObligatorios())) {
            Dialogos.aviso(this, "Campos incompletos", "Completá los campos marcados en rojo.");
            return;
        }
        try {
            agregar();
            refrescarTabla();
            limpiarCampos();
            Dialogos.info(this, "Éxito", nombreEntidad() + " agregado correctamente.");
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void accionModificar() {
        if (idSeleccionado == null) {
            avisoSinSeleccion("modificar");
            return;
        }
        try {
            modificar(idSeleccionado);
            refrescarTabla();
            limpiarCampos();
            Dialogos.info(this, "Éxito", mensajeModificado());
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void accionEliminar() {
        if (idSeleccionado == null) {
            avisoSinSeleccion("eliminar");
            return;
        }
        boolean confirmado = Dialogos.confirmar(this, "Confirmar eliminación",
                "¿Seguro que querés eliminar el " + entidad + " seleccionado?");
        if (!confirmado) return;
        try {
            eliminar(idSeleccionado);
            refrescarTabla();
            limpiarCampos();
            Dialogos.info(this, "Éxito", nombreEntidad() + " eliminado.");
        } catch (ClinicaException ex) {
            Dialogos.error(this, ex.getMessage());
        }
    }

    private void avisoSinSeleccion(String accion) {
        Dialogos.aviso(this, "Sin selección", "Seleccioná un " + entidad + " de la tabla para " + accion + ".");
    }

    protected void refrescarTabla() {
        modelo.setRowCount(0); // vacío la tabla y la vuelvo a llenar desde cero
        for (Object[] fila : filas()) {
            modelo.addRow(fila);
        }
    }

    private void cargarFilaEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        idSeleccionado = modelo.getId(fila);
        mostrarEnFormulario(idSeleccionado);
    }

    protected void limpiarCampos() {
        idSeleccionado = null;
        tabla.clearSelection();
        limpiarFormulario();
    }

    private String nombreEntidad() {
        return Character.toUpperCase(entidad.charAt(0)) + entidad.substring(1);
    }
}
