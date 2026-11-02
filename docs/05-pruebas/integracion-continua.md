# Integración continua v9.3

El workflow `.github/workflows/ci.yml` ejecuta `mvn clean verify` con Java 21 en cada push o pull request hacia `main` y permite ejecución manual. Las pruebas `*Test` son unitarias; las `*IT` se ejecutan con Maven Failsafe y PostgreSQL temporal mediante Testcontainers.

## Ejecución local

```bash
mvn clean verify
```

Requiere Docker activo para las pruebas de integración. Si Docker no está disponible, Testcontainers omite las pruebas marcadas, pero el pipeline oficial sí debe ejecutarlas.

## Informes

- Surefire: `target/surefire-reports`
- Failsafe: `target/failsafe-reports`
- JaCoCo: `target/site/jacoco/index.html`

La cobertura es un indicador de ejecución, no una demostración suficiente de corrección. Los casos financieros y las restricciones por rol deben conservar aserciones semánticas.
