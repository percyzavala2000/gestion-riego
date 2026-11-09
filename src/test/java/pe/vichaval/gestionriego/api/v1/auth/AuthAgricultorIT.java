package pe.vichaval.gestionriego.api.v1.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.*;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService;
import pe.vichaval.gestionriego.integration.PostgreSqlIntegrationBase;
import pe.vichaval.gestionriego.security.agricultor.service.CuentaAgricultorService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthAgricultorIT extends PostgreSqlIntegrationBase {
    @Autowired
    MockMvc mvc;
    @Autowired
    AgricultorService agricultores;
    @Autowired
    CuentaAgricultorService cuentas;
    private final ObjectMapper json = new ObjectMapper();

    @Test
    void loginMeRefreshLogoutYReusoRechazado() throws Exception {
        var a = agricultores.save(form("78787878"), null);
        cuentas.crear(a.getId(), "agricultor.78", "ClaveSegura-2026");
        String body = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"usuario\":\"agricultor.78\",\"password\":\"ClaveSegura-2026\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tokenType").value("Bearer")).andReturn().getResponse().getContentAsString();
        JsonNode n = json.readTree(body);
        String access = n.get("accessToken").asText(), refresh = n.get("refreshToken").asText();
        mvc.perform(get("/api/v1/agricultor/me").header("Authorization", "Bearer " + access)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(a.getId()));
        String rb = mvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\"" + refresh + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        mvc.perform(post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\"" + refresh + "\"}")).andExpect(status().isUnauthorized());
        String nuevoRefresh = json.readTree(rb).get("refreshToken").asText();
        mvc.perform(post("/api/v1/auth/logout").contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\"" + nuevoRefresh + "\"}")).andExpect(status().isNoContent());
    }

    @Test
    void tokenInvalidoNoAccede() {
        Assertions.assertDoesNotThrow(() -> mvc.perform(get("/api/v1/agricultor/me").header("Authorization", "Bearer invalido")).andExpect(status().isUnauthorized()));
    }

    private AgricultorForm form(String dni) {
        var f = new AgricultorForm();
        f.setDni(dni);
        f.setNombres("Agricultor");
        f.setApellidos("Movil");
        f.setTelefono("999999999");
        f.setSector("Norte");
        f.setEstado("ACTIVO");
        return f;
    }
}
