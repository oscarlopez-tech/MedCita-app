# MedCita-app

Este proyecto es un sistema de gestión para clínica médica. Actualmente incluye una interfaz de escritorio en Java Swing para inicio de sesión, registro de usuarios y recuperación de contraseña.

## Ejecución

Se requiere Java 21 y Maven. Desde la raíz del proyecto, ejecuta:

```shell
mvn clean compile exec:java
```

Las operaciones de autenticación y registro requieren acceso a la base de datos configurada en `ConexionController`.

## Estado del trabajo por estudiante

### Estudiante 1: Roles, permisos y estructura base

- **Pendiente:** documentar y establecer de forma general los roles y permisos del sistema, así como las reglas de acceso de los módulos de Citas, Facturación y Usuarios.
- **Nota:** el módulo de Citas ya incluye comprobaciones de permisos propias. Esto no sustituye la configuración general de acceso que corresponde a este apartado.

### Estudiante 2: Módulo de Citas — completado

- Las citas requieren nombre del paciente, correo del doctor, fecha y hora.
- El sistema valida los campos y evita dos citas para el mismo doctor en el mismo horario.
- El administrador puede crear, modificar y consultar todas las citas.
- El doctor puede crear citas a su nombre, modificar sus propias citas y consultar solo las suyas.
- Facturación/CxC puede consultar todas las citas, pero no crearlas ni modificarlas.
- La lógica de permisos y operaciones está en `CitasController`; la persistencia JDBC está en `JdbcCitaRepository`.
- La tabla requerida está definida en `src/main/resources/db/citas.sql`. Hay que ejecutar ese script en la base de datos antes de usar la persistencia de citas.
- Se agregaron pruebas automatizadas para permisos, creación, modificación y horarios duplicados.

### Estudiante 3: Facturación y Cuentas por Cobrar — pendiente

- Falta definir e implementar el flujo para generar una factura a partir de una cita atendida.
- Falta definir e implementar los estados de Cuentas por Cobrar, como pendiente y pagado.
- Según el alcance acordado, el doctor no debe tener acceso a este módulo.

### Estudiante 4: Gestión de Usuarios — pendiente

- Falta implementar las reglas para que el administrador dé de alta, edite o bloquee usuarios.
- Falta definir e implementar los datos requeridos para un usuario, incluyendo nombre, correo, contraseña y rol.
