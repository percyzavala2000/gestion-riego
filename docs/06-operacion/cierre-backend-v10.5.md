# Cierre del backend v10.5

## Contrato congelado
El backend queda preparado para una aplicación React Native con Android e iOS. Los cambios incompatibles en `/api/v1` requieren una nueva versión de API.

## Validación mínima
```bash
mvn clean verify
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml up -d --build
docker compose -p vichayal_prod --env-file .env.prod -f compose-prod.yaml -f compose-observability.yaml ps
curl --fail http://127.0.0.1:8080/actuator/health
```

## Firebase
Mantener `FIREBASE_ENABLED=false` hasta montar `/run/secrets/firebase-service-account.json`. Android usa `google-services.json`; iOS usa `GoogleService-Info.plist` y APNs. Ningún archivo de credenciales se guarda en Git.

## CORS
React Native nativo no requiere CORS. Mantener `MOBILE_CORS_ENABLED=false` salvo que exista un cliente web externo con origen explícito.

## Respaldo previo a despliegue
```bash
ENV_FILE=.env.prod COMPOSE_FILE=compose-prod.yaml COMPOSE_PROJECT_NAME=vichayal_prod ./scripts/backup-postgres.sh
```
