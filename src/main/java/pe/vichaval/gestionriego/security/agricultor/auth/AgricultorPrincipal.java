package pe.vichaval.gestionriego.security.agricultor.auth;
public record AgricultorPrincipal(Long cuentaId, Long agricultorId, String usuario) {}
