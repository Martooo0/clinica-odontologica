package repositorio;

import dominio.EstadoPago;
import dominio.MedioPago;
import dominio.ObraSocial;
import dominio.Pago;
import dominio.Turno;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RepositorioPago extends RepositorioBase<Pago> {

    private static final String ARCHIVO = "pagos.txt";
    private static final int ID = 0;
    private static final int TURNO = 1;
    private static final int PRECIO_LISTA = 2;
    private static final int OBRA_SOCIAL = 3;
    private static final int PORCENTAJE = 4;
    private static final int DESCUENTO = 5;
    private static final int MONTO_FINAL = 6;
    private static final int ESTADO = 7;
    private static final int MEDIO_PAGO = 8;
    private static final int FECHA_PAGO = 9;

    private final RepositorioTurno repositorioTurno;

    public RepositorioPago(RepositorioTurno repositorioTurno) {
        this.repositorioTurno = repositorioTurno;
    }

    // Un turno tiene como mucho un pago
    public Pago buscarPorTurno(Long idTurno) {
        return buscarPrimero(pago -> pago.getTurno().getId().equals(idTurno));
    }

    // Formato: id;turnoId;precioLista;obraSocial;porcentaje;descuento;montoFinal;estado;medioPago;fechaPago
    @Override
    protected String getArchivo() {
        return ARCHIVO;
    }

    @Override
    protected String serializar(Pago pago) {
        Object medio = pago.getMedioPago() == null ? VACIO : pago.getMedioPago().name();
        Object fecha = pago.getFechaPago() == null ? VACIO : pago.getFechaPago();
        return unir(pago.getId(), pago.getTurno().getId(), pago.getPrecioLista().toPlainString(),
                pago.getObraSocial().name(), pago.getPorcentajeDescuento(),
                pago.getDescuento().toPlainString(), pago.getMontoFinal().toPlainString(),
                pago.getEstado().name(), medio, fecha);
    }

    @Override
    protected Pago parsearLinea(String[] partes) {
        Turno turno = repositorioTurno.buscarPorId(Long.parseLong(partes[TURNO]));
        if (turno == null) return null; // el turno ya no existe: descarto el pago

        Pago pago = new Pago();
        pago.setId(Long.parseLong(partes[ID]));
        pago.setTurno(turno);
        pago.setPrecioLista(new BigDecimal(partes[PRECIO_LISTA]));
        pago.setObraSocial(ObraSocial.valueOf(partes[OBRA_SOCIAL]));
        pago.setPorcentajeDescuento(Integer.parseInt(partes[PORCENTAJE]));
        pago.setDescuento(new BigDecimal(partes[DESCUENTO]));
        pago.setMontoFinal(new BigDecimal(partes[MONTO_FINAL]));
        pago.setEstado(EstadoPago.valueOf(partes[ESTADO]));
        if (!partes[MEDIO_PAGO].equals(VACIO)) pago.setMedioPago(MedioPago.valueOf(partes[MEDIO_PAGO]));
        if (!partes[FECHA_PAGO].equals(VACIO)) pago.setFechaPago(LocalDate.parse(partes[FECHA_PAGO]));
        return pago;
    }
}
