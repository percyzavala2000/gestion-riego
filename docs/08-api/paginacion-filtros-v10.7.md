# Paginación y filtros v10.7

## Convenciones

- `page`: índice basado en cero.
- `size`: 10, 20 o 50; cualquier otro valor se normaliza a 20.
- Los filtros se conservan al navegar entre páginas.
- El orden se define en el servidor mediante campos permitidos, no mediante propiedades arbitrarias recibidas del cliente.

## Módulos

| Ruta | Parámetros principales |
|---|---|
| `/agricultores` | `q`, `estado`, `page`, `size` |
| `/turnos` | `q`, `estado`, `page`, `size` |
| `/parcelas` | `q`, `estado`, `sector`, `page`, `size` |
| `/tesoreria` | `q`, `estado`, `metodo`, `page`, `size` |
| `/tesoreria/cuentas` | `q`, `estado`, `vencimiento`, `page`, `size` |
| `/notificaciones` | `estado`, `prioridad`, `tipo`, `page`, `size` |
| `/tesoreria/caja` | parámetros independientes `e*`, `d*` y `c*` |

## Caja

- Egresos: `eQ`, `eEstado`, `ePage`, `eSize`.
- Devoluciones: `dQ`, `dEstado`, `dPage`, `dSize`.
- Cierres: `cEstado`, `cDesde`, `cHasta`, `cPage`, `cSize`.

Los tres paginadores son independientes.
