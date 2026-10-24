package controlador;

import observador.Observador;
import observador.Sujeto;

public abstract class ControladorObservable {

    private final Sujeto sujeto;

    protected ControladorObservable(Sujeto sujeto) {
        this.sujeto = sujeto;
    }

    public void agregarObservador(Observador observador) {
        sujeto.agregarObservador(observador);
    }

    public void quitarObservador(Observador observador) {
        sujeto.quitarObservador(observador);
    }
}
