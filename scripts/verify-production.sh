#!/usr/bin/env bash
set -euo pipefail
ENV_FILE="${ENV_FILE:-.env.prod}"
COMPOSE_FILE="${COMPOSE_FILE:-compose-prod.yaml}"
PROJECT_NAME="${COMPOSE_PROJECT_NAME:-vichayal_prod}"
docker compose -p "$PROJECT_NAME" --env-file "$ENV_FILE" -f "$COMPOSE_FILE" config >/dev/null
docker compose -p "$PROJECT_NAME" --env-file "$ENV_FILE" -f "$COMPOSE_FILE" ps
curl --fail --silent "http://${APP_BIND_ADDRESS:-127.0.0.1}:${APP_PORT:-8080}/actuator/health" | grep -q '"status":"UP"'
echo "Producción saludable"
