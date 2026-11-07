package pe.vichaval.gestionriego.api.v1;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pe.vichaval.gestionriego.integration.PostgreSqlIntegrationBase;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiAccessIT extends PostgreSqlIntegrationBase {
    @Autowired
    MockMvc mvc;

    @Test
    void apiRequiereAutenticacion() throws Exception {
        mvc.perform(get("/api/v1/agricultores")).andExpect(status().is3xxRedirection());
    }

    @Test
    void operadorPuedeConsultarYTesoreriaNo() throws Exception {
        mvc.perform(get("/api/v1/agricultores").with(user("operador").roles("OPERADOR"))).andExpect(status().isOk());
        mvc.perform(get("/api/v1/agricultores").with(user("tesoreria").roles("TESORERIA"))).andExpect(status().isForbidden());
    }
}
