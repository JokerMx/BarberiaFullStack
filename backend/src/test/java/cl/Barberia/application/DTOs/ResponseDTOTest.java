package cl.Barberia.application.DTOs;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ResponseDTOTest {
    @Test void createsSuccessAndErrorWithOptionalData() {
        assertTrue(ResponseDTO.success("ok").isSuccess());
        assertEquals(1, ResponseDTO.success("ok", Map.of("id", 1)).getData().get("id"));
        assertFalse(ResponseDTO.error("error").isSuccess());
        assertEquals("value", ResponseDTO.error("error", Map.of("key", "value")).getData().get("key"));
    }
}