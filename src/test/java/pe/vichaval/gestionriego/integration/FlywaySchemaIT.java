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
    void aplicaMigracionesV1aV12EnPostgreSqlVacio() throws Exception {
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            try (ResultSet rs = s.executeQuery("select count(*) from flyway_schema_history where success=true")) {
                rs.next();
                assertEquals(12, rs.getInt(1));
            }
            try (ResultSet rs = s.executeQuery("select to_regclass('public.auditoria_eventos'), to_regclass('public.cierres_caja'), to_regclass('public.cuentas_agricultor'), to_regclass('public.sesiones_agricultor'), to_regclass('public.notificaciones')")) {
                rs.next();
                assertNotNull(rs.getString(1));
                assertNotNull(rs.getString(2));
                assertNotNull(rs.getString(3));
                assertNotNull(rs.getString(4));
                assertNotNull(rs.getString(5));
            }
            try (ResultSet rs = s.executeQuery("select count(*) from pg_class where relkind='S' and relname in ('seq_pago_codigo','seq_cuenta_codigo','seq_egreso_codigo','seq_devolucion_codigo')")) {
                rs.next();
                assertEquals(4, rs.getInt(1));
            }
        }
    }
}
