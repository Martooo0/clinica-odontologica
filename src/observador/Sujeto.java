package observador;

import java.util.ArrayList;
import java.util.List;


public abstract class Sujeto {

    private final List<Observador> observadores = new ArrayList<>();

    public void agregarObservador(Observador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void quitarObservador(Observador observador) {
        observadores.remove(observador);
    }

    protected void notificarObservadores(Evento evento) {
        for (Observador observador : new ArrayList<>(observadores)) {
            observador.actualizar(evento);
        }
    }
}
