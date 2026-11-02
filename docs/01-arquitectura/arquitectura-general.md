# Arquitectura general

Vichayal Riego Web es un monolito modular Java 21 con Spring Boot, MVC, Thymeleaf, Spring Security, JPA, PostgreSQL y Flyway. La interfaz llama controladores MVC; los controladores delegan en servicios transaccionales; los servicios aplican reglas y usan repositorios JPA; PostgreSQL conserva datos y Flyway versiona el esquema.

## Módulos
- `agricultor`, `parcela`, `riego`: operación hídrica.
- `tesoreria`, `finanzas`, `caja`: pagos, deuda, egresos, devoluciones y cierres.
- `usuario`, `security`: identidad y autorización.
- `reporte`: PDF y Excel.
- `inteligencia`: reglas explicables.
- `auditoria`: trazabilidad HTTP mutable.
- `config.seed`: entorno demo.

## Principios
Separación por dominio, transacciones en servicios, migraciones inmutables, mínimo privilegio, no exponer entidades mediante API futura y datos demo aislados.
