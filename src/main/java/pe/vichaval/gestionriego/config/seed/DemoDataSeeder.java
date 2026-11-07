package pe.vichaval.gestionriego.config.seed;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("demo")
public class DemoDataSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;
    private final Environment environment;

    public DemoDataSeeder(JdbcTemplate jdbc, PasswordEncoder encoder, Environment environment) {
        this.jdbc = jdbc;
        this.encoder = encoder;
        this.environment = environment;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        DemoSeedGuard.validar(environment.getActiveProfiles());
        Integer existentes = jdbc.queryForObject("select count(*) from usuarios where username='demo_admin'", Integer.class);
        if (existentes != null && existentes > 0) {
            log.info("Los datos demo ya existen; no se insertaron duplicados");
            return;
        }
        log.info("Iniciando carga idempotente de datos de demostración");
        Long admin = usuario("demo_admin", "Administrador", "Demostración", "ADMINISTRADOR");
        usuario("demo_operador", "Operador", "Demostración", "OPERADOR");
        usuario("demo_tesoreria", "Tesorería", "Demostración", "TESORERIA");

        Long[] agricultores = {
                agricultor("90000001", "Adriana", "Campos Demo", "999000001", "Norte"),
                agricultor("90000002", "Bruno", "Ríos Demo", "999000002", "Centro"),
                agricultor("90000003", "Claudia", "Vega Demo", "999000003", "Sur"),
                agricultor("90000004", "Diego", "Paredes Demo", "999000004", "Norte"),
                agricultor("90000005", "Elena", "Torres Demo", "999000005", "Centro")
        };
        Long p1 = parcela("DEMO-PAR-001", "Lote Limón", new BigDecimal("2.50"), "Limón", agricultores[0]);
        Long p2 = parcela("DEMO-PAR-002", "Lote Maíz", new BigDecimal("4.00"), "Maíz", agricultores[1]);
        Long p3 = parcela("DEMO-PAR-003", "Lote Arroz", new BigDecimal("5.20"), "Arroz", agricultores[2]);
        Long p4 = parcela("DEMO-PAR-004", "Lote Mango", new BigDecimal("3.10"), "Mango", agricultores[3]);
        parcela("DEMO-PAR-005", "Lote Plátano", new BigDecimal("1.80"), "Plátano", agricultores[4]);

        LocalDate hoy = LocalDate.now();
        turno("DEMO-TR-001", hoy.minusDays(3), LocalTime.of(7, 0), LocalTime.of(8, 0), "VENCIDO", p1);
        turno("DEMO-TR-002", hoy.minusDays(2), LocalTime.of(9, 0), LocalTime.of(10, 0), "VENCIDO", p2);
        turno("DEMO-TR-003", hoy.plusDays(1), LocalTime.of(6, 0), LocalTime.of(7, 0), "PROGRAMADO", p3);
        turno("DEMO-TR-004", hoy.plusDays(2), LocalTime.of(6, 0), LocalTime.of(7, 0), "PROGRAMADO", p3);
        turno("DEMO-TR-005", hoy.plusDays(3), LocalTime.of(6, 0), LocalTime.of(7, 0), "PROGRAMADO", p3);
        turno("DEMO-TR-006", hoy, LocalTime.of(8, 0), LocalTime.of(9, 0), "PROGRAMADO", p1);
        turno("DEMO-TR-007", hoy, LocalTime.of(10, 0), LocalTime.of(11, 0), "PROGRAMADO", p2);
        turno("DEMO-TR-008", hoy, LocalTime.of(12, 0), LocalTime.of(13, 0), "PROGRAMADO", p4);
        turno("DEMO-TR-009", hoy, LocalTime.of(14, 0), LocalTime.of(15, 0), "PROGRAMADO", p3);

        Long conceptoRiego = jdbc.queryForObject("select id from conceptos_cobro where codigo='RIEGO'", Long.class);
        Long conceptoMant = jdbc.queryForObject("select id from conceptos_cobro where codigo='MANTENIMIENTO'", Long.class);
        Long c1 = cuenta(agricultores[0], conceptoRiego, new BigDecimal("500"), BigDecimal.ZERO, hoy.minusDays(40), hoy.minusDays(10), "VENCIDA");
        Long c2 = cuenta(agricultores[1], conceptoMant, new BigDecimal("400"), new BigDecimal("150"), hoy.minusDays(15), hoy.plusDays(15), "PAGO_PARCIAL");
        Long c3 = cuenta(agricultores[2], conceptoRiego, new BigDecimal("300"), new BigDecimal("300"), hoy.minusDays(10), hoy.plusDays(20), "PAGADA");
        cuenta(agricultores[3], conceptoRiego, new BigDecimal("250"), BigDecimal.ZERO, hoy, hoy.plusDays(30), "PENDIENTE");

        pago(agricultores[1], c2, new BigDecimal("150"), hoy.minusDays(2), "EFECTIVO", "DEMO-COMP-001", "CONFIRMADO");
        pago(agricultores[2], c3, new BigDecimal("300"), hoy.minusDays(1), "EFECTIVO", "DEMO-COMP-002", "CONFIRMADO");
        pago(agricultores[0], c1, new BigDecimal("100"), hoy, "YAPE", "DEMO-COMP-003", "PENDIENTE");
        Long pagoDev = pago(agricultores[4], null, new BigDecimal("180"), hoy.minusDays(1), "TRANSFERENCIA", "DEMO-COMP-004", "CONFIRMADO");

        egreso(admin, hoy.minusDays(1), "Mantenimiento preventivo", new BigDecimal("90"), "DEMO-EGR-001");
        egreso(admin, hoy, "Compra de materiales", new BigDecimal("65"), "DEMO-EGR-002");
        String codigoDev = jdbc.queryForObject("select 'DV-'||lpad(nextval('seq_devolucion_codigo')::text,6,'0')", String.class);
        jdbc.update("insert into devoluciones(codigo,pago_id,monto,motivo,estado,fecha,usuario_id) values(?,?,?,?,?,?,?)",
                codigoDev, pagoDev, new BigDecimal("30"), "Solicitud demo pendiente", "SOLICITADA", Date.valueOf(hoy), admin);
        jdbc.update("insert into cierres_caja(fecha,saldo_inicial,ingresos,egresos,devoluciones,saldo_teorico,saldo_contado,diferencia,observaciones,estado,usuario_id) values(?,?,?,?,?,?,?,?,?,?,?)",
                Date.valueOf(hoy.minusDays(1)), new BigDecimal("100"), new BigDecimal("480"), new BigDecimal("90"), BigDecimal.ZERO, new BigDecimal("490"), new BigDecimal("430"), new BigDecimal("-60"), "Cierre demo con diferencia", "CERRADO", admin);
        log.info("Datos demo cargados. Usuarios: demo_admin, demo_operador y demo_tesoreria");
    }

    private Long usuario(String username, String nombres, String apellidos, String rol) {
        return jdbc.queryForObject("insert into usuarios(username,password_hash,nombres,apellidos,activo,rol_id) values(?,?,?,?,true,(select id from roles where codigo=?)) returning id", Long.class, username, encoder.encode("Demo2026!"), nombres, apellidos, rol);
    }

    private Long agricultor(String dni, String nombres, String apellidos, String telefono, String sector) {
        return jdbc.queryForObject("insert into agricultores(dni,nombres,apellidos,telefono,sector,estado) values(?,?,?,?,?,'ACTIVO') returning id", Long.class, dni, nombres, apellidos, telefono, sector);
    }

    private Long parcela(String codigo, String nombre, BigDecimal ha, String cultivo, Long agricultor) {
        return jdbc.queryForObject("insert into parcelas(codigo,nombre,hectareas,cultivo,estado,agricultor_id) values(?,?,?,?,'ACTIVA',?) returning id", Long.class, codigo, nombre, ha, cultivo, agricultor);
    }

    private void turno(String codigo, LocalDate fecha, LocalTime inicio, LocalTime fin, String estado, Long parcela) {
        jdbc.update("insert into turnos_riego(codigo,fecha,hora_inicio,hora_fin,estado,observaciones,parcela_id) values(?,?,?,?,?,'Dato ficticio demo',?)", codigo, Date.valueOf(fecha), inicio, fin, estado, parcela);
    }

    private Long cuenta(Long agricultor, Long concepto, BigDecimal total, BigDecimal pagado, LocalDate emision, LocalDate vencimiento, String estado) {
        String codigo = jdbc.queryForObject("select 'CC-'||lpad(nextval('seq_cuenta_codigo')::text,6,'0')", String.class);
        return jdbc.queryForObject("insert into cuentas_cobrar(codigo,agricultor_id,concepto_id,monto_total,monto_pagado,fecha_emision,fecha_vencimiento,estado,observaciones) values(?,?,?,?,?,?,?,?,?) returning id", Long.class, codigo, agricultor, concepto, total, pagado, Date.valueOf(emision), Date.valueOf(vencimiento), estado, "Dato ficticio demo");
    }

    private Long pago(Long agricultor, Long cuenta, BigDecimal monto, LocalDate fecha, String metodo, String comprobante, String estado) {
        String codigo = jdbc.queryForObject("select 'PG-'||lpad(nextval('seq_pago_codigo')::text,6,'0')", String.class);
        return jdbc.queryForObject("insert into pagos(codigo,monto,fecha,concepto,comprobante,metodo,estado,agricultor_id,cuenta_cobrar_id) values(?,?,?,?,?,?,?,?,?) returning id", Long.class, codigo, monto, Date.valueOf(fecha), "Operación demo", comprobante, metodo, estado, agricultor, cuenta);
    }

    private void egreso(Long usuario, LocalDate fecha, String concepto, BigDecimal monto, String comprobante) {
        String codigo = jdbc.queryForObject("select 'EG-'||lpad(nextval('seq_egreso_codigo')::text,6,'0')", String.class);
        jdbc.update("insert into egresos(codigo,fecha,concepto,descripcion,monto,metodo,comprobante,estado,usuario_id) values(?,?,?,'Dato ficticio demo',?,'EFECTIVO',?,'REGISTRADO',?)", codigo, Date.valueOf(fecha), concepto, monto, comprobante, usuario);
    }
}
