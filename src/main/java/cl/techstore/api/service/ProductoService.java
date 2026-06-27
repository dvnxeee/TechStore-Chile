package cl.techstore.api.service;

import cl.techstore.api.dto.AuditMessage;
import cl.techstore.api.dto.ProductoDTO;
import cl.techstore.api.model.Producto;
import cl.techstore.api.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final AuditProducer auditProducer;

    public ProductoService(ProductoRepository productoRepository, AuditProducer auditProducer) {
        this.productoRepository = productoRepository;
        this.auditProducer = auditProducer;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findByActivoTrue();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public Producto crear(ProductoDTO dto, String email) {
        Producto producto = new Producto();

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        Producto productoCreado = productoRepository.save(producto);

        auditProducer.enviarAuditoria(
                new AuditMessage("CREAR", productoCreado.getId(), email));

        return productoCreado;
    }

    public Producto modificar(Long id, ProductoDTO dto, String email) {
        Producto producto = buscarPorId(id);

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());

        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }

        Producto productoModificado = productoRepository.save(producto);

        auditProducer.enviarAuditoria(
                new AuditMessage("MODIFICAR", productoModificado.getId(), email));

        return productoModificado;
    }

    public void eliminar(Long id, String email) {
        Producto producto = buscarPorId(id);

        producto.setActivo(false);

        productoRepository.save(producto);

        auditProducer.enviarAuditoria(
                new AuditMessage("ELIMINAR", id, email));
    }
}