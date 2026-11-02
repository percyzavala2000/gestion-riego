# Runbook de observabilidad y mantenimiento v9.5

## Inicio
```bash
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
```

Servicios locales: aplicación `127.0.0.1:8080`, Prometheus `127.0.0.1:9090` y Grafana `127.0.0.1:3000`. No publiques estos puertos sin autenticación, TLS y control de red.

## Señales principales
- `up{job="vichayal-riego"}` debe ser 1.
- `http_server_requests_seconds` permite revisar tasa, error y latencia.
- `jvm_memory_used_bytes`, `process_cpu_usage` y `hikaricp_connections_*` describen recursos.
- `vichayal_pagos_pendientes`, `vichayal_deuda_total_soles` y `vichayal_devoluciones_solicitadas` son métricas agregadas, no identifican personas.

## Incidencias
1. Copia `X-Correlation-Id` de la respuesta.
2. Busca `correlationId` en los logs estructurados.
3. Revisa salud, latencia, conexiones Hikari y estado de PostgreSQL.
4. Antes de reiniciar, conserva logs y crea respaldo si la base responde.
5. Documenta hora, impacto, causa, mitigación y acción preventiva.

## Retención
La auditoría se depura diariamente a las 02:30, zona America/Lima, preservando 365 días por defecto. No configures menos de 30 días. Ajusta la política a las obligaciones institucionales y legales aplicables.
