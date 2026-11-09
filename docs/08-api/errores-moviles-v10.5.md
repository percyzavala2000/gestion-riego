# Errores móviles v10.5

| Estado | Significado | Acción del cliente |
|---:|---|---|
| 400 | Solicitud inválida | Corregir datos |
| 401 | Token ausente, inválido o expirado | Intentar refresh; si falla, volver al login |
| 403 | Operación no autorizada | No reintentar |
| 404 | Recurso no encontrado o ajeno | Volver al listado |
| 409 | Conflicto de negocio | Mostrar detalle |
| 429 | Exceso de intentos | Respetar `Retry-After` |
| 500 | Error inesperado | Mostrar `correlationId` y permitir reintento controlado |

Las respuestas REST usan `application/problem+json`.
