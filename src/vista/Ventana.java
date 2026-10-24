package vista;

import fachada.ClinicaFacade;
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

    protected void suscribir(ClinicaFacade clinica, Observador observador) {
        clinica.agregarObservador(observador);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                clinica.quitarObservador(observador);
            }
        });
    }
}
