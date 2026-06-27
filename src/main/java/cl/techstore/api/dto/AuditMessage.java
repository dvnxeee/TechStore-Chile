package cl.techstore.api.dto;

import java.time.Instant;

public class AuditMessage {

    private String accion;
    private Long productoId;
    private String usuario;
    private Instant fecha;

    public AuditMessage(String accion, Long productoId, String usuario) {
        this.accion = accion;
        this.productoId = productoId;
        this.usuario = usuario;
        this.fecha = Instant.now();
    }

    public String getAccion() {
        return accion;
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getUsuario() {
        return usuario;
    }

    public Instant getFecha() {
        return fecha;
    }
}
