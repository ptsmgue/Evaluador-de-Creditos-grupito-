package com.mycompany.evaluadorcrediticio;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;
import java.util.Scanner;

public class Usuario {

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_USUARIO = "USUARIO";

    private static final int LARGO_MIN_CONTRASENA = 4;
    private static final int LARGO_MAX_CAMPO = 200; 

    public String nombreUsuario;
    public String contrasena;
    public String rol;

    ConexionBDD conx;


    // Constructores
    public Usuario() {
    }

    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    public Usuario(String nombreUsuario, String contrasena, String rol) {
        this(nombreUsuario, contrasena);
        this.rol = rol;
    }

    public void setConexion(ConexionBDD conexionDesdeMain) {
        this.conx = conexionDesdeMain;
    }

    // Menú inicial
    public void menuPrincipal(Scanner sc) {
        int opcion;
        do {
            System.out.println("\n=== Evaluador Crediticio ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Salir");
            opcion = leerOpcion(sc, "Elige una opción: ");

            switch (opcion) {
                case 1 -> {
                    if (iniciarSesion(sc)) {
                        if (esAdmin()) {
                            menuAdmin(sc);
                        } else {
                            menuUsuario(sc);
                        }
                    }
                }
                case 2 -> System.out.println("Hasta luego.");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 2);
    }


    // Inicio de sesión
    public boolean iniciarSesion(Scanner sc) {
        System.out.print("Ingrese el usuario: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Ingrese la contraseña: ");
        String pass = sc.nextLine();

        if (!abrirConexion()) {
            return false;
        }

        String sql = "SELECT rol FROM usuario WHERE nombreUsuario = ? AND contrasena = ?";
        try (PreparedStatement ps = conx.cnx.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, hash(pass));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.nombreUsuario = nombre;
                    this.rol = rs.getString("rol");
                    System.out.println("Bienvenido, " + nombre + " (" + this.rol + ")");
                    return true;
                }
            }
            System.out.println("Usuario o contraseña incorrectos.");
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        } finally {
            conx.cerrar();
        }
        return false;
    }

    public void cerrarSesion() {
        this.nombreUsuario = null;
        this.contrasena = null;
        this.rol = null;
        System.out.println("Sesión cerrada.");
    }

    // Menú del Admin
    private void menuAdmin(Scanner sc) {
        int opcion;
        do {
            System.out.println("\n--- Menú de Admin ---");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Eliminar usuario");
            System.out.println("3. Listar usuarios");
            System.out.println("4. Cerrar sesión");
            opcion = leerOpcion(sc, "Elige una opción: ");

            switch (opcion) {
                case 1 -> registrarUsuario(sc);
                case 2 -> eliminarUsuario(sc);
                case 3 -> listarUsuarios();
                case 4 -> cerrarSesion();
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 4);
    }

    // Menú del Usuario normal
    private void menuUsuario(Scanner sc) {
        int opcion;
        do {
            System.out.println("\n--- Menú de Usuario ---");
            System.out.println("1. Ver historial crediticio de personas");
            System.out.println("2. Cerrar sesión");
            opcion = leerOpcion(sc, "Elige una opción: ");

            switch (opcion) {
                case 1 -> {
                    Persona persona = new Persona();
                    persona.setConexion(conx);
                    persona.comprobarHistorial(sc);
                }
                case 2 -> cerrarSesion();
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 2);
    }

    // Acciones exclusivas del Admin
    public void registrarUsuario(Scanner sc) {
        if (!esAdmin()) {
            System.out.println("Solo un admin puede registrar usuarios.");
            return;
        }

        System.out.print("Nombre del nuevo usuario: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty() || nombre.length() > LARGO_MAX_CAMPO) {
            System.out.println("Nombre inválido (no puede estar vacío ni superar "
                    + LARGO_MAX_CAMPO + " caracteres).");
            return;
        }

        System.out.print("Contraseña (mínimo " + LARGO_MIN_CONTRASENA + " caracteres): ");
        String pass = sc.nextLine();
        if (pass.length() < LARGO_MIN_CONTRASENA) {
            System.out.println("Contraseña inválida: muy corta.");
            return;
        }

        int opcionRol = leerOpcion(sc, "Rol (1 = Usuario, 2 = Admin): ");
        String nuevoRol;
        if (opcionRol == 1) {
            nuevoRol = ROL_USUARIO;
        } else if (opcionRol == 2) {
            nuevoRol = ROL_ADMIN;
        } else {
            System.out.println("Rol inválido.");
            return;
        }

        if (!abrirConexion()) {
            return;
        }

        try {
            if (existeUsuario(nombre)) {
                System.out.println("Ya existe un usuario llamado '" + nombre + "'.");
                return;
            }

            String sql = "INSERT INTO usuario (nombreUsuario, contrasena, rol) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conx.cnx.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, hash(pass));
                ps.setString(3, nuevoRol);
                ps.executeUpdate();
                System.out.println("Usuario '" + nombre + "' registrado como " + nuevoRol + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
        } finally {
            conx.cerrar();
        }
    }

    public void eliminarUsuario(Scanner sc) {
        if (!esAdmin()) {
            System.out.println("Solo un admin puede eliminar usuarios.");
            return;
        }

        System.out.print("Nombre del usuario a eliminar: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("Nombre inválido.");
            return;
        }
        if (nombre.equals(this.nombreUsuario)) {
            System.out.println("No podés eliminar tu propio usuario mientras tenés la sesión iniciada.");
            return;
        }

        System.out.print("¿Seguro que querés eliminar a '" + nombre + "'? (s/n): ");
        String confirmacion = sc.nextLine().trim();
        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (!abrirConexion()) {
            return;
        }

        String sql = "DELETE FROM usuario WHERE nombreUsuario = ?";
        try (PreparedStatement ps = conx.cnx.prepareStatement(sql)) {
            ps.setString(1, nombre);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Usuario '" + nombre + "' eliminado.");
            } else {
                System.out.println("No existe ningún usuario llamado '" + nombre + "'.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        } finally {
            conx.cerrar();
        }
    }

    public void listarUsuarios() {
        if (!esAdmin()) {
            System.out.println("Solo un admin puede listar usuarios.");
            return;
        }
        if (!abrirConexion()) {
            return;
        }

        String sql = "SELECT idUsuario, nombreUsuario, rol FROM usuario ORDER BY idUsuario";
        try (PreparedStatement ps = conx.cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Usuarios registrados ---");
            System.out.printf("%-5s %-30s %-10s%n", "ID", "Usuario", "Rol");
            int total = 0;
            while (rs.next()) {
                System.out.printf("%-5d %-30s %-10s%n",
                        rs.getInt("idUsuario"),
                        rs.getString("nombreUsuario"),
                        rs.getString("rol"));
                total++;
            }
            if (total == 0) {
                System.out.println("(no hay usuarios)");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        } finally {
            conx.cerrar();
        }
    }

    // Auxiliares
    private boolean esAdmin() {
        return ROL_ADMIN.equals(this.rol);
    }

    /** Usa la conexión ya abierta, por eso se llama dentro de un bloque con conectar(). */
    private boolean existeUsuario(String nombre) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE nombreUsuario = ?";
        try (PreparedStatement ps = conx.cnx.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean abrirConexion() {
        if (conx == null) {
            System.out.println("Error: no hay conexión configurada (falta setConexion).");
            return false;
        }
        conx.conectar();
        try {
            return conx.cnx != null && !conx.cnx.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private int leerOpcion(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String hash(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
