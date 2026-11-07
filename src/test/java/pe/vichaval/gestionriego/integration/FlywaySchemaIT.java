package pe.vichaval.gestionriego.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FlywaySchemaIT extends PostgreSqlIntegrationBase {
    @Autowired
    DataSource dataSource;

    @Test
    void aplicaMigracionesV1aV8EnPostgreSqlVacio() throws Exception {
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            try (ResultSet rs = s.executeQuery("select count(*) from flyway_schema_history where success=true")) {
                rs.next();
                assertEquals(8, rs.getInt(1));
            }
            try (ResultSet rs = s.executeQuery("select to_regclass('public.auditoria_eventos'), to_regclass('public.cierres_caja')")) {
                rs.next();
                assertNotNull(rs.getString(1));
                assertNotNull(rs.getString(2));
            }
            try (ResultSet rs = s.executeQuery("select count(*) from pg_class where relkind='S' and relname in ('seq_pago_codigo','seq_cuenta_codigo','seq_egreso_codigo','seq_devolucion_codigo')")) {
                rs.next();
                assertEquals(4, rs.getInt(1));
            }
        }
    }
}
