package cl.Barberia.application.reservation;

import cl.Barberia.infrastructure.persistence.entity.ReservaEntity;
import cl.Barberia.infrastructure.persistence.repository.ReservaRepositoryJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaApplicationServiceTest {

    @Mock
    private ReservaRepositoryJpa reservaRepository;

    @InjectMocks
    private ReservaApplicationService reservaService;

    @Test
    void rejectsReservationAtPastTimeToday() {
        ReservaEntity reserva = ReservaEntity.builder()
            .fecha(LocalDate.now())
            .hora(LocalTime.now().minusMinutes(1))
            .build();

        assertThrows(IllegalArgumentException.class, () -> reservaService.crear(reserva));
        verifyNoInteractions(reservaRepository);
    }

    @Test
    void assignsPendingStateAndCodeWhenCreatingReservation() {
        ReservaEntity reserva = ReservaEntity.builder()
            .fecha(LocalDate.now().plusDays(1))
            .hora(LocalTime.of(10, 0))
            .build();
        when(reservaRepository.save(any(ReservaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservaEntity saved = reservaService.crear(reserva);

        assertTrue(saved.getCodigo().startsWith("RES-"));
        assertEquals("PENDIENTE", saved.getEstado());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void delegatesReservationListingsToRepository() {
        when(reservaRepository.findAll()).thenReturn(java.util.List.of());
        when(reservaRepository.findByClienteId(1L)).thenReturn(java.util.List.of());
        when(reservaRepository.findByFecha(LocalDate.now())).thenReturn(java.util.List.of());
        when(reservaRepository.findByEstado("PENDIENTE")).thenReturn(java.util.List.of());

        assertTrue(reservaService.listarTodas().isEmpty());
        assertTrue(reservaService.listarPorCliente(1L).isEmpty());
        assertTrue(reservaService.listarPorFecha(LocalDate.now()).isEmpty());
        assertTrue(reservaService.listarPorEstado("PENDIENTE").isEmpty());
    }

    @Test
    void updatesOrCancelsExistingReservationAndKeepsMissingOneEmpty() {
        ReservaEntity reserva = ReservaEntity.builder().id(1L).estado("PENDIENTE").build();
        when(reservaRepository.findById(1L)).thenReturn(java.util.Optional.of(reserva));
        when(reservaRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        when(reservaRepository.save(any(ReservaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals("CONFIRMADA", reservaService.actualizarEstado(1L, "CONFIRMADA").orElseThrow().getEstado());
        assertEquals("CANCELADA", reservaService.cancelar(1L).orElseThrow().getEstado());
        assertTrue(reservaService.actualizarEstado(2L, "CONFIRMADA").isEmpty());
        assertTrue(reservaService.cancelar(2L).isEmpty());
    }
}
