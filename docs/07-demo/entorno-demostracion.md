# Entorno de demostración

El perfil demo usa PostgreSQL separado en el puerto 5433. Nunca combines `demo` y `prod`.

```bash
docker compose -f compose-demo.yaml up -d
SPRING_PROFILES_ACTIVE=demo mvn spring-boot:run
```

Credenciales locales:
- `demo_admin` / `Demo2026!`
- `demo_operador` / `Demo2026!`
- `demo_tesoreria` / `Demo2026!`

Cambia estas credenciales si el entorno será accesible por terceros. Para reiniciar por completo el demo:
```bash
docker compose -f compose-demo.yaml down -v
```
