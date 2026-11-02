# Respaldo y restauración

## Respaldo
```bash
docker compose exec -T postgres pg_dump -U gestion_user -d gestion_riego > respaldo.sql
```

## Restauración de ensayo
Crea una base vacía, restaura con `psql`, inicia la aplicación y valida Flyway, usuarios, pagos, cuentas y cierres. Conservación sugerida: 7 diarios, 4 semanales y 12 mensuales. Un respaldo no probado no constituye recuperación garantizada.
