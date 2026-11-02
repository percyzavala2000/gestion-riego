# Políticas de seguridad y auditoría

- Contraseñas de al menos 8 caracteres; en producción usa una frase extensa y única.
- No almacenar credenciales en Git.
- Aplicar mínimo privilegio y revisar cuentas inactivas.
- La bitácora registra operación y resultado, no cuerpos, contraseñas ni tokens.
- Conservar auditoría al menos 12 meses, según política institucional.
- Investigar errores mediante `X-Correlation-Id`.
- Producción exige HTTPS, cookie Secure, respaldo y secretos por variables de entorno.
