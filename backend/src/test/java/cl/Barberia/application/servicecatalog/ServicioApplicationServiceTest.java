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
    void returnsEmptyWhenUpdatingMissingService() {
        when(servicioRepository.existsById(4L)).thenReturn(false);

        assertTrue(servicioService.actualizar(4L, ServicioEntity.builder().build()).isEmpty());
        verify(servicioRepository, never()).save(any(ServicioEntity.class));
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

    @Test
    void delegatesQueriesAndCreationToRepository() {
        ServicioEntity servicio = ServicioEntity.builder().id(1L).activo(true).build();
        when(servicioRepository.findByActivoTrue()).thenReturn(java.util.List.of(servicio));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(servicioRepository.save(servicio)).thenReturn(servicio);

        assertEquals(1, servicioService.listarActivos().size());
        assertEquals(servicio, servicioService.obtener(1L).orElseThrow());
        assertEquals(servicio, servicioService.crear(servicio));
    }

    @Test
    void returnsEmptyWhenDeactivatingMissingService() {
        when(servicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(servicioService.desactivar(99L).isEmpty());
    }
}
