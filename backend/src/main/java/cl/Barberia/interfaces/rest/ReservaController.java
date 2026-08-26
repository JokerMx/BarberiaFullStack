package cl.Barberia.interfaces.rest;

import cl.Barberia.infrastructure.persistence.entity.ReservaEntity;
import cl.Barberia.application.reservation.ReservaApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de citas y reservas")
public class ReservaController {

    private final ReservaApplicationService reservaService;

    @GetMapping
    @Operation(summary = "Listar todas las reservas")
    public ResponseEntity<List<ReservaEntity>> listarReservas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar reservas de un cliente")
    public ResponseEntity<List<ReservaEntity>> listarReservasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.listarPorCliente(clienteId));
    }

    @GetMapping("/fecha/{fecha}")
    @Operation(summary = "Listar reservas por fecha")
    public ResponseEntity<List<ReservaEntity>> listarReservasPorFecha(@PathVariable String fecha) {
        LocalDate fechaParseada = LocalDate.parse(fecha);
        return ResponseEntity.ok(reservaService.listarPorFecha(fechaParseada));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar reservas por estado")
    public ResponseEntity<List<ReservaEntity>> listarReservasPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reservaService.listarPorEstado(estado));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaEntity reserva) {
        return ResponseEntity.ok(reservaService.crear(reserva));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de una reserva")
    public ResponseEntity<ReservaEntity> actualizarEstado(@PathVariable Long id, 
                                                          @RequestParam String estado) {
        return reservaService.actualizarEstado(id, estado)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        return reservaService.cancelar(id)
            .map(reserva -> ResponseEntity.ok().<Void>build())
            .orElse(ResponseEntity.notFound().build());
    }
}