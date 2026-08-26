package cl.Barberia.application.reservation;

import cl.Barberia.infrastructure.persistence.entity.ReservaEntity;
import cl.Barberia.infrastructure.persistence.repository.ReservaRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaApplicationService {

    private final ReservaRepositoryJpa reservaRepository;

    public List<ReservaEntity> listarTodas() {
        return reservaRepository.findAll();
    }

    public List<ReservaEntity> listarPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    public List<ReservaEntity> listarPorFecha(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha);
    }

    public List<ReservaEntity> listarPorEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    public ReservaEntity crear(ReservaEntity reserva) {
        validarFechaYHora(reserva.getFecha(), reserva.getHora());
        reserva.setCodigo("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reserva.setEstado("PENDIENTE");
        return reservaRepository.save(reserva);
    }

    public Optional<ReservaEntity> actualizarEstado(Long id, String estado) {
        return reservaRepository.findById(id).map(reserva -> {
            reserva.setEstado(estado);
            return reservaRepository.save(reserva);
        });
    }

    public Optional<ReservaEntity> cancelar(Long id) {
        return reservaRepository.findById(id).map(reserva -> {
            reserva.setEstado("CANCELADA");
            reservaRepository.save(reserva);
            return reserva;
        });
    }

    private void validarFechaYHora(LocalDate fecha, LocalTime hora) {
        if (fecha != null && hora != null && fecha.equals(LocalDate.now()) && hora.isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("No se puede reservar una hora que ya pasó");
        }
    }
}
