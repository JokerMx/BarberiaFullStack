package cl.Barberia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication //declara webapplication
@ComponentScan(basePackages = "cl.Barberia")  // ← Escanea TODO el proyecto
public class BarberiaApplication {
	public static void main(String[] args) {
		SpringApplication.run(BarberiaApplication.class, args);
		System.out.println("🪒 Barbería API iniciada correctamente");
		System.out.println("📌 Endpoints:");
		System.out.println("   POST /api/auth/login     - Login de usuarios");
		System.out.println("   POST /api/usuarios/registro - Registrar usuario");
		System.out.println("   GET  /api/auth/health    - Health check");
	}
}