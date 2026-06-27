package cl.techstore.api.dto;

import java.time.Instant;

public class AuditMessage {

    private String accion;
    private Long productoId;
    private String nombre;
    private String usuario;
    private Instant fecha;

    public AuditMessage(String accion, Long productoId, String nombre, String usuario) {
        this.accion = accion;
        this.productoId = productoId;
        this.nombre = nombre;
        this.usuario = usuario;
        this.fecha = Instant.now();
    }

    public String getAccion() {
        return accion;
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public Instant getFecha() {
        return fecha;
    }
}