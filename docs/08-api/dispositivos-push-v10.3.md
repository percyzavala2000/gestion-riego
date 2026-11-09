# Dispositivos push v10.3

API React Native protegida por Bearer:

- `PUT /api/v1/agricultor/me/dispositivos/{installationId}`
- `GET /api/v1/agricultor/me/dispositivos`
- `DELETE /api/v1/agricultor/me/dispositivos/{id}`

Plataformas: `ANDROID` e `IOS`. El `installationId` hace idempotente el registro y permite renovar el token FCM sin duplicar el dispositivo.

El payload push contiene `notificacionId`, `tipo`, `screen` y, cuando existe, `entityId`. Pantallas React Native: `TurnoDetalle`, `CuentaDetalle`, `PagoDetalle` y `NotificacionDetalle`.
