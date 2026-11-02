#!/usr/bin/env bash
set -euo pipefail
COMPOSE_FILE="${COMPOSE_FILE:-compose-prod.yaml}"
ENV_FILE="${ENV_FILE:-.env.prod}"
PROJECT_NAME="${COMPOSE_PROJECT_NAME:-vichayal_prod}"
BACKUP_DIR="${BACKUP_DIR:-backups}"
mkdir -p "$BACKUP_DIR"
STAMP="$(date -u +%Y%m%dT%H%M%SZ)"
FILE="$BACKUP_DIR/gestion-riego-$STAMP.dump"
docker compose -p "$PROJECT_NAME" --env-file "$ENV_FILE" -f "$COMPOSE_FILE" exec -T postgres sh -eu -c 'pg_dump -Fc -U "$POSTGRES_USER" -d "$POSTGRES_DB"' > "$FILE"
test -s "$FILE"
docker run --rm -v "$(cd "$BACKUP_DIR" && pwd):/backups:ro" postgres:18 pg_restore --list "/backups/$(basename "$FILE")" >/dev/null
shasum -a 256 "$FILE" > "$FILE.sha256"
echo "Respaldo verificado: $FILE"
