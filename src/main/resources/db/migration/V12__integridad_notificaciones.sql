CREATE INDEX idx_notificacion_referencia ON notificaciones(referencia_tipo,referencia_id);
CREATE INDEX idx_notificacion_fecha_estado ON notificaciones(fecha_creacion,estado);
