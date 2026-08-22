package cl.Barberia.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("dev")  // ← SOLO SE ACTIVA EN PERFIL DEV
public class OpenApiConfig {

    @Bean
    public OpenAPI barberiaOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("🪒 Barbería API")
                .description("API REST para sistema de gestión de barbería con DDD + Hexagonal")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Soporte Barbería")
                    .email("soporte@barberia.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Servidor Local (Desarrollo)")
            ));
    }
}