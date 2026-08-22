package cl.Barberia.interfaces.rest;

import cl.Barberia.infrastructure.persistence.entity.ServicioEntity;
import cl.Barberia.infrastructure.persistence.repository.ServicioRepositoryJpa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Catálogo de servicios de la barbería")
public class ServicioController {

    private final ServicioRepositoryJpa servicioRepository;

    @GetMapping
    @Operation(summary = "Listar todos los servicios activos")
    public ResponseEntity<List<ServicioEntity>> listarServicios() {
        return ResponseEntity.ok(servicioRepository.findByActivoTrue());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un servicio por ID")
    public ResponseEntity<ServicioEntity> obtenerServicio(@PathVariable Long id) {
        return servicioRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo servicio (solo ADMIN)")
    public ResponseEntity<ServicioEntity> crearServicio(@RequestBody ServicioEntity servicio) {
        return ResponseEntity.ok(servicioRepository.save(servicio));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un servicio existente (solo ADMIN)")
    public ResponseEntity<ServicioEntity> actualizarServicio(@PathVariable Long id, 
                                                             @RequestBody ServicioEntity servicio) {
        if (!servicioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        servicio.setId(id);
        return ResponseEntity.ok(servicioRepository.save(servicio));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un servicio (solo ADMIN)")
    public ResponseEntity<Void> desactivarServicio(@PathVariable Long id) {
        return servicioRepository.findById(id)
            .map(servicio -> {
                servicio.setActivo(false);
                servicioRepository.save(servicio);
                return ResponseEntity.ok().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}