package com.mycompany.evaluadorcrediticio;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    public String nombreUsuario;
    public String contrasena;

    private static Usuario usuarioActual;

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    /** Valida las credenciales en la BD y, si son correctas, deja al usuario como sesión actual. */
    public static boolean iniciarSesion(String nombre, String contra) {
        if (nombre == null || contra == null) {
            return false;
        }
        
        ConexionBDD db = new ConexionBDD();
        try (Connection conn = db.abrir();
             CallableStatement stmt = conn.prepareCall("{call sp_iniciar_sesion(?, ?)}")) {
            
            stmt.setString(1, nombre.trim());
            stmt.setString(2, contra);
            
            try (ResultSet rs = stmt.executeQuery()) {
                verificarErrorSP(rs); // Chequea si el procedure devolvió un mensaje de error
                if (rs.next()) {
                    usuarioActual = new Usuario(rs.getString("nombreUsuario"), rs.getString("contrasena"));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /** Agrega un usuario en la BD. Lanza RuntimeException/IllegalArgumentException si falla. */
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

        ConexionBDD db = new ConexionBDD();
        try (Connection conn = db.abrir();
             CallableStatement stmt = conn.prepareCall("{call sp_registrar_usuario(?, ?)}")) {
            
            stmt.setString(1, n);
            stmt.setString(2, contra);
            
            // Usamos execute() porque puede devolver un ResultSet (si hubo error) o un update count (si anduvo bien)
            boolean hasResultSet = stmt.execute();
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    verificarErrorSP(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión al registrar usuario: " + e.getMessage());
        }
    }

    /** Elimina un usuario de la BD. No permite borrar al que tiene la sesión abierta. */
    public static void eliminarUsuario(String nombre) {
        String n = nombre == null ? "" : nombre.trim();
        
        if (usuarioActual != null && n.equalsIgnoreCase(usuarioActual.nombreUsuario)) {
            throw new IllegalArgumentException("No podés eliminar al usuario con el que iniciaste sesión.");
        }

        ConexionBDD db = new ConexionBDD();
        try (Connection conn = db.abrir();
             CallableStatement stmt = conn.prepareCall("{call sp_eliminar_usuario(?)}")) {
            
            stmt.setString(1, n);
            
            boolean hasResultSet = stmt.execute();
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    verificarErrorSP(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión al eliminar usuario: " + e.getMessage());
        }
    }

    /** Consulta la lista actual de usuarios desde la BD. */
    public static List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        ConexionBDD db = new ConexionBDD();
        
        try (Connection conn = db.abrir();
             CallableStatement stmt = conn.prepareCall("{call sp_listar_usuarios()}");
             ResultSet rs = stmt.executeQuery()) {
            
            verificarErrorSP(rs);
            while (rs.next()) {
                lista.add(new Usuario(rs.getString("nombreUsuario"), rs.getString("contrasena")));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private static Usuario buscar(String nombre) {
        ConexionBDD db = new ConexionBDD();
        try (Connection conn = db.abrir();
             CallableStatement stmt = conn.prepareCall("{call sp_buscar_usuario(?)}")) {
            
            stmt.setString(1, nombre);
            
            try (ResultSet rs = stmt.executeQuery()) {
                verificarErrorSP(rs);
                if (rs.next()) {
                    return new Usuario(rs.getString("nombreUsuario"), rs.getString("contrasena"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Revisa si el ResultSet contiene la columna "MensajeError" generada por el EXIT HANDLER de MySQL.
     * Si la encuentra, lanza una excepción para cortar el flujo, tal como si hubiera fallado el SQL.
     */
    private static void verificarErrorSP(ResultSet rs) throws SQLException {
        if (rs != null && rs.getMetaData().getColumnCount() > 0) {
            if (rs.getMetaData().getColumnName(1).equalsIgnoreCase("MensajeError")) {
                if (rs.next()) {
                    throw new RuntimeException(rs.getString("MensajeError"));
                }
            }
        }
    }
}