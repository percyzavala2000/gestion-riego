# Diccionario de datos resumido

| Tabla | Finalidad | Claves y reglas principales |
|---|---|---|
| roles | Catálogo de roles | `codigo` único |
| usuarios | Acceso al sistema | `username` único, FK `rol_id` |
| agricultores | Padrón | DNI único, estado |
| parcelas | Unidades agrícolas | código único, FK agricultor |
| turnos_riego | Programación | código único, FK parcela, estados |
| conceptos_cobro | Catálogo financiero | código único |
| cuentas_cobrar | Obligaciones | monto positivo, pagado entre 0 y total |
| pagos | Ingresos | comprobante/código únicos, FK agricultor y cuenta opcional |
| egresos | Salidas | comprobante/código únicos |
| devoluciones | Reintegros | FK pago, monto positivo |
| cierres_caja | Consolidación diaria | fecha única, saldos y diferencia |
| auditoria_eventos | Trazabilidad | correlación, usuario, ruta y resultado |

Los estados y transiciones son reglas de dominio. No actualices saldos manualmente en producción.
