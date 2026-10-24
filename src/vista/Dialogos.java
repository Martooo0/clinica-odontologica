package vista;

import javax.swing.JOptionPane;
import java.awt.Component;

final class Dialogos {

    private Dialogos() {}

    static void error(Component padre, String mensaje) {
        error(padre, "Error", mensaje);
    }

    static void error(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    static void aviso(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirmar(Component padre, String titulo, String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(padre, mensaje, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return respuesta == JOptionPane.YES_OPTION;
    }

    static boolean pedirDatos(Component padre, String titulo, Component formulario) {
        int respuesta = JOptionPane.showConfirmDialog(padre, formulario, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return respuesta == JOptionPane.OK_OPTION;
    }
}
