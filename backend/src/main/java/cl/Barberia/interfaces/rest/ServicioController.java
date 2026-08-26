package cl.Barberia.interfaces.rest;

import cl.Barberia.infrastructure.persistence.entity.ServicioEntity;
import cl.Barberia.application.servicecatalog.ServicioApplicationService;
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

    private final ServicioApplicationService servicioService;

    @GetMapping
    @Operation(summary = "Listar todos los servicios activos")
    public ResponseEntity<List<ServicioEntity>> listarServicios() {
        return ResponseEntity.ok(servicioService.listarActivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un servicio por ID")
    public ResponseEntity<ServicioEntity> obtenerServicio(@PathVariable Long id) {
        return servicioService.obtener(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo servicio (solo ADMIN)")
    public ResponseEntity<ServicioEntity> crearServicio(@RequestBody ServicioEntity servicio) {
        return ResponseEntity.ok(servicioService.crear(servicio));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un servicio existente (solo ADMIN)")
    public ResponseEntity<ServicioEntity> actualizarServicio(@PathVariable Long id, 
                                                             @RequestBody ServicioEntity servicio) {
        return servicioService.actualizar(id, servicio)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un servicio (solo ADMIN)")
    public ResponseEntity<Void> desactivarServicio(@PathVariable Long id) {
        return servicioService.desactivar(id)
            .map(servicio -> ResponseEntity.ok().<Void>build())
            .orElse(ResponseEntity.notFound().build());
    }
}