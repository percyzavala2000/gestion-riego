# Migraciones Flyway

V1 crea el esquema; V2 agrega estados; V3 crea conceptos/cuentas; V4 vincula pagos; V5 crea egresos/devoluciones; V6 cierres; V7 auditoría; V8 integridad y secuencias.

## Regla
Una migración aplicada es inmutable. Todo cambio futuro usa la siguiente versión. `spring.flyway.clean-disabled=true` impide limpieza accidental.
