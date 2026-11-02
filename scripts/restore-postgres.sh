#!/usr/bin/env bash
set -euo pipefail
[ "$#" -eq 1 ] || { echo "Uso: $0 respaldo.dump"; exit 1; }
FILE="$1"
[ -s "$FILE" ] || { echo "Respaldo inexistente o vacío: $FILE"; exit 1; }
COMPOSE_FILE="${COMPOSE_FILE:-compose-prod.yaml}"
ENV_FILE="${ENV_FILE:-.env.prod}"
PROJECT_NAME="${COMPOSE_PROJECT_NAME:-vichayal_prod}"
TARGET_DB="${RESTORE_TARGET_DB:-gestion_riego_restore_test}"
case "$TARGET_DB" in gestion_riego|*prod*) echo "Destino inseguro: $TARGET_DB"; exit 1;; esac
docker run --rm -v "$(cd "$(dirname "$FILE")" && pwd):/backups:ro" postgres:18 pg_restore --list "/backups/$(basename "$FILE")" >/dev/null
docker compose -p "$PROJECT_NAME" --env-file "$ENV_FILE" -f "$COMPOSE_FILE" exec -T -e TARGET_DB="$TARGET_DB" postgres sh -eu -c 'dropdb --if-exists -U "$POSTGRES_USER" "$TARGET_DB"; createdb -U "$POSTGRES_USER" "$TARGET_DB"; pg_restore --no-owner -U "$POSTGRES_USER" -d "$TARGET_DB"' < "$FILE"
echo "Restauración de ensayo terminada en: $TARGET_DB"
