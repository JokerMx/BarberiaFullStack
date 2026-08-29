package cl.Barberia.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HomeControllerTest {

    @Test
    void exposesApplicationMetadataAndEndpointsToHomeTemplate() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = new HomeController().home(model);

        assertEquals("home", view);
        assertEquals("online", model.getAttribute("status"));
        assertTrue(((java.util.Map<?, ?>) model.getAttribute("endpoints")).containsKey("👤 Usuarios"));
        assertEquals("PostgreSQL", ((java.util.Map<?, ?>) model.getAttribute("tecnologias")).get("database"));
    }
}