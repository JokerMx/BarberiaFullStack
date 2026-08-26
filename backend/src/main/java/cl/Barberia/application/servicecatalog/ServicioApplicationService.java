package cl.Barberia.application.servicecatalog;

import cl.Barberia.infrastructure.persistence.entity.ServicioEntity;
import cl.Barberia.infrastructure.persistence.repository.ServicioRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioApplicationService {

    private final ServicioRepositoryJpa servicioRepository;

    public List<ServicioEntity> listarActivos() {
        return servicioRepository.findByActivoTrue();
    }

    public Optional<ServicioEntity> obtener(Long id) {
        return servicioRepository.findById(id);
    }

    public ServicioEntity crear(ServicioEntity servicio) {
        return servicioRepository.save(servicio);
    }

    public Optional<ServicioEntity> actualizar(Long id, ServicioEntity datos) {
        if (!servicioRepository.existsById(id)) {
            return Optional.empty();
        }
        datos.setId(id);
        return Optional.of(servicioRepository.save(datos));
    }

    public Optional<ServicioEntity> desactivar(Long id) {
        return servicioRepository.findById(id).map(servicio -> {
            servicio.setActivo(false);
            servicioRepository.save(servicio);
            return servicio;
        });
    }
}
