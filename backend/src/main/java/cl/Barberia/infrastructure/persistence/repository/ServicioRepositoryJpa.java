package cl.Barberia.infrastructure.persistence.repository;

import cl.Barberia.infrastructure.persistence.entity.ServicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepositoryJpa extends JpaRepository<ServicioEntity, Long> {
    List<ServicioEntity> findByActivoTrue();
}