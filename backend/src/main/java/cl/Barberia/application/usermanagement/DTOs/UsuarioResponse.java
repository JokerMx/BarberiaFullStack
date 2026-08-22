package cl.Barberia.application.usermanagement.DTOs;

import cl.Barberia.domain.usermanagement.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private String rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer intentosFallidos;
    private LocalDateTime bloqueadoHasta;

    public static UsuarioResponse fromDomain(Usuario usuario, Integer intentosFallidos, LocalDateTime bloqueadoHasta) {
        return UsuarioResponse.builder()
            .id(usuario.getId())
            .username(usuario.getUsername())
            .email(usuario.getEmail())
            .nombreCompleto(usuario.getNombreCompleto())
            .rol(usuario.getRol().name())
            .activo(usuario.isActivo())
            .fechaCreacion(usuario.getFechaCreacion())
            .fechaActualizacion(usuario.getFechaActualizacion())
            .intentosFallidos(intentosFallidos)
            .bloqueadoHasta(bloqueadoHasta)
            .build();
    }
}