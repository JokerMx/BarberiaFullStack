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
}
