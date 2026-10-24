package dominio;

public enum EstadoPago {
    PENDIENTE,  // el turno está pedido/confirmado y todavía no se cobró
    PAGADO,     // el secretario registró el cobro
    ANULADO;    // el turno se canceló antes de cobrarse
}
