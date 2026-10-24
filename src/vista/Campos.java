package vista;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.Color;

final class Campos {

    private static final String BORDE_NORMAL = "TextField.border";
    private static final Color COLOR_ERROR = Color.RED;
    private static final int GROSOR_ERROR = 2;

    private Campos() {}

    // Validación visual: marca en rojo los campos vacíos y devuelve true si hay alguno
    static boolean marcarVacios(JTextField... campos) {
        boolean hayVacios = false;
        for (JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) {
                campo.setBorder(BorderFactory.createLineBorder(COLOR_ERROR, GROSOR_ERROR));
                hayVacios = true;
            } else {
                campo.setBorder(UIManager.getBorder(BORDE_NORMAL));
            }
        }
        return hayVacios;
    }

    static void limpiar(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
            campo.setBorder(UIManager.getBorder(BORDE_NORMAL));
        }
    }
}
