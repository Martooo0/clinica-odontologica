package vista;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Toolkit;

public class AreaNotificaciones extends JScrollPane {

    private final JTextArea area;

    public AreaNotificaciones(String titulo) {
        this(titulo, new JTextArea());
    }

    public AreaNotificaciones(String titulo, int filas, int columnas) {
        this(titulo, new JTextArea(filas, columnas));
    }

    private AreaNotificaciones(String titulo, JTextArea area) {
        super(area);
        this.area = area;
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        setBorder(BorderFactory.createTitledBorder(titulo));
    }

    public void agregar(String mensaje) {
        area.append(mensaje + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    public void notificar(String mensaje) {
        agregar(mensaje);
        Toolkit.getDefaultToolkit().beep();
    }

    public void limpiar() {
        area.setText("");
    }
}
