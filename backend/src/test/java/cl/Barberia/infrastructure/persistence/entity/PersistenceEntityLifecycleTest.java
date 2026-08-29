package cl.Barberia.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PersistenceEntityLifecycleTest {

    @Test
    void initializesAndUpdatesAuditDatesForAllPersistentEntities() {
        UsuarioEntity usuario = new UsuarioEntity();
        ReservaEntity reserva = new ReservaEntity();
        ServicioEntity servicio = new ServicioEntity();

        usuario.onCreate(); reserva.onCreate(); servicio.onCreate();
        assertNotNull(usuario.getFechaCreacion());
        assertNotNull(reserva.getFechaCreacion());
        assertNotNull(servicio.getFechaCreacion());

        usuario.onUpdate(); reserva.onUpdate(); servicio.onUpdate();
        assertNotNull(usuario.getFechaActualizacion());
        assertNotNull(reserva.getFechaActualizacion());
        assertNotNull(servicio.getFechaActualizacion());
    }
}