package cl.Barberia.application.servicecatalog;

import cl.Barberia.infrastructure.persistence.entity.ServicioEntity;
import cl.Barberia.infrastructure.persistence.repository.ServicioRepositoryJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioApplicationServiceTest {

    @Mock
    private ServicioRepositoryJpa servicioRepository;

    @InjectMocks
    private ServicioApplicationService servicioService;

    @Test
    void updatesExistingServiceWithRequestedId() {
        ServicioEntity datos = ServicioEntity.builder()
            .nombre("Corte")
            .precio(BigDecimal.valueOf(10000))
            .duracionMinutos(30)
            .build();
        when(servicioRepository.existsById(4L)).thenReturn(true);
        when(servicioRepository.save(datos)).thenReturn(datos);

        ServicioEntity updated = servicioService.actualizar(4L, datos).orElseThrow();

        assertEquals(4L, updated.getId());
        verify(servicioRepository).save(datos);
    }

    @Test
    void desactivatesExistingService() {
        ServicioEntity servicio = ServicioEntity.builder().activo(true).build();
        when(servicioRepository.findById(4L)).thenReturn(Optional.of(servicio));
        when(servicioRepository.save(servicio)).thenReturn(servicio);

        servicioService.desactivar(4L);

        assertFalse(servicio.getActivo());
        verify(servicioRepository).save(servicio);
    }
}
