package vista;

import dominio.Identificable;

import javax.swing.JComboBox;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ComboEntidad<T extends Identificable> extends JComboBox<String> {

    private final Function<T, String> etiqueta;
    private List<T> elementos = new ArrayList<>();

    public ComboEntidad(Function<T, String> etiqueta) {
        this.etiqueta = etiqueta;
    }

    public void cargar(List<T> nuevos) {
        T elegido = getSeleccionado();
        removeAllItems();
        elementos = nuevos;
        for (T elemento : elementos) {
            addItem(etiqueta.apply(elemento));
        }
        if (elegido != null && indiceDe(elegido) != -1) {
            setSelectedIndex(indiceDe(elegido));
        }
    }

    public T getSeleccionado() {
        int indice = getSelectedIndex();
        return indice == -1 ? null : elementos.get(indice);
    }

    public void seleccionar(T elemento) {
        setSelectedIndex(elemento == null ? -1 : indiceDe(elemento));
    }

    private int indiceDe(T elemento) {
        for (int i = 0; i < elementos.size(); i++) {
            if (elementos.get(i).getId().equals(elemento.getId())) {
                return i;
            }
        }
        return -1;
    }
}
