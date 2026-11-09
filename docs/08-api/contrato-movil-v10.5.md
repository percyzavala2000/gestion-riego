# Contrato móvil estable v10.5

Cliente previsto: React Native con Android e iOS.

## Seguridad
- Access token opaco: 15 minutos.
- Refresh token rotatorio: 14 días.
- Login limitado por IP: 10 solicitudes por 60 segundos por defecto.
- Exceso: `429 application/problem+json` y encabezado `Retry-After`.
- Los tokens se almacenan mediante hash SHA-256 en PostgreSQL.

## CORS
React Native nativo no necesita CORS. CORS queda deshabilitado por defecto. Solo debe habilitarse para un cliente web autorizado mediante:

```text
MOBILE_CORS_ENABLED=true
MOBILE_CORS_ALLOWED_ORIGINS=https://app.example.org
```

Nunca se permite `*` junto con credenciales.

## API principal
- `/api/v1/auth/*`
- `/api/v1/agricultor/me`
- `/api/v1/agricultor/me/parcelas`
- `/api/v1/agricultor/me/turnos`
- `/api/v1/agricultor/me/notificaciones`
- `/api/v1/agricultor/me/dispositivos`

## Errores
Las respuestas REST usan `application/problem+json`, estado HTTP, título, detalle, instancia y `correlationId` cuando está disponible.
