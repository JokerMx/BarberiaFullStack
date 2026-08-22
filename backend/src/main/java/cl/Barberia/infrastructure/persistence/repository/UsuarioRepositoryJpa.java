package cl.Barberia.infrastructure.persistence.repository;

import cl.Barberia.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepositoryJpa extends JpaRepository<UsuarioEntity, Long> {

    // ===== CONSULTAS =====
    Optional<UsuarioEntity> findByUsername(String username);
    Optional<UsuarioEntity> findByEmail(String email);
    List<UsuarioEntity> findByRol(String rol);

    @Query("SELECT u.passwordHash FROM UsuarioEntity u WHERE u.username = :username")
    Optional<String> findPasswordHashByUsername(@Param("username") String username);

    @Query("SELECT u.intentosFallidos FROM UsuarioEntity u WHERE u.username = :username")
    Optional<Integer> findIntentosFallidosByUsername(@Param("username") String username);

    @Query("SELECT u.bloqueadoHasta FROM UsuarioEntity u WHERE u.username = :username")
    Optional<LocalDateTime> findBloqueadoHastaByUsername(@Param("username") String username);

    // ===== ACTUALIZACIONES =====
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.email = :email WHERE u.id = :id")
    void actualizarEmail(@Param("id") Long id, @Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.nombreCompleto = :nombreCompleto WHERE u.id = :id")
    void actualizarNombreCompleto(@Param("id") Long id, @Param("nombreCompleto") String nombreCompleto);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.rol = :rol WHERE u.id = :id")
    void actualizarRol(@Param("id") Long id, @Param("rol") String rol);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.activo = :activo WHERE u.id = :id")
    void actualizarActivo(@Param("id") Long id, @Param("activo") Boolean activo);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.passwordHash = :passwordHash WHERE u.id = :id")
    void actualizarPassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.intentosFallidos = u.intentosFallidos + 1 WHERE u.id = :id")
    void incrementarIntentosFallidos(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.intentosFallidos = :intentos, u.bloqueadoHasta = :bloqueadoHasta WHERE u.id = :id")
    void actualizarIntentosFallidos(@Param("id") Long id,
                                   @Param("intentos") Integer intentos,
                                   @Param("bloqueadoHasta") LocalDateTime bloqueadoHasta);
}