CREATE TABLE IF NOT EXISTS citas (
    id_cita BIGINT NOT NULL AUTO_INCREMENT,
    paciente VARCHAR(150) NOT NULL,
    doctor_correo VARCHAR(254) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    PRIMARY KEY (id_cita),
    CONSTRAINT uq_cita_doctor_horario UNIQUE (doctor_correo, fecha, hora)
);
