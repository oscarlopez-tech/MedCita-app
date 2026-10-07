# MedCita-app
Este proyecto es un sistema de gestión para clínica médica. Actualmente incluye una interfaz de escritorio en Java Swing para inicio de sesión, registro de usuarios y recuperación de contraseña.

## Ejecución

Se requiere Java 21 y Maven. Desde la raíz del proyecto, ejecuta:

```shell
mvn clean compile exec:java
```

Las operaciones de autenticación y registro requieren acceso a la base de datos configurada en `ConexionController`.
