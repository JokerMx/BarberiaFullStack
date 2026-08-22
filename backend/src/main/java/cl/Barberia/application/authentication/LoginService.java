package cl.Barberia.application.authentication;

import cl.Barberia.domain.authentication.PasswordHash;
import cl.Barberia.domain.authentication.UsuarioAutenticado;
import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.usermanagement.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticado autenticar(String login, String plainPassword) {
        // ===== VALIDAR QUE EL LOGIN NO SEA NULO O VACÍO =====
        if (login == null || login.isBlank()) {
            throw new CredencialesInvalidasException("Usuario o email no proporcionado");
        }

        // ===== BUSCAR POR USERNAME O EMAIL =====
        // Primero intentar por username
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(login);
        
        // Si no se encuentra, buscar por email
        if (usuarioOpt.isEmpty()) {
            usuarioOpt = usuarioRepository.findByEmail(login);
        }
        
        // Si no se encuentra, lanzar excepción
        Usuario usuario = usuarioOpt
            .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));

        // ===== VERIFICAR QUE EL USUARIO ESTÁ ACTIVO =====
        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException("Usuario desactivado");
        }

        // ===== OBTENER EL HASH DE LA CONTRASEÑA =====
        PasswordHash passwordHash = usuarioRepository.findPasswordHashByUsername(usuario.getUsername())
            .orElseThrow(() -> new CredencialesInvalidasException("Credenciales inválidas"));

        // ===== CREAR AGREGADO Y AUTENTICAR =====
        UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(usuario, passwordHash);
        
        try {
            usuarioAutenticado.autenticar(plainPassword);
            
            // Reiniciar intentos fallidos si el login es exitoso
            usuarioRepository.actualizarIntentosFallidos(usuario.getId(), 0, null);
            
            return usuarioAutenticado;
            
        } catch (CuentaBloqueadaException e) {
            usuarioRepository.actualizarIntentosFallidos(usuario.getId(), 5, LocalDateTime.now().plusMinutes(30));
            throw e;
            
        } catch (CredencialesInvalidasException e) {
            usuarioRepository.incrementarIntentosFallidos(usuario.getId());
            throw e;
        }
    }
}