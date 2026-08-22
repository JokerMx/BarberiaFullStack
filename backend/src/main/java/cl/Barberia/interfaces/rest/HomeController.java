package cl.Barberia.interfaces.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Datos para la plantilla
        model.addAttribute("titulo", "🪒 Barbería API");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("status", "online");
        model.addAttribute("descripcion", "Sistema de gestión para barbería");

        // Endpoints organizados por categoría
        Map<String, Map<String, String>> endpoints = new LinkedHashMap<>();

        Map<String, String> auth = new LinkedHashMap<>();
        auth.put("POST /api/auth/login", "Iniciar sesión (username + password)");
        auth.put("GET  /api/auth/health", "Health check");
        endpoints.put("🔐 Autenticación", auth);

        Map<String, String> usuarios = new LinkedHashMap<>();
        usuarios.put("POST /api/usuarios/registro", "Registrar nuevo usuario");
        usuarios.put("GET  /api/usuarios", "Listar todos los usuarios");
        usuarios.put("GET  /api/usuarios/{id}", "Obtener usuario por ID");
        usuarios.put("PUT  /api/usuarios/{id}", "Actualizar usuario");
        usuarios.put("DELETE /api/usuarios/{id}", "Eliminar usuario");
        usuarios.put("GET  /api/usuarios/rol/{rol}", "Listar usuarios por rol");
        usuarios.put("GET  /api/usuarios/username/{username}", "Buscar usuario por username");
        endpoints.put("👤 Usuarios", usuarios);

        model.addAttribute("endpoints", endpoints);

        Map<String, String> tecnologias = new LinkedHashMap<>();
        tecnologias.put("framework", "Spring Boot 3.2.8");
        tecnologias.put("java", "21");
        tecnologias.put("database", "PostgreSQL");
        tecnologias.put("arquitectura", "DDD + Hexagonal");
        model.addAttribute("tecnologias", tecnologias);

        return "home";
    }
}