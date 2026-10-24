package observador;

public enum TipoEvento {
    TURNO_SOLICITADO("El paciente solicitó un turno", Categoria.TURNO, true),
    TURNO_ASIGNADO("Secretaría asignó un turno", Categoria.TURNO, false),
    TURNO_CONFIRMADO("Turno confirmado", Categoria.TURNO, false),
    TURNO_CANCELADO("Turno cancelado por la clínica", Categoria.TURNO, false),
    TURNO_CANCELADO_POR_PACIENTE("El paciente canceló su turno", Categoria.TURNO, true),
    TURNO_COMPLETADO("Turno completado", Categoria.TURNO, false),
    TURNO_REPROGRAMADO("Turno reprogramado", Categoria.TURNO, false),
    TURNO_PENDIENTE("Turno vuelto a pendiente", Categoria.TURNO, false),
    TURNO_ELIMINADO("Turno eliminado", Categoria.TURNO, false),
    PAGO_REGISTRADO("Pago registrado", Categoria.PAGO, false),
    PACIENTE_REGISTRADO("Nuevo paciente registrado", Categoria.PACIENTE, true),
    PACIENTE_MODIFICADO("Datos de paciente modificados", Categoria.PACIENTE, false),
    PACIENTE_ELIMINADO("Paciente eliminado", Categoria.PACIENTE, false),
    ODONTOLOGO_REGISTRADO("Nuevo odontólogo", Categoria.ODONTOLOGO, false),
    ODONTOLOGO_MODIFICADO("Odontólogo modificado", Categoria.ODONTOLOGO, false),
    ODONTOLOGO_ELIMINADO("Odontólogo eliminado", Categoria.ODONTOLOGO, false),
    TRATAMIENTO_REGISTRADO("Nuevo tratamiento", Categoria.TRATAMIENTO, false),
    TRATAMIENTO_MODIFICADO("Tratamiento modificado", Categoria.TRATAMIENTO, false),
    TRATAMIENTO_ELIMINADO("Tratamiento eliminado", Categoria.TRATAMIENTO, false);

    public enum Categoria {
        TURNO,
        PAGO,
        PACIENTE,
        ODONTOLOGO,
        TRATAMIENTO
    }

    private final String descripcion;
    private final Categoria categoria;
    private final boolean accionDelPaciente;

    TipoEvento(String descripcion, Categoria categoria, boolean accionDelPaciente) {
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.accionDelPaciente = accionDelPaciente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public boolean esAccionDelPaciente() {
        return accionDelPaciente;
    }
}
