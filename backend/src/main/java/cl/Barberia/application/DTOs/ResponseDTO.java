package cl.Barberia.application.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private boolean success;
    private String mensaje;
    private Map<String, Object> data;

    public static ResponseDTO success(String mensaje) {
        return ResponseDTO.builder()
            .success(true)
            .mensaje(mensaje)
            .build();
    }

    public static ResponseDTO success(String mensaje, Map<String, Object> data) {
        return ResponseDTO.builder()
            .success(true)
            .mensaje(mensaje)
            .data(data)
            .build();
    }

    public static ResponseDTO error(String mensaje) {
        return ResponseDTO.builder()
            .success(false)
            .mensaje(mensaje)
            .build();
    }

    public static ResponseDTO error(String mensaje, Map<String, Object> data) {
        return ResponseDTO.builder()
            .success(false)
            .mensaje(mensaje)
            .data(data)
            .build();
    }
}