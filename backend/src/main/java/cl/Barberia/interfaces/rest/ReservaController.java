package cl.Barberia.interfaces.rest;

import cl.Barberia.infrastructure.persistence.entity.ReservaEntity;
import cl.Barberia.infrastructure.persistence.repository.ReservaRepositoryJpa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de citas y reservas")
public class ReservaController {

    private final ReservaRepositoryJpa reservaRepository;

    @GetMapping
    @Operation(summary = "Listar todas las reservas")
    public ResponseEntity<List<ReservaEntity>> listarReservas() {
        return ResponseEntity.ok(reservaRepository.findAll());
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar reservas de un cliente")
    public ResponseEntity<List<ReservaEntity>> listarReservasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaRepository.findByClienteId(clienteId));
    }

    @GetMapping("/fecha/{fecha}")
    @Operation(summary = "Listar reservas por fecha")
    public ResponseEntity<List<ReservaEntity>> listarReservasPorFecha(@PathVariable String fecha) {
        LocalDate fechaParseada = LocalDate.parse(fecha);
        return ResponseEntity.ok(reservaRepository.findByFecha(fechaParseada));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar reservas por estado")
    public ResponseEntity<List<ReservaEntity>> listarReservasPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reservaRepository.findByEstado(estado));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaEntity reserva) {
        if (reserva.getFecha() != null
                && reserva.getHora() != null
                && reserva.getFecha().equals(LocalDate.now())
                && reserva.getHora().isBefore(LocalTime.now())) {
            return ResponseEntity.badRequest().body("No se puede reservar una hora que ya pasó");
        }

        // Generar código único
        reserva.setCodigo("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reserva.setEstado("PENDIENTE");
        return ResponseEntity.ok(reservaRepository.save(reserva));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de una reserva")
    public ResponseEntity<ReservaEntity> actualizarEstado(@PathVariable Long id, 
                                                          @RequestParam String estado) {
        return reservaRepository.findById(id)
            .map(reserva -> {
                reserva.setEstado(estado);
                return ResponseEntity.ok(reservaRepository.save(reserva));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        return reservaRepository.findById(id)
            .map(reserva -> {
                reserva.setEstado("CANCELADA");
                reservaRepository.save(reserva);
                return ResponseEntity.ok().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}