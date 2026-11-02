package pe.vichaval.gestionriego.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAccessIT extends PostgreSqlIntegrationBase {
    @Autowired MockMvc mvc;

    @Test void anonimoEsRedirigidoAlLogin() throws Exception {
        mvc.perform(get("/auditoria")).andExpect(status().is3xxRedirection());
    }
    @Test void administradorSuperaLaCapaDeAutorizacion() throws Exception {
        mvc.perform(get("/auditoria/ruta-inexistente").with(user("admin").roles("ADMINISTRADOR")))
                .andExpect(status().isNotFound());
    }
    @Test void operadorNoAccedeAAuditoria() throws Exception {
        mvc.perform(get("/auditoria").with(user("operador").roles("OPERADOR")))
                .andExpect(status().isForbidden());
    }
    @Test void tesoreriaSuperaAutorizacionFinancieraYOperadorNo() throws Exception {
        mvc.perform(get("/tesoreria/ruta-inexistente").with(user("tesoreria").roles("TESORERIA")))
                .andExpect(status().isNotFound());
        mvc.perform(get("/tesoreria/ruta-inexistente").with(user("operador").roles("OPERADOR")))
                .andExpect(status().isForbidden());
    }
}
