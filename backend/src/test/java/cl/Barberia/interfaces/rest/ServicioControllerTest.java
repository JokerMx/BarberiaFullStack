package cl.Barberia.interfaces.rest;

import cl.Barberia.application.servicecatalog.ServicioApplicationService;
import cl.Barberia.infrastructure.persistence.entity.ServicioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioControllerTest {

    @Mock
    private ServicioApplicationService servicioApplicationService;

    @InjectMocks
    private ServicioController servicioController;

    @Test
    void returnsServicesAndCreatesService() {
        ServicioEntity servicio = ServicioEntity.builder().id(1L).build();
        when(servicioApplicationService.listarActivos()).thenReturn(List.of(servicio));
        when(servicioApplicationService.crear(servicio)).thenReturn(servicio);

        assertEquals(1, servicioController.listarServicios().getBody().size());
        assertEquals(200, servicioController.crearServicio(servicio).getStatusCode().value());
    }

    @Test
    void mapsOptionalQueriesAndCommandsToHttpStatus() {
        ServicioEntity servicio = ServicioEntity.builder().id(1L).build();
        when(servicioApplicationService.obtener(1L)).thenReturn(Optional.of(servicio));
        when(servicioApplicationService.obtener(2L)).thenReturn(Optional.empty());
        when(servicioApplicationService.actualizar(1L, servicio)).thenReturn(Optional.of(servicio));
        when(servicioApplicationService.actualizar(2L, servicio)).thenReturn(Optional.empty());
        when(servicioApplicationService.desactivar(1L)).thenReturn(Optional.of(servicio));
        when(servicioApplicationService.desactivar(2L)).thenReturn(Optional.empty());

        assertEquals(200, servicioController.obtenerServicio(1L).getStatusCode().value());
        assertEquals(404, servicioController.obtenerServicio(2L).getStatusCode().value());
        assertEquals(200, servicioController.actualizarServicio(1L, servicio).getStatusCode().value());
        assertEquals(404, servicioController.actualizarServicio(2L, servicio).getStatusCode().value());
        assertEquals(200, servicioController.desactivarServicio(1L).getStatusCode().value());
        assertEquals(404, servicioController.desactivarServicio(2L).getStatusCode().value());
    }
}