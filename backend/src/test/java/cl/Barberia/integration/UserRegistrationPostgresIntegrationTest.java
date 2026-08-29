package cl.Barberia.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationPostgresIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("barberia_test")
        .withUsername("barberia")
        .withPassword("barberia");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("springdoc.api-docs.enabled", () -> false);
        registry.add("springdoc.swagger-ui.enabled", () -> false);
        registry.add("app.cors.allowed-origins", () -> "http://localhost:5173");
    }

    @Test
    void persistsRegisteredUserThroughTheHttpApi() throws Exception {
        mockMvc.perform(post("/api/usuarios/registro")
                .contentType(APPLICATION_JSON)
                .content("""
                    {"username":"postgresclient","email":"postgresclient@example.com","nombreCompleto":"Postgres Client","password":"secret123","rol":"CLIENTE"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value("true"));

        Integer persistedUsers = jdbcTemplate.queryForObject(
            "select count(*) from usuarios where username = ?", Integer.class, "postgresclient");

        org.junit.jupiter.api.Assertions.assertEquals(1, persistedUsers);
    }
}