package cl.Barberia.domain.usermanagement;

import cl.Barberia.domain.authentication.PasswordHash;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    // ===== CONSULTAS =====
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(Long id);
    Optional<PasswordHash> findPasswordHashByUsername(String username);
    Optional<Integer> findIntentosFallidosByUsername(String username);
    Optional<LocalDateTime> findBloqueadoHastaByUsername(String username);
    List<Usuario> findAll();
    List<Usuario> findByRol(String rol);
    
    // ===== GUARDAR =====
    Usuario save(Usuario usuario, String passwordPlain);
    
    // ===== ELIMINAR =====
    void deleteById(Long id);
    
    // ===== ACTUALIZACIONES =====
    void actualizarEmail(Long id, String email);
    void actualizarNombreCompleto(Long id, String nombreCompleto);
    void actualizarRol(Long id, String rol);
    void actualizarActivo(Long id, Boolean activo);
    void actualizarPassword(Long id, String passwordHash);
    void incrementarIntentosFallidos(Long userId);
    void actualizarIntentosFallidos(Long userId, Integer intentos, LocalDateTime bloqueadoHasta);
}