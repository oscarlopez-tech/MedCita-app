# MedCita-app

Este proyecto es un sistema de gestión para clínica médica. Actualmente incluye una interfaz de escritorio en Java Swing para inicio de sesión, registro de usuarios y recuperación de contraseña.

## Ejecución

Se requiere Java 21 y Maven. Desde la raíz del proyecto, ejecuta:

```shell
mvn clean compile exec:java
```

La aplicación usa SQLite: la base de datos es el archivo local `data/medcita.db`, que se crea automáticamente al iniciar la aplicación mediante `InicializadorBaseDatos`. No requiere instalar un servidor.

## Navegación entre pantallas

- Las pantallas del `CardLayout` de `MedCitaFrame` tienen un botón "Volver al inicio" que regresa a la pantalla principal (HOME) o a LOGIN si todavía no hay sesión. La vista expone el botón con `getBotonVolver()` y `MedCitaFrame` conecta la acción.
- Los diálogos modales (`JDialog`) tienen un botón "Cancelar" o "Cerrar" que cierra la ventana con `dispose()`.
- La navegación no guarda historial: cada botón de regreso lleva siempre a un destino fijo.

## Estado del trabajo por estudiante

### Estudiante 1: Roles, permisos y estructura base — completado

- Los roles están definidos en `RolUsuario`: ADMINISTRADOR, DOCTOR, FACTURACION y SIN_ACCESO. Todo usuario nuevo se registra con SIN_ACCESO.
- Los permisos están definidos en `Permiso` y se asignan a cada rol en `ControlAcceso.puede(rol, permiso)`:

| Rol | Permisos |
|---|---|
| ADMINISTRADOR | Todos |
| DOCTOR | Ver, crear y modificar citas (al modificar, solo las suyas) |
| FACTURACION | Ver todas las citas, ver y crear facturas |
| SIN_ACCESO | Ninguno |

- La ventana principal muestra solo los botones permitidos para el rol de quien inició sesión. El botón Configuración es exclusivo del administrador.
- El usuario que inició sesión se guarda en `UserSession`.
- Para crear el primer administrador, regístrate en la app y luego ejecuta sobre `data/medcita.db`: `UPDATE usuarios SET rol = 'ADMINISTRADOR' WHERE correo = 'tu@correo.com';`
- Pruebas: `ControlAccesoTest`.

### Estudiante 2: Módulo de Citas — completado

- Las citas requieren nombre del paciente, correo del doctor, fecha y hora.
- El sistema valida los campos y evita dos citas para el mismo doctor en el mismo horario.
- El administrador puede crear, modificar y consultar todas las citas.
- El doctor puede crear citas a su nombre, modificar sus propias citas y consultar solo las suyas.
- Facturación/CxC puede consultar todas las citas, pero no crearlas ni modificarlas.
- La lógica de permisos y operaciones está en `CitasController`; la persistencia JDBC está en `JdbcCitaRepository`.
- La tabla `citas` se crea automáticamente en `InicializadorBaseDatos`; no hay que ejecutar scripts manualmente.
- Se agregaron pruebas automatizadas para permisos, creación, modificación y horarios duplicados.

### Estudiante 3: Facturación y Cuentas por Cobrar — pendiente

- Falta definir e implementar el flujo para generar una factura a partir de una cita atendida.
- Falta definir e implementar los estados de Cuentas por Cobrar, como pendiente y pagado.
- Según el alcance acordado, el doctor no debe tener acceso a este módulo.

### Estudiante 4: Gestión de Usuarios — completado

- La ventana Configuración (solo para administradores) contiene la pestaña Gestión de Usuarios. Las nuevas pestañas se agregan en `Configuracion` sin modificar el contenedor.
- La tabla muestra nombre, correo, teléfono, rol y estado (Activo/Bloqueado). El filtro "Solicitudes" muestra los usuarios activos con rol SIN_ACCESO.
- El administrador puede asignar un rol, bloquear, desbloquear y eliminar usuarios.
- Bloquear no elimina la cuenta: un usuario bloqueado no puede iniciar sesión.
- Al eliminar un usuario se borran también las citas asociadas a su correo. Ambas operaciones se hacen en una transacción.
- Ningún administrador puede bloquearse, degradarse ni eliminarse a sí mismo. Desde esta pantalla no existe cambio de contraseña ni edición de datos de otros usuarios.
- Todas las operaciones pasan por `UsuariosController`, que verifica que quien actúa sea administrador.
- Pruebas: `UsuariosControllerTest` (reglas de acceso y de protección del propio usuario, sin base de datos).
- Limitación conocida: las contraseñas se guardan en texto plano.




// Contraseña y Usuarios de pruebas y test
 Correo: admin@medcita.test
Contraseña: 12345678