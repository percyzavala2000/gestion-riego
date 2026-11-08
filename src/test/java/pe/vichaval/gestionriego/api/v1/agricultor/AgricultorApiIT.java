package pe.vichaval.gestionriego.api.v1.agricultor;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService;
import pe.vichaval.gestionriego.integration.PostgreSqlIntegrationBase;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AgricultorApiIT extends PostgreSqlIntegrationBase {
    @Autowired MockMvc mvc;
    @Autowired AgricultorService service;

    @Test
    void creaAgricultorCon201LocationYCuerpo() throws Exception {
        mvc.perform(post("/api/v1/agricultores")
                        .with(user("operador").roles("OPERADOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"87654321","nombres":"Ana","apellidos":"Prueba","telefono":"999999999","sector":"Norte","estado":"ACTIVO"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/api/v1/agricultores/[0-9]+$")))
                .andExpect(jsonPath("$.dni").value("87654321"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void rechazaDniDuplicadoCon409() throws Exception {
        service.save(form("11223344"), null);
        mvc.perform(post("/api/v1/agricultores")
                        .with(user("operador").roles("OPERADOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dni":"11223344","nombres":"Otro","apellidos":"Registro","telefono":"999999999","sector":"Sur","estado":"ACTIVO"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflicto de negocio"));
    }

    @Test
    void rechazaPaginaNegativaCon400() throws Exception {
        mvc.perform(get("/api/v1/agricultores?page=-1")
                        .with(user("operador").roles("OPERADOR")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambioExplicitoDeEstadoEsIdempotente() throws Exception {
        var agricultor = service.save(form("44332211"), null);
        String ruta = "/api/v1/agricultores/" + agricultor.getId() + "/estado";
        String cuerpo = "{\"estado\":\"INACTIVO\"}";

        mvc.perform(patch(ruta).with(user("operador").roles("OPERADOR"))
                        .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("INACTIVO"));

        mvc.perform(patch(ruta).with(user("operador").roles("OPERADOR"))
                        .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("INACTIVO"));
    }

    @Test
    void rechazaEstadoNoPermitido() throws Exception {
        var agricultor = service.save(form("55667788"), null);
        mvc.perform(patch("/api/v1/agricultores/{id}/estado", agricultor.getId())
                        .with(user("operador").roles("OPERADOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"BLOQUEADO\"}"))
                .andExpect(status().isBadRequest());
    }

    private AgricultorForm form(String dni) {
        var form = new AgricultorForm();
        form.setDni(dni);
        form.setNombres("Agricultor");
        form.setApellidos("Prueba");
        form.setTelefono("999999999");
        form.setSector("Norte");
        form.setEstado("ACTIVO");
        return form;
    }
}
