package cl.Barberia.infrastructure.persistence.adapter;

import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.infrastructure.persistence.entity.UsuarioEntity;
import cl.Barberia.infrastructure.persistence.repository.UsuarioRepositoryJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryAdapterTest {

    @Mock private UsuarioRepositoryJpa jpaRepository;
    @InjectMocks private UsuarioRepositoryAdapter adapter;

    @Test
    void mapsAllUserLookupVariantsAndLists() {
        UsuarioEntity entity = entity(1L, "cliente", "cliente@example.com");
        when(jpaRepository.findByUsername("cliente")).thenReturn(Optional.of(entity));
        when(jpaRepository.findByEmail("cliente@example.com")).thenReturn(Optional.of(entity));
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        when(jpaRepository.findByRol("CLIENTE")).thenReturn(List.of(entity));

        assertEquals("cliente", adapter.findByUsername("cliente").orElseThrow().getUsername());
        assertEquals("cliente@example.com", adapter.findByEmail("cliente@example.com").orElseThrow().getEmail());
        assertEquals(1L, adapter.findById(1L).orElseThrow().getId());
        assertEquals(1, adapter.findAll().size());
        assertEquals(1, adapter.findByRol("CLIENTE").size());
    }

    @Test
    void preservesEmptyLookupsAndReadsSecurityFields() {
        LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(30);
        when(jpaRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(jpaRepository.findPasswordHashByUsername("cliente")).thenReturn(Optional.of("$2a$10$7EqJtq98hPqEX7fNZaFWoO7Fjl.CGAYgZx7XYy1TbZi5IC3wN3h7e"));
        when(jpaRepository.findIntentosFallidosByUsername("cliente")).thenReturn(Optional.of(2));
        when(jpaRepository.findBloqueadoHastaByUsername("cliente")).thenReturn(Optional.of(lockedUntil));

        assertTrue(adapter.findByUsername("missing").isEmpty());
        assertTrue(adapter.findPasswordHashByUsername("cliente").isPresent());
        assertEquals(2, adapter.findIntentosFallidosByUsername("cliente").orElseThrow());
        assertEquals(lockedUntil, adapter.findBloqueadoHastaByUsername("cliente").orElseThrow());
    }

    @Test
    void savesDomainUserWithHashedPassword() {
        Usuario domainUser = Usuario.builder().id(1L).username("cliente").email("cliente@example.com")
            .nombreCompleto("Cliente Prueba").rol(Rol.CLIENTE).activo(true).build();
        when(jpaRepository.save(org.mockito.ArgumentMatchers.any(UsuarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario saved = adapter.save(domainUser, "secret123");

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertEquals("cliente", saved.getUsername());
        assertFalse("secret123".equals(captor.getValue().getPasswordHash()));
    }

    @Test
    void delegatesDeletionAndAllUpdates() {
        LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(30);
        adapter.deleteById(1L); adapter.actualizarEmail(1L, "new@example.com");
        adapter.actualizarNombreCompleto(1L, "Nuevo Cliente"); adapter.actualizarRol(1L, "BARBERO");
        adapter.actualizarActivo(1L, false); adapter.actualizarPassword(1L, "hash");
        adapter.incrementarIntentosFallidos(1L); adapter.actualizarIntentosFallidos(1L, 5, lockedUntil);

        verify(jpaRepository).deleteById(1L); verify(jpaRepository).actualizarEmail(1L, "new@example.com");
        verify(jpaRepository).actualizarNombreCompleto(1L, "Nuevo Cliente"); verify(jpaRepository).actualizarRol(1L, "BARBERO");
        verify(jpaRepository).actualizarActivo(1L, false); verify(jpaRepository).actualizarPassword(1L, "hash");
        verify(jpaRepository).incrementarIntentosFallidos(1L); verify(jpaRepository).actualizarIntentosFallidos(1L, 5, lockedUntil);
    }

    private UsuarioEntity entity(Long id, String username, String email) {
        return UsuarioEntity.builder().id(id).username(username).email(email).passwordHash("hash")
            .nombreCompleto("Cliente Prueba").rol("CLIENTE").activo(true).fechaCreacion(LocalDateTime.now()).build();
    }
}