package pe.vichaval.gestionriego.config.seed;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(
        name = "vichayal.demo.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private static final int TOTAL_AGRICULTORES = 35;
    private static final int TOTAL_PARCELAS = 50;
    private static final int TOTAL_TURNOS = 80;
    private static final int TOTAL_CUENTAS = 45;
    private static final int TOTAL_PAGOS = 35;
    private static final int TOTAL_EGRESOS = 20;
    private static final int TOTAL_DEVOLUCIONES = 10;
    private static final int TOTAL_CIERRES = 12;

    private static final String[] NOMBRES = {
            "Adriana", "Bruno", "Claudia", "Diego", "Elena",
            "Felipe", "Gabriela", "Hugo", "Ines", "Jorge",
            "Karla", "Luis", "Mariana", "Nicolas", "Olga",
            "Pablo", "Rosa", "Sergio", "Teresa", "Victor",
            "Wendy", "Ximena", "Yolanda", "Alonso", "Beatriz",
            "Carlos", "Diana", "Eduardo", "Fiorella", "Gustavo",
            "Helena", "Ivan", "Julia", "Kevin", "Lucia"
    };

    private static final String[] APELLIDOS = {
            "Campos Demo", "Rios Demo", "Vega Demo", "Paredes Demo", "Torres Demo",
            "Mendoza Demo", "Salazar Demo", "Flores Demo", "Castillo Demo", "Navarro Demo",
            "Quispe Demo", "Ramos Demo", "Soto Demo", "Valencia Demo", "Zamora Demo",
            "Acosta Demo", "Benites Demo", "Cabrera Demo", "Delgado Demo", "Espinoza Demo",
            "Fernandez Demo", "Garcia Demo", "Herrera Demo", "Ibarra Demo", "Jimenez Demo",
            "Leon Demo", "Medina Demo", "Nunez Demo", "Ortega Demo", "Palacios Demo",
            "Reyes Demo", "Silva Demo", "Tapia Demo", "Ugarte Demo", "Vargas Demo"
    };

    private static final String[] SECTORES = {"Norte", "Centro", "Sur"};
    private static final String[] CULTIVOS = {
            "Limon", "Maiz", "Arroz", "Mango", "Platano",
            "Palta", "Camote", "Frejol", "Cebolla", "Maracuya"
    };
    private static final String[] METODOS = {"EFECTIVO", "TRANSFERENCIA", "YAPE", "PLIN"};

    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;
    private final Environment environment;
    private final String usersPassword;

    public DemoDataSeeder(
            JdbcTemplate jdbc,
            PasswordEncoder encoder,
            Environment environment,
            @Value("${vichayal.demo.users-password:}") String usersPassword
    ) {
        this.jdbc = jdbc;
        this.encoder = encoder;
        this.environment = environment;
        this.usersPassword = usersPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        DemoSeedGuard.validar(environment.getActiveProfiles());
        validarPasswordDemo();

        Integer existentes = jdbc.queryForObject(
                "select count(*) from usuarios where username='demo_admin'",
                Integer.class
        );
        if (existentes != null && existentes > 0) {
            log.info("Los datos demo ya existen; no se insertaron duplicados");
            return;
        }

        log.info("Iniciando carga ampliada de datos de demostracion para paginacion");

        Long admin = usuario("demo_admin", "Administrador", "Demostracion", "ADMINISTRADOR");
        usuario("demo_operador", "Operador", "Demostracion", "OPERADOR");
        usuario("demo_tesoreria", "Tesoreria", "Demostracion", "TESORERIA");

        List<Long> agricultores = crearAgricultores();
        List<Long> parcelas = crearParcelas(agricultores);
        LocalDate hoy = LocalDate.now();

        crearTurnos(parcelas, hoy);

        Long conceptoRiego = jdbc.queryForObject(
                "select id from conceptos_cobro where codigo='RIEGO'", Long.class);
        Long conceptoMant = jdbc.queryForObject(
                "select id from conceptos_cobro where codigo='MANTENIMIENTO'", Long.class);

        List<CuentaDemo> cuentas = crearCuentas(
                agricultores, conceptoRiego, conceptoMant, hoy);
        List<Long> pagosConfirmados = crearPagos(agricultores, cuentas, hoy);
        crearEgresos(admin, hoy);
        crearDevoluciones(admin, pagosConfirmados, hoy);
        crearCierres(admin, hoy);
        crearNotificaciones(admin, hoy);

        log.info(
                "Seed ampliado cargado: {} agricultores, {} parcelas, {} turnos, "
                        + "{} cuentas, {} pagos, {} egresos, {} devoluciones y {} cierres",
                TOTAL_AGRICULTORES, TOTAL_PARCELAS, TOTAL_TURNOS,
                TOTAL_CUENTAS, TOTAL_PAGOS, TOTAL_EGRESOS,
                TOTAL_DEVOLUCIONES, TOTAL_CIERRES
        );
    }

    private List<Long> crearAgricultores() {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < TOTAL_AGRICULTORES; i++) {
            String dni = "%08d".formatted(90000001 + i);
            String telefono = "9%08d".formatted(i + 1);
            String estado = i % 9 == 0 ? "INACTIVO" : "ACTIVO";
            ids.add(agricultor(
                    dni, NOMBRES[i], APELLIDOS[i], telefono,
                    SECTORES[i % SECTORES.length], estado
            ));
        }
        return ids;
    }

    private List<Long> crearParcelas(List<Long> agricultores) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < TOTAL_PARCELAS; i++) {
            String codigo = "DEMO-PAR-%03d".formatted(i + 1);
            String cultivo = CULTIVOS[i % CULTIVOS.length];
            String nombre = "Lote " + cultivo + " " + "%02d".formatted(i + 1);
            BigDecimal hectareas = BigDecimal.valueOf(150L + (i % 18) * 35L, 2);
            String estado = i % 13 == 0 ? "EN_MANTENIMIENTO"
                    : i % 11 == 0 ? "INACTIVA" : "ACTIVA";
            ids.add(parcela(
                    codigo, nombre, hectareas, cultivo,
                    agricultores.get(i % agricultores.size()), estado
            ));
        }
        return ids;
    }

    private void crearTurnos(List<Long> parcelas, LocalDate hoy) {
        String[] estadosPasados = {"COMPLETADO", "VENCIDO", "CANCELADO"};
        for (int i = 0; i < TOTAL_TURNOS; i++) {
            int desplazamiento = i - 45;
            LocalDate fecha = hoy.plusDays(desplazamiento);
            LocalTime inicio = LocalTime.of(5 + (i % 12), (i % 2) * 30);
            String estado = desplazamiento < 0
                    ? estadosPasados[i % estadosPasados.length]
                    : "PROGRAMADO";
            turno(
                    "DEMO-TR-%03d".formatted(i + 1),
                    fecha, inicio, inicio.plusMinutes(60), estado,
                    parcelas.get(i % parcelas.size())
            );
        }
    }

    private List<CuentaDemo> crearCuentas(
            List<Long> agricultores,
            Long conceptoRiego,
            Long conceptoMant,
            LocalDate hoy
    ) {
        List<CuentaDemo> cuentas = new ArrayList<>();
        for (int i = 0; i < TOTAL_CUENTAS; i++) {
            BigDecimal total = BigDecimal.valueOf(200 + (i % 9) * 50L);
            int tipo = i % 5;
            BigDecimal pagado;
            String estado;
            LocalDate vencimiento;
            if (tipo == 0) {
                pagado = total;
                estado = "PAGADA";
                vencimiento = hoy.plusDays(10 + i % 15);
            } else if (tipo == 1) {
                pagado = total.divide(BigDecimal.valueOf(2));
                estado = "PAGO_PARCIAL";
                vencimiento = hoy.plusDays(5 + i % 12);
            } else if (tipo == 2) {
                pagado = BigDecimal.ZERO;
                estado = "VENCIDA";
                vencimiento = hoy.minusDays(1 + i % 25);
            } else if (tipo == 3) {
                pagado = BigDecimal.ZERO;
                estado = "PENDIENTE";
                vencimiento = hoy.plusDays(i % 7);
            } else {
                pagado = BigDecimal.ZERO;
                estado = "ANULADA";
                vencimiento = hoy.plusDays(20 + i % 10);
            }
            Long id = cuenta(
                    agricultores.get(i % agricultores.size()),
                    i % 3 == 0 ? conceptoMant : conceptoRiego,
                    total, pagado, hoy.minusDays(50 - i), vencimiento, estado
            );
            cuentas.add(new CuentaDemo(id, agricultores.get(i % agricultores.size()), total, pagado, estado));
        }
        return cuentas;
    }

    private List<Long> crearPagos(
            List<Long> agricultores,
            List<CuentaDemo> cuentas,
            LocalDate hoy
    ) {
        List<Long> confirmados = new ArrayList<>();
        int creados = 0;

        for (CuentaDemo cuenta : cuentas) {
            if (creados >= TOTAL_PAGOS) break;
            if (cuenta.pagado().signum() > 0) {
                Long id = pago(
                        cuenta.agricultorId(), cuenta.id(), cuenta.pagado(),
                        hoy.minusDays((creados % 25) + 1),
                        METODOS[creados % METODOS.length],
                        "DEMO-COMP-%03d".formatted(creados + 1), "CONFIRMADO"
                );
                confirmados.add(id);
                creados++;
            }
        }

        while (creados < TOTAL_PAGOS) {
            int i = creados;
            String estado = i % 6 == 0 ? "PENDIENTE"
                    : i % 7 == 0 ? "RECHAZADO"
                    : i % 8 == 0 ? "ANULADO" : "CONFIRMADO";
            Long id = pago(
                    agricultores.get(i % agricultores.size()), null,
                    BigDecimal.valueOf(80 + (i % 8) * 25L),
                    hoy.minusDays(i % 30), METODOS[i % METODOS.length],
                    "DEMO-COMP-%03d".formatted(i + 1), estado
            );
            if ("CONFIRMADO".equals(estado)) confirmados.add(id);
            creados++;
        }
        return confirmados;
    }

    private void crearEgresos(Long admin, LocalDate hoy) {
        String[] conceptos = {
                "Mantenimiento preventivo", "Compra de materiales",
                "Limpieza de canal", "Reparacion de compuerta",
                "Combustible", "Herramientas de campo"
        };
        for (int i = 0; i < TOTAL_EGRESOS; i++) {
            egreso(
                    admin, hoy.minusDays(i), conceptos[i % conceptos.length],
                    BigDecimal.valueOf(55 + (i % 10) * 20L),
                    "DEMO-EGR-%03d".formatted(i + 1),
                    i % 7 == 0 ? "ANULADO" : "REGISTRADO"
            );
        }
    }

    private void crearDevoluciones(
            Long admin,
            List<Long> pagosConfirmados,
            LocalDate hoy
    ) {
        String[] estados = {"SOLICITADA", "APROBADA", "EJECUTADA", "RECHAZADA"};
        for (int i = 0; i < TOTAL_DEVOLUCIONES; i++) {
            String codigo = siguienteCodigo("seq_devolucion_codigo", "DV");
            jdbc.update(
                    "insert into devoluciones(codigo,pago_id,monto,motivo,estado,fecha,usuario_id) "
                            + "values(?,?,?,?,?,?,?)",
                    codigo,
                    pagosConfirmados.get(i % pagosConfirmados.size()),
                    BigDecimal.valueOf(10 + (i % 5) * 5L),
                    "Solicitud ficticia para prueba de paginacion " + (i + 1),
                    estados[i % estados.length],
                    Date.valueOf(hoy.minusDays(i)),
                    admin
            );
        }
    }

    private void crearCierres(Long admin, LocalDate hoy) {
        for (int i = 0; i < TOTAL_CIERRES; i++) {
            LocalDate fecha = hoy.minusDays(i + 1L);
            BigDecimal saldoInicial = BigDecimal.valueOf(100 + i * 10L);
            BigDecimal ingresos = BigDecimal.valueOf(300 + i * 25L);
            BigDecimal egresos = BigDecimal.valueOf(50 + i * 5L);
            BigDecimal devoluciones = BigDecimal.valueOf((i % 3) * 10L);
            BigDecimal teorico = saldoInicial.add(ingresos).subtract(egresos).subtract(devoluciones);
            BigDecimal diferencia = BigDecimal.valueOf((i % 5) - 2L);
            BigDecimal contado = teorico.add(diferencia);
            jdbc.update(
                    "insert into cierres_caja(fecha,saldo_inicial,ingresos,egresos,devoluciones,"
                            + "saldo_teorico,saldo_contado,diferencia,observaciones,estado,usuario_id) "
                            + "values(?,?,?,?,?,?,?,?,?,?,?)",
                    Date.valueOf(fecha), saldoInicial, ingresos, egresos, devoluciones,
                    teorico, contado, diferencia,
                    "Cierre ficticio para prueba de paginacion " + (i + 1),
                    i % 6 == 0 ? "REABIERTO" : "CERRADO", admin
            );
        }
    }

    private void crearNotificaciones(Long admin, LocalDate hoy) {
        String[] tipos = {
                "TURNO_PROXIMO", "CUENTA_PROXIMA_VENCER",
                "CUENTA_VENCIDA", "AVISO_ADMINISTRATIVO"
        };
        String[] prioridades = {"BAJA", "MEDIA", "ALTA", "CRITICA"};
        String[] estados = {"NO_LEIDA", "LEIDA"};
        for (int i = 0; i < 30; i++) {
            jdbc.update(
                    "insert into notificaciones(usuario_id,tipo,titulo,mensaje,prioridad,estado,"
                            + "referencia_tipo,referencia_url,clave_idempotencia,fecha_creacion) "
                            + "values(?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP - (? * interval '1 day'))",
                    admin,
                    tipos[i % tipos.length],
                    "Notificacion demo " + (i + 1),
                    "Aviso ficticio para validar filtros y paginacion.",
                    prioridades[i % prioridades.length],
                    estados[i % estados.length],
                    "DEMO", "/dashboard", "DEMO-NOT-" + (i + 1), i % 20
            );
        }
    }

    private void validarPasswordDemo() {
        if (usersPassword == null || usersPassword.isBlank()) {
            throw new IllegalStateException(
                    "DEMO_USERS_PASSWORD es obligatorio cuando DEMO_SEED_ENABLED=true");
        }
        if (usersPassword.length() < 12) {
            throw new IllegalStateException(
                    "DEMO_USERS_PASSWORD debe tener al menos 12 caracteres");
        }
    }

    private Long usuario(String username, String nombres, String apellidos, String rol) {
        return jdbc.queryForObject(
                "insert into usuarios(username,password_hash,nombres,apellidos,activo,rol_id) "
                        + "values(?,?,?,?,true,(select id from roles where codigo=?)) returning id",
                Long.class, username, encoder.encode(usersPassword), nombres, apellidos, rol);
    }

    private Long agricultor(
            String dni, String nombres, String apellidos,
            String telefono, String sector, String estado
    ) {
        return jdbc.queryForObject(
                "insert into agricultores(dni,nombres,apellidos,telefono,sector,estado) "
                        + "values(?,?,?,?,?,?) returning id",
                Long.class, dni, nombres, apellidos, telefono, sector, estado);
    }

    private Long parcela(
            String codigo, String nombre, BigDecimal ha, String cultivo,
            Long agricultor, String estado
    ) {
        return jdbc.queryForObject(
                "insert into parcelas(codigo,nombre,hectareas,cultivo,estado,agricultor_id) "
                        + "values(?,?,?,?,?,?) returning id",
                Long.class, codigo, nombre, ha, cultivo, estado, agricultor);
    }

    private void turno(
            String codigo, LocalDate fecha, LocalTime inicio,
            LocalTime fin, String estado, Long parcela
    ) {
        jdbc.update(
                "insert into turnos_riego(codigo,fecha,hora_inicio,hora_fin,estado,observaciones,parcela_id) "
                        + "values(?,?,?,?,?,'Dato ficticio demo para paginacion',?)",
                codigo, Date.valueOf(fecha), inicio, fin, estado, parcela);
    }

    private Long cuenta(
            Long agricultor, Long concepto, BigDecimal total, BigDecimal pagado,
            LocalDate emision, LocalDate vencimiento, String estado
    ) {
        String codigo = siguienteCodigo("seq_cuenta_codigo", "CC");
        return jdbc.queryForObject(
                "insert into cuentas_cobrar(codigo,agricultor_id,concepto_id,monto_total,monto_pagado,"
                        + "fecha_emision,fecha_vencimiento,estado,observaciones) "
                        + "values(?,?,?,?,?,?,?,?,?) returning id",
                Long.class, codigo, agricultor, concepto, total, pagado,
                Date.valueOf(emision), Date.valueOf(vencimiento), estado,
                "Dato ficticio demo para paginacion");
    }

    private Long pago(
            Long agricultor, Long cuenta, BigDecimal monto, LocalDate fecha,
            String metodo, String comprobante, String estado
    ) {
        String codigo = siguienteCodigo("seq_pago_codigo", "PG");
        return jdbc.queryForObject(
                "insert into pagos(codigo,monto,fecha,concepto,comprobante,metodo,estado,"
                        + "agricultor_id,cuenta_cobrar_id) values(?,?,?,?,?,?,?,?,?) returning id",
                Long.class, codigo, monto, Date.valueOf(fecha), "Operacion demo",
                comprobante, metodo, estado, agricultor, cuenta);
    }

    private void egreso(
            Long usuario, LocalDate fecha, String concepto, BigDecimal monto,
            String comprobante, String estado
    ) {
        String codigo = siguienteCodigo("seq_egreso_codigo", "EG");
        jdbc.update(
                "insert into egresos(codigo,fecha,concepto,descripcion,monto,metodo,comprobante,estado,usuario_id) "
                        + "values(?,?,?,'Dato ficticio demo para paginacion',?,'EFECTIVO',?,?,?)",
                codigo, Date.valueOf(fecha), concepto, monto, comprobante, estado, usuario);
    }

    private String siguienteCodigo(String secuencia, String prefijo) {
        return jdbc.queryForObject(
                "select ? || '-' || lpad(nextval('" + secuencia + "')::text,6,'0')",
                String.class, prefijo);
    }

    private record CuentaDemo(
            Long id,
            Long agricultorId,
            BigDecimal total,
            BigDecimal pagado,
            String estado
    ) {
    }
}
