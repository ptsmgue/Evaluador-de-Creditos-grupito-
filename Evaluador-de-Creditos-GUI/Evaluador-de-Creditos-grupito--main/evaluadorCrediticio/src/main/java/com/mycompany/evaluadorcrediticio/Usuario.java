package com.mycompany.evaluadorcrediticio;

import java.util.ArrayList;
import java.util.List;

/**
 * Usuarios del sistema.
 *
 * Por ahora la lista vive en memoria (no hay tabla de usuarios en la BD):
 * arranca con el usuario admin/1234 que ya existía y los cambios se pierden
 * al cerrar el programa. Todo el acceso pasa por los métodos estáticos de esta
 * clase, así que pasar a la BD más adelante no toca las ventanas.
 */
public class Usuario {
    public String nombreUsuario;
    public String contrasena;

    private static final List<Usuario> USUARIOS = new ArrayList<>();
    private static Usuario usuarioActual;

    static {
        USUARIOS.add(new Usuario("admin", "1234"));
    }

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    /** Valida las credenciales y, si son correctas, deja al usuario como sesión actual. */
    public static boolean iniciarSesion(String nombre, String contra) {
        if (nombre == null || contra == null) {
            return false;
        }
        for (Usuario u : USUARIOS) {
            if (u.nombreUsuario.equalsIgnoreCase(nombre.trim()) && u.contrasena.equals(contra)) {
                usuarioActual = u;
                return true;
            }
        }
        return false;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /** Agrega un usuario. Lanza IllegalArgumentException si los datos no son válidos. */
    public static void registrarUsuario(String nombre, String contra) {
        String n = nombre == null ? "" : nombre.trim();
        if (n.isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (n.contains(" ")) {
            throw new IllegalArgumentException("El nombre de usuario no puede tener espacios.");
        }
        if (contra == null || contra.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
        if (buscar(n) != null) {
            throw new IllegalArgumentException("Ya existe un usuario llamado \"" + n + "\".");
        }
        USUARIOS.add(new Usuario(n, contra));
    }

    /** Elimina un usuario. No permite borrar al que tiene la sesión abierta. */
    public static void eliminarUsuario(String nombre) {
        Usuario u = nombre == null ? null : buscar(nombre.trim());
        if (u == null) {
            throw new IllegalArgumentException("No existe el usuario indicado.");
        }
        if (u == usuarioActual) {
            throw new IllegalArgumentException("No podés eliminar al usuario con el que iniciaste sesión.");
        }
        USUARIOS.remove(u);
    }

    /** Copia de la lista actual de usuarios. */
    public static List<Usuario> listarUsuarios() {
        return new ArrayList<>(USUARIOS);
    }

    private static Usuario buscar(String nombre) {
        for (Usuario u : USUARIOS) {
            if (u.nombreUsuario.equalsIgnoreCase(nombre)) {
                return u;
            }
        }
        return null;
    }
}
