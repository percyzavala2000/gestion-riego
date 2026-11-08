# Notificaciones persistentes v10.2
Las notificaciones se almacenan en PostgreSQL y se aíslan por destinatario. La clave de idempotencia evita duplicados.

## API del agricultor
- `GET /api/v1/agricultor/me/notificaciones`
- `GET /api/v1/agricultor/me/notificaciones/no-leidas`
- `PATCH /api/v1/agricultor/me/notificaciones/{id}/leer`
- `PATCH /api/v1/agricultor/me/notificaciones/leer-todas`
- `PATCH /api/v1/agricultor/me/notificaciones/{id}/archivar`

## Retención
Leídas: 180 días. Archivadas: 90 días. Generación: 06:15. Limpieza: 02:45, zona America/Lima.
