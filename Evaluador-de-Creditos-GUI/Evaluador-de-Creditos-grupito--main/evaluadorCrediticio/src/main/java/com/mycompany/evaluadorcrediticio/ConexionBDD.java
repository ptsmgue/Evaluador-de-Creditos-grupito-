package com.mycompany.evaluadorcrediticio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Lee la configuración de conexion.txt (servidor:puerto,base,usuario,clave) y
 * entrega conexiones nuevas a MySQL.
 *
 * Cada llamada a {@link #abrir()} devuelve una conexión propia que quien la
 * pide debe cerrar (try-with-resources). Así no hay estado compartido entre
 * operaciones y los errores llegan a quien llamó, en vez de perderse en consola.
 */
public class ConexionBDD {

    /** Lugares donde se busca conexion.txt, en este orden. */
    private static final List<Path> UBICACIONES = List.of(
            Path.of("conexion.txt"),
            Path.of("src", "main", "conexion.txt"),
            Path.of("..", "conexion.txt"));

    String url, server, bdd, user, pwd;

    /** Si la configuración no se pudo leer, acá queda el motivo (se informa al abrir). */
    private String errorConfiguracion;

    public ConexionBDD() {
        Path archivo = UBICACIONES.stream().filter(Files::isRegularFile).findFirst().orElse(null);
        if (archivo == null) {
            errorConfiguracion = "No se encontró el archivo conexion.txt (se buscó en: "
                    + UBICACIONES.stream().map(Path::toString).toList() + ").";
            return;
        }
        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
            String linea = lineas.isEmpty() ? "" : lineas.get(0).replace("\uFEFF", "").stripTrailing();
            String[] partes = linea.split(",", 4);
            if (partes.length < 4) {
                errorConfiguracion = "conexion.txt tiene un formato inválido. "
                        + "Se esperaba: servidor:puerto,base,usuario,clave";
                return;
            }
            server = partes[0].trim();
            bdd = partes[1].trim();
            user = partes[2].trim();
            pwd = partes[3];
            url = "jdbc:mysql://" + server + "/" + bdd
                    + "?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=5000";
        } catch (IOException e) {
            errorConfiguracion = "Error al leer conexion.txt: " + e.getMessage();
        }
    }

    /** Abre una conexión nueva. Quien la recibe es responsable de cerrarla. */
    public Connection abrir() throws SQLException {
        if (errorConfiguracion != null) {
            throw new SQLException(errorConfiguracion);
        }
        return DriverManager.getConnection(url, user, pwd);
    }
}
