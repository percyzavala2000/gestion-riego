# Migraciones Flyway

V1 crea el esquema; V2 agrega estados; V3 crea conceptos/cuentas; V4 vincula pagos; V5 crea egresos/devoluciones; V6 cierres; V7 auditoría; V8 integridad y secuencias.

## Regla
Una migración aplicada es inmutable. Todo cambio futuro usa la siguiente versión. `spring.flyway.clean-disabled=true` impide limpieza accidental.

V9 y V10 incorporan cuentas y tokens del agricultor; V11 y V12 notificaciones persistentes; V13 y V14 dispositivos y entregas push.
