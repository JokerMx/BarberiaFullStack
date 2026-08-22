package cl.Barberia.infrastructure.persistence.repository;

import cl.Barberia.infrastructure.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepositoryJpa extends JpaRepository<ReservaEntity, Long> {
    List<ReservaEntity> findByClienteId(Long clienteId);
    List<ReservaEntity> findByFecha(LocalDate fecha);
    List<ReservaEntity> findByEstado(String estado);
}