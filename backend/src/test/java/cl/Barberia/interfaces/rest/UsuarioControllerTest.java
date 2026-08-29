package cl.Barberia.interfaces.rest;

import cl.Barberia.application.usermanagement.ActualizarUsuarioService;
import cl.Barberia.application.usermanagement.DTOs.ActualizarUsuarioRequest;
import cl.Barberia.application.usermanagement.DTOs.RegistroUsuarioRequest;
import cl.Barberia.application.usermanagement.DTOs.UsuarioResponse;
import cl.Barberia.application.usermanagement.EliminarUsuarioService;
import cl.Barberia.application.usermanagement.ListarUsuariosService;
import cl.Barberia.application.usermanagement.ObtenerUsuarioService;
import cl.Barberia.application.usermanagement.RegistrarUsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock private RegistrarUsuarioService registrarUsuarioService;
    @Mock private ListarUsuariosService listarUsuariosService;
    @Mock private ObtenerUsuarioService obtenerUsuarioService;
    @Mock private ActualizarUsuarioService actualizarUsuarioService;
    @Mock private EliminarUsuarioService eliminarUsuarioService;
    @InjectMocks private UsuarioController usuarioController;

    @Test
    void registersUserAndReturnsSuccess() {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest();
        request.setUsername("cliente"); request.setEmail("cliente@example.com");
        request.setNombreCompleto("Cliente Prueba"); request.setPassword("secret123"); request.setRol("CLIENTE");

        var response = usuarioController.registrar(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("true", response.getBody().get("success"));
        verify(registrarUsuarioService).registrar("cliente", "cliente@example.com", "Cliente Prueba", "secret123", "CLIENTE");
    }

    @Test
    void delegatesReadUpdateAndDeleteEndpoints() {
        UsuarioResponse response = UsuarioResponse.builder().id(1L).username("cliente").build();
        when(listarUsuariosService.listarTodos()).thenReturn(List.of(response));
        when(listarUsuariosService.listarPorRol("CLIENTE")).thenReturn(List.of(response));
        when(obtenerUsuarioService.obtenerPorId(1L)).thenReturn(response);
        when(obtenerUsuarioService.obtenerPorUsername("cliente")).thenReturn(response);
        when(obtenerUsuarioService.obtenerPorEmail("cliente@example.com")).thenReturn(response);
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest();
        when(actualizarUsuarioService.actualizar(1L, request)).thenReturn(response);

        assertEquals(1, usuarioController.listarTodos().getBody().size());
        assertEquals(1, usuarioController.listarPorRol("CLIENTE").getBody().size());
        assertEquals(response, usuarioController.obtenerPorId(1L).getBody());
        assertEquals(response, usuarioController.obtenerPorUsername("cliente").getBody());
        assertEquals(response, usuarioController.obtenerPorEmail("cliente@example.com").getBody());
        assertEquals(response, usuarioController.actualizar(1L, request).getBody());
        assertEquals(200, usuarioController.eliminar(1L).getStatusCode().value());
        verify(eliminarUsuarioService).eliminar(1L);
    }
}