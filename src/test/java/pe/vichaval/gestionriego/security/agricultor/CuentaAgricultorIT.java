package pe.vichaval.gestionriego.security.agricultor;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.vichaval.gestionriego.agricultor.dto.AgricultorForm;
import pe.vichaval.gestionriego.agricultor.service.AgricultorService;
import pe.vichaval.gestionriego.integration.PostgreSqlIntegrationBase;
import pe.vichaval.gestionriego.security.agricultor.service.CuentaAgricultorService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CuentaAgricultorIT extends PostgreSqlIntegrationBase {
    @Autowired AgricultorService agricultores;
    @Autowired CuentaAgricultorService cuentas;

    @Test
    void creaYAutenticaCuenta() {
        var agricultor = agricultores.save(form("90909090"), null);
        var cuenta = cuentas.crear(agricultor.getId(), "agricultor.90", "ClaveSegura-2026");
        assertNotNull(cuenta.getId());
        assertEquals(agricultor.getId(), cuentas.autenticar("AGRICULTOR.90", "ClaveSegura-2026").getAgricultor().getId());
    }

    @Test
    void rechazaCuentaDuplicadaParaMismoAgricultor() {
        var agricultor = agricultores.save(form("91919191"), null);
        cuentas.crear(agricultor.getId(), "agricultor.91", "ClaveSegura-2026");
        assertThrows(IllegalStateException.class,
                () -> cuentas.crear(agricultor.getId(), "agricultor.otro", "ClaveSegura-2026"));
    }

    @Test
    void registraIntentoFallido() {
        var agricultor = agricultores.save(form("92929292"), null);
        var cuenta = cuentas.crear(agricultor.getId(), "agricultor.92", "ClaveSegura-2026");
        assertThrows(IllegalArgumentException.class,
                () -> cuentas.autenticar("agricultor.92", "incorrecta"));
        assertEquals(1, cuenta.getIntentosFallidos());
    }

    private AgricultorForm form(String dni) {
        var f = new AgricultorForm();
        f.setDni(dni); f.setNombres("Agricultor"); f.setApellidos("Seguridad");
        f.setTelefono("999999999"); f.setSector("Norte"); f.setEstado("ACTIVO");
        return f;
    }
}
