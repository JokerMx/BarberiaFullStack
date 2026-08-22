package cl.Barberia.infrastructure.persistence.adapter;

import cl.Barberia.domain.authentication.PasswordHash;
import cl.Barberia.domain.usermanagement.Rol;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import cl.Barberia.infrastructure.persistence.entity.UsuarioEntity;
import cl.Barberia.infrastructure.persistence.repository.UsuarioRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioRepositoryJpa jpaRepository;

    // ===== CONSULTAS =====
    @Override
    public Optional<Usuario> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
            .map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public Optional<PasswordHash> findPasswordHashByUsername(String username) {
        return jpaRepository.findPasswordHashByUsername(username)
            .map(PasswordHash::fromHash);
    }

    @Override
    public Optional<Integer> findIntentosFallidosByUsername(String username) {
        return jpaRepository.findIntentosFallidosByUsername(username);
    }

    @Override
    public Optional<LocalDateTime> findBloqueadoHastaByUsername(String username) {
        return jpaRepository.findBloqueadoHastaByUsername(username);
    }

    @Override
    public List<Usuario> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> findByRol(String rol) {
        return jpaRepository.findByRol(rol).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    // ===== GUARDAR =====
    @Override
    public Usuario save(Usuario usuario, String passwordPlain) {
        UsuarioEntity entity = UsuarioEntity.builder()
            .username(usuario.getUsername())
            .email(usuario.getEmail())
            .passwordHash(PasswordHash.fromPlainPassword(passwordPlain).getValue())
            .nombreCompleto(usuario.getNombreCompleto())
            .rol(usuario.getRol().name())
            .activo(usuario.isActivo())
            .build();
        
        UsuarioEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    // ===== ELIMINAR =====
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    // ===== ACTUALIZACIONES =====
    @Override
    public void actualizarEmail(Long id, String email) {
        jpaRepository.actualizarEmail(id, email);
    }

    @Override
    public void actualizarNombreCompleto(Long id, String nombreCompleto) {
        jpaRepository.actualizarNombreCompleto(id, nombreCompleto);
    }

    @Override
    public void actualizarRol(Long id, String rol) {
        jpaRepository.actualizarRol(id, rol);
    }

    @Override
    public void actualizarActivo(Long id, Boolean activo) {
        jpaRepository.actualizarActivo(id, activo);
    }

    @Override
    public void actualizarPassword(Long id, String passwordHash) {
        jpaRepository.actualizarPassword(id, passwordHash);
    }

    @Override
    public void incrementarIntentosFallidos(Long userId) {
        jpaRepository.incrementarIntentosFallidos(userId);
    }

    @Override
    public void actualizarIntentosFallidos(Long userId, Integer intentos, LocalDateTime bloqueadoHasta) {
        jpaRepository.actualizarIntentosFallidos(userId, intentos, bloqueadoHasta);
    }

    // ===== MAPEO =====
    private Usuario toDomain(UsuarioEntity entity) {
        return Usuario.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .email(entity.getEmail())
            .nombreCompleto(entity.getNombreCompleto())
            .rol(Rol.fromString(entity.getRol()))
            .activo(entity.getActivo())
            .fechaCreacion(entity.getFechaCreacion())
            .fechaActualizacion(entity.getFechaActualizacion())
            .build();
    }
}