package vista;

import controlador.ControladorObservable;
import observador.Observador;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class Ventana extends JFrame {

    protected Ventana(String titulo, Dimension tamanio) {
        setTitle(titulo);
        setSize(tamanio);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    protected void suscribir(Observador observador, ControladorObservable... controladores) {
        for (ControladorObservable controlador : controladores) {
            controlador.agregarObservador(observador);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                for (ControladorObservable controlador : controladores) {
                    controlador.quitarObservador(observador);
                }
            }
        });
    }
}
