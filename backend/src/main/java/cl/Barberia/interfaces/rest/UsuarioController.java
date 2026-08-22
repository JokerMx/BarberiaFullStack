package cl.Barberia.interfaces.rest;

import cl.Barberia.application.usermanagement.*;
import cl.Barberia.application.usermanagement.DTOs.ActualizarUsuarioRequest;
import cl.Barberia.application.usermanagement.DTOs.RegistroUsuarioRequest;
import cl.Barberia.application.usermanagement.DTOs.UsuarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final RegistrarUsuarioService registrarUsuarioService;
    private final ListarUsuariosService listarUsuariosService;
    private final ObtenerUsuarioService obtenerUsuarioService;
    private final ActualizarUsuarioService actualizarUsuarioService;
    private final EliminarUsuarioService eliminarUsuarioService;

    // ===== REGISTRAR =====
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody RegistroUsuarioRequest request) {
        registrarUsuarioService.registrar(
            request.getUsername(),
            request.getEmail(),
            request.getNombreCompleto(),
            request.getPassword(),
            request.getRol()
        );
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    // ===== LISTAR TODOS =====
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(listarUsuariosService.listarTodos());
    }

    // ===== LISTAR POR ROL =====
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponse>> listarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(listarUsuariosService.listarPorRol(rol));
    }

    // ===== OBTENER POR ID =====
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerUsuarioService.obtenerPorId(id));
    }

    // ===== OBTENER POR USERNAME =====
    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioResponse> obtenerPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(obtenerUsuarioService.obtenerPorUsername(username));
    }

    // ===== OBTENER POR EMAIL (NUEVO) =====
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(obtenerUsuarioService.obtenerPorEmail(email));
    }

    // ===== ACTUALIZAR =====
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(actualizarUsuarioService.actualizar(id, request));
    }

    // ===== ELIMINAR =====
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        eliminarUsuarioService.eliminar(id);
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }
}