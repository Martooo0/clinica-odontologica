package vista;

import javax.swing.table.DefaultTableModel;

public class ModeloTablaSoloLectura extends DefaultTableModel {

    private static final int COLUMNA_ID = 0;

    public ModeloTablaSoloLectura(String... columnas) {
        super(columnas, 0);
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    public Long getId(int fila) {
        return (Long) getValueAt(fila, COLUMNA_ID);
    }
}
