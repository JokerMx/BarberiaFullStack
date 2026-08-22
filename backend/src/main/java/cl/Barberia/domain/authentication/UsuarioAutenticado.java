package cl.Barberia.domain.authentication;

import cl.Barberia.domain.usermanagement.Usuario;
import cl.Barberia.domain.authentication.exceptions.CredencialesInvalidasException;
import cl.Barberia.domain.authentication.exceptions.CuentaBloqueadaException;
import lombok.Getter;

@Getter
public class UsuarioAutenticado {
    private final Usuario usuario;
    private final Username username;
    private final PasswordHash passwordHash;
    private final IntentosFallidos intentosFallidos;

    public UsuarioAutenticado(Usuario usuario, PasswordHash passwordHash) {
        this.usuario = usuario;
        this.username = new Username(usuario.getUsername());
        this.passwordHash = passwordHash;
        this.intentosFallidos = new IntentosFallidos();
    }

    public boolean autenticar(String plainPassword) {
        if (intentosFallidos.estaBloqueado()) {
            throw new CuentaBloqueadaException("Cuenta bloqueada temporalmente");
        }

        boolean esValida = passwordHash.verificar(plainPassword);

        if (esValida) {
            intentosFallidos.resetear();
            return true;
        } else {
            intentosFallidos.registrarIntentoFallido();
            throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
        }
    }
}