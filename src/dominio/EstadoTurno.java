package dominio;

import observador.TipoEvento;

public enum EstadoTurno {
    PENDIENTE {
        @Override
        public boolean puedePasarA(EstadoTurno destino) {
            return destino == CONFIRMADO || destino == CANCELADO;
        }

        @Override
        public boolean esCancelablePorPaciente() {
            return true;
        }

        @Override
        public TipoEvento getEvento() {
            return TipoEvento.TURNO_PENDIENTE;
        }
    },
    CONFIRMADO {
        @Override
        public boolean puedePasarA(EstadoTurno destino) {
            return destino == PENDIENTE || destino == COMPLETADO || destino == CANCELADO;
        }

        @Override
        public boolean esCancelablePorPaciente() {
            return true;
        }

        @Override
        public TipoEvento getEvento() {
            return TipoEvento.TURNO_CONFIRMADO;
        }
    },
    COMPLETADO {
        @Override
        public boolean puedePasarA(EstadoTurno destino) {
            return false;
        }

        @Override
        public boolean esCancelablePorPaciente() {
            return false;
        }

        @Override
        public TipoEvento getEvento() {
            return TipoEvento.TURNO_COMPLETADO;
        }
    },
    CANCELADO {
        @Override
        public boolean puedePasarA(EstadoTurno destino) {
            return destino == PENDIENTE || destino == CONFIRMADO;
        }

        @Override
        public boolean esCancelablePorPaciente() {
            return false;
        }

        @Override
        public TipoEvento getEvento() {
            return TipoEvento.TURNO_CANCELADO;
        }

        @Override
        public boolean ocupaHorario() {
            return false;
        }

        @Override
        public void actualizarPago(Pago pago) {
            pago.anular();
        }
    };

    public abstract boolean puedePasarA(EstadoTurno destino);

    public abstract boolean esCancelablePorPaciente();

    public abstract TipoEvento getEvento();

    public boolean ocupaHorario() {
        return true;
    }

    public void actualizarPago(Pago pago) {
        pago.reactivar();
    }
}
