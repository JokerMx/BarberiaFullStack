package cl.Barberia.domain.usermanagement;

import cl.Barberia.domain.authentication.Username;  // ← IMPORT AGREGADO

import java.time.LocalDateTime;

public class Usuario {
    private final Long id;
    private final Username username;
    private final Email email;
    private final NombreCompleto nombreCompleto;
    private final Rol rol;
    private boolean activo;
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    private Usuario(Builder builder) {
        this.id = builder.id;
        this.username = new Username(builder.username);
        this.email = new Email(builder.email);
        this.nombreCompleto = new NombreCompleto(builder.nombreCompleto);
        this.rol = builder.rol;
        this.activo = builder.activo;
        this.fechaCreacion = builder.fechaCreacion != null ? builder.fechaCreacion : LocalDateTime.now();
        this.fechaActualizacion = builder.fechaActualizacion != null ? builder.fechaActualizacion : LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username.getValue(); }
    public String getEmail() { return email.getValue(); }
    public String getNombreCompleto() { return nombreCompleto.getValue(); }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String nombreCompleto;
        private Rol rol;
        private boolean activo = true;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder nombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; return this; }
        public Builder rol(Rol rol) { this.rol = rol; return this; }
        public Builder activo(boolean activo) { this.activo = activo; return this; }
        public Builder fechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; return this; }
        public Builder fechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; return this; }

        public Usuario build() {
            return new Usuario(this);
        }
    }
}