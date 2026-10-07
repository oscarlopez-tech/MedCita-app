package com.tercergrupo.model;

public class User {
    private String nombre;
    private String apellido;
    private String telefono;
    private String correoElectronico;
    private String usuario;
    private String contraseña;
    private String preguntaSeguridad;
    private String respuestaSeguridad;

    public User() {
    }

    public User(String nombre, String apellido, String telefono, String correoElectronico, String usuario,
            String contraseña) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar en blanco o Nulo");
        }
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar en blanco o Nulo");
        }
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        if (correoElectronico == null || !correoElectronico.contains("@") || correoElectronico.isBlank()) {
            throw new IllegalArgumentException("Correo Electronico Invalido");
        }
        this.correoElectronico = correoElectronico;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Usuario Incorrecto, intente nuevamente");
        }
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        if (contraseña == null || contraseña.length() < 8 || contraseña.isBlank()) {
            throw new IllegalArgumentException("La contraseña tiene que utilizar un minimo de 8 caracteres");
        }
        this.contraseña = contraseña;
    }

    public String getPreguntaSeguridad() {
        return preguntaSeguridad;
    }

    public void setPreguntaSeguridad(String preguntaSeguridad) {
        if (preguntaSeguridad == null || preguntaSeguridad.isBlank()) {
            throw new IllegalArgumentException("La pregunta de seguridad es obligatoria");
        }
        this.preguntaSeguridad = preguntaSeguridad;
    }

    public String getRespuestaSeguridad() {
        return respuestaSeguridad;
    }

    public void setRespuestaSeguridad(String respuestaSeguridad) {
        if (respuestaSeguridad == null || respuestaSeguridad.isBlank()) {
            throw new IllegalArgumentException("La respuesta de seguridad es obligatoria");
        }
        this.respuestaSeguridad = respuestaSeguridad;
    }
}
