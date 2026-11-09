# Despliegue de producción v9.4

1. Copia `.env.prod.example` a `.env.prod` y sustituye todos los secretos.
2. Mantén `APP_BIND_ADDRESS=127.0.0.1` mientras no exista proxy HTTPS.
3. Valida con `docker compose --env-file .env.prod -f compose-prod.yaml config`.
4. Ejecuta `docker compose --env-file .env.prod -f compose-prod.yaml up -d --build`.
5. Comprueba `docker compose --env-file .env.prod -f compose-prod.yaml ps` y `scripts/verify-production.sh`.
6. Confirma Flyway V1-V14 y crea el administrador inicial por un procedimiento controlado.

## Actualización
Crea respaldo, construye la nueva imagen, ejecuta `up -d --build`, valida salud y pruebas de humo. No edites migraciones ya aplicadas.

## Reversión
Conserva la imagen anterior. Si la nueva versión falla antes de una migración incompatible, restaura la etiqueta anterior. Si existen cambios de esquema, usa un plan específico y un respaldo verificado.

## HTTPS
`COOKIE_SECURE=true` requiere HTTPS. Coloca un proxy inverso TLS delante de la aplicación antes de publicarla en Internet.
