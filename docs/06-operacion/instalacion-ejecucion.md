# Instalación y ejecución

Requisitos: Java 21, Maven, Docker y puertos 8080/5432. Inicia PostgreSQL con `docker compose up -d postgres`, verifica `docker compose ps`, ejecuta `mvn clean test` y luego `mvn spring-boot:run`.

Perfiles: `dev` local, `demo` aislado, `prod` con variables obligatorias. Comprueba siempre `mvn -version` porque el proyecto requiere Java 21.
