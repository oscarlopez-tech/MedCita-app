# MedCita-app

Este proyecto es un sistema de gestión para clínica médica. Actualmente incluye una interfaz de escritorio en Java Swing para inicio de sesión, registro de usuarios y recuperación de contraseña.

## Ejecución

Se requiere Java 21 y Maven. Desde la raíz del proyecto, ejecuta:

```shell
mvn clean compile exec:java
```

La aplicación usa SQLite: la base de datos es el archivo local `data/medcita.db`, que se crea automáticamente al iniciar la aplicación mediante `InicializadorBaseDatos`. No requiere instalar un servidor.

## Usuario de prueba

Para las pruebas manuales, al iniciar la aplicación se crea (o se restaura) un administrador:

- **Correo:** `admin@medcita.test`
- **Contraseña:** `12345678`
- **Rol:** ADMINISTRADOR

El administrador se siembra desde `InicializadorBaseDatos` en cada arranque, así que si se bloquea o cambia de rol durante las pruebas, vuelve a su estado original al reiniciar la app.

> **Antes de la entrega final:** quitar el bloque `adminPrueba` y la línea `sentencia.execute(adminPrueba);` de `InicializadorBaseDatos.java`, y borrar esa fila de la base de datos. Las credenciales no deben quedar en el código ni en este README.

## Navegación entre pantallas

- Las pantallas del `CardLayout` de `MedCitaFrame` tienen un botón "Volver al inicio" que regresa a la pantalla principal (HOME) o a LOGIN si todavía no hay sesión. La vista expone el botón con `getBotonVolver()` y `MedCitaFrame` conecta la acción.
- Los diálogos modales (`JDialog`) tienen un botón "Cancelar" o "Cerrar" que cierra la ventana con `dispose()`.
- La navegación no guarda historial: cada botón de regreso lleva siempre a un destino fijo.

## Diseño de la interfaz

- Logo "MEDCITA" bicolor: "MED" en `#0284C7` (azul tecnológico) y "CITA" en `#0F766E` (verde azulado médico).
- Cada tarjeta tiene un acento azul en la parte superior (`#2563EB`) sobre fondo `#EFF6FF`.
- Botones con jerarquía: la acción principal va rellena (Iniciar sesión, Crear cuenta, Nueva cita, Guardar, Asignar rol) y las secundarias van como texto azul. Todos los botones cambian de color al pasar el mouse.
- Los campos de texto resaltan su borde en azul cuando reciben el foco.
- Las descripciones vacías no ocupan espacio. En HOME, "Sesión iniciada correctamente." aparece al final, con un gris semitransparente.

## Resumen de cambios realizados

- **Base de datos:** migración de Railway/MySQL a SQLite (`jdbc:sqlite:data/medcita.db`) con `PRAGMA foreign_keys = ON`. Las tablas `usuarios` y `citas` se crean en `InicializadorBaseDatos`. Se eliminaron `ConexionController` y `citas.sql`. La carpeta `data/` está en `.gitignore`.
- **Roles y permisos:** enum `Permiso` (VER_CITAS, CREAR_CITA, MODIFICAR_CITA, VER_FACTURAS, CREAR_FACTURA, GESTIONAR_USUARIOS) y `ControlAcceso.puede(rol, permiso)`. La ventana principal muestra solo los botones permitidos por rol.
- **Sesión:** `UserSession` guarda el usuario que inició sesión. Se limpia al cerrar sesión.
- **Gestión de usuarios:** ventana Configuración con la pestaña Gestión de Usuarios, para listar, filtrar solicitudes, asignar rol, bloquear, desbloquear y eliminar usuarios.
- **Citas:** pantalla de listado filtrada por rol, formulario para crear y editar citas, y botones visibles según el permiso del rol.
- **Limpieza:** se eliminaron `UserManagment` (enum sin uso) y el método `actualizarUsuario` de `UserRegistration` (sin referencias).
- **Pruebas:** se agregaron `ControlAccesoTest` y `UsuariosControllerTest`.
- **Documentación:** este README.

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
- Pantalla Citas: tabla con paciente, doctor, fecha y hora, filtrada según el rol. Botones Nueva cita, Editar, Actualizar y Volver al inicio.
- Formulario Nueva cita / Editar cita (diálogo modal): el administrador elige un doctor activo; el doctor ve su propio correo fijo. La fecha se escribe como `yyyy-MM-dd` y la hora como `HH:mm`. Al editar, si el doctor de la cita está bloqueado, sigue apareciendo en la lista para no cambiar la cita de doctor sin aviso.
- Los botones Nueva cita y Editar solo aparecen si el rol tiene los permisos CREAR_CITA y MODIFICAR_CITA.
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

- La ventana Configuración (solo para administradores) contiene la pestaña Gestión de Usuarios y un botón Cerrar. Las nuevas pestañas se agregan en `Configuracion` sin modificar el contenedor.
- La tabla muestra nombre, correo, teléfono, rol y estado (Activo/Bloqueado). El filtro "Solicitudes" muestra los usuarios activos con rol SIN_ACCESO.
- El administrador puede asignar un rol, bloquear, desbloquear y eliminar usuarios.
- Bloquear no elimina la cuenta: un usuario bloqueado no puede iniciar sesión.
- Al eliminar un usuario se borran también las citas asociadas a su correo. Ambas operaciones se hacen en una transacción.
- Ningún administrador puede bloquearse, degradarse ni eliminarse a sí mismo. Desde esta pantalla no existe cambio de contraseña ni edición de datos de otros usuarios.
- Todas las operaciones pasan por `UsuariosController`, que verifica que quien actúa sea administrador.
- Pruebas: `UsuariosControllerTest` (reglas de acceso y de protección del propio usuario, sin base de datos).
- Limitación conocida: las contraseñas se guardan en texto plano.

## Pruebas automatizadas

Ejecuta `mvn test`. Actualmente hay 16 pruebas que pasan:

- `CitasControllerTest`: 6 pruebas de permisos, creación, modificación y horarios duplicados de citas.
- `ControlAccesoTest`: 5 pruebas de permisos por rol.
- `UsuariosControllerTest`: 4 pruebas de reglas de acceso y de protección del propio usuario.
- `AppTest`: 1 prueba básica.

## Pendientes

- Módulo de Facturación y Cuentas por Cobrar (Estudiante 3).
- Cifrar las contraseñas; actualmente se guardan en texto plano.
- Retirar el usuario de prueba antes de la entrega final.
