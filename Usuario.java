package com.mycompany.evaluadorcrediticio;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Usuario{
public String nombreUsuario;
public String contrasena;
ConexionBDD conx;

public Usuario(String nombreUsuario, String contrasena){
    this.nombreUsuario = nombreUsuario;
    this.contrasena = contrasena;
}

public static void inciarSesion(){
Scanner scanner = new Scanner(System.in);

String usuario = "admin";
String contra = "1234";


System.out.print("Ingrese el usario: ");
String usuariointput = scanner.nextLine();
System.out.print("Ingrese la contraseña: ");
String contraintput = scanner.nextLine();

if (usuariointput.equals(usuario) && contraintput.equals(contra)){
    System.out.println("Incio");
} else{
    System.out.println("pipipi");
}
}

 public void registrarUsuario(Scanner sc) {
        System.out.print("Nombre del nuevo usuario: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty() || nombre.length() > 10) {
            System.out.println("Nombre inválido (no puede estar vacío ni superar "
                    + 10 + " caracteres).");
            return;
        }

        System.out.print("Contraseña (mínimo " + 10 + " caracteres): ");
        String pass = sc.nextLine();
        if (pass.length() < 10) {
            System.out.println("Contraseña inválida: muy corta.");
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
                ps.setString(2, (pass));
                ps.executeUpdate();
                System.out.println("Usuario '" + nombre + "' registrado como ");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
        } finally {
            conx.cerrar();
        }
    }   



 public void eliminarUsuario(Scanner sc) {
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

public Usuario(){

}

}
