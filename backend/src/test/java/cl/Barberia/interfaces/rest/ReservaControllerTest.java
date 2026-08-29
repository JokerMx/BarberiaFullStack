package cl.Barberia.interfaces.rest;

import cl.Barberia.application.reservation.ReservaApplicationService;
import cl.Barberia.infrastructure.persistence.entity.ReservaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    @Mock
    private ReservaApplicationService reservaApplicationService;

    @InjectMocks
    private ReservaController reservaController;

    @Test
    void returnsReservationsForEveryFilter() {
        ReservaEntity reserva = ReservaEntity.builder().id(1L).build();
        when(reservaApplicationService.listarTodas()).thenReturn(List.of(reserva));
        when(reservaApplicationService.listarPorCliente(4L)).thenReturn(List.of(reserva));
        when(reservaApplicationService.listarPorFecha(LocalDate.of(2026, 8, 28))).thenReturn(List.of(reserva));
        when(reservaApplicationService.listarPorEstado("PENDIENTE")).thenReturn(List.of(reserva));

        assertEquals(1, reservaController.listarReservas().getBody().size());
        assertEquals(1, reservaController.listarReservasPorCliente(4L).getBody().size());
        assertEquals(1, reservaController.listarReservasPorFecha("2026-08-28").getBody().size());
        assertEquals(1, reservaController.listarReservasPorEstado("PENDIENTE").getBody().size());
    }

    @Test
    void createsReservationAndMapsOptionalStateChangesToHttpStatus() {
        ReservaEntity reserva = ReservaEntity.builder().id(1L).build();
        when(reservaApplicationService.crear(reserva)).thenReturn(reserva);
        when(reservaApplicationService.actualizarEstado(1L, "CONFIRMADA")).thenReturn(Optional.of(reserva));
        when(reservaApplicationService.actualizarEstado(2L, "CONFIRMADA")).thenReturn(Optional.empty());
        when(reservaApplicationService.cancelar(1L)).thenReturn(Optional.of(reserva));
        when(reservaApplicationService.cancelar(2L)).thenReturn(Optional.empty());

        assertEquals(200, reservaController.crearReserva(reserva).getStatusCode().value());
        assertEquals(200, reservaController.actualizarEstado(1L, "CONFIRMADA").getStatusCode().value());
        assertEquals(404, reservaController.actualizarEstado(2L, "CONFIRMADA").getStatusCode().value());
        assertEquals(200, reservaController.cancelarReserva(1L).getStatusCode().value());
        assertEquals(404, reservaController.cancelarReserva(2L).getStatusCode().value());
    }
}