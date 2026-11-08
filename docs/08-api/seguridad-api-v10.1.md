# Seguridad API v10.1

La API del agricultor utiliza tokens opacos aleatorios. Solo sus hashes SHA-256 se almacenan en PostgreSQL.

## Endpoints
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`
- `GET /api/v1/agricultor/me`
- `GET /api/v1/agricultor/me/parcelas`
- `GET /api/v1/agricultor/me/turnos`
- `GET /api/v1/agricultor/me/turnos/{id}`

El access token dura 15 minutos y se envía como `Authorization: Bearer TOKEN`. El refresh token dura 14 días, se rota en cada uso y el anterior se revoca. Las rutas privadas obtienen el agricultor desde el token y no aceptan un identificador arbitrario.
