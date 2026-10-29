package com.mycompany.evaluadorcrediticio;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//@miguel
public class Persona extends Cliente {
    private String nombrePersona;
    private String apellido;
    private String dni;

    private static final Pattern DNI = Pattern.compile("\\d{7,8}");

    /** Una fila del listado que devuelve verHistorialPersona. */
    public record Resumen(String nombre, String apellido, double ingresos, double gastos, String historial) {
    }

    public Persona() {
    }

    /**
     * El nombreCliente que se guarda en la BD se arma como "nombre apellido".
     */
    public Persona(String nombrePersona, String apellido, String dni,
            double ingresos, double gastos, String historial, ConexionBDD conx) {
        super(nombreCompleto(nombrePersona, apellido), ingresos, gastos, historial, conx);
        this.nombrePersona = nombrePersona == null ? null : nombrePersona.trim();
        this.apellido = apellido == null ? null : apellido.trim();
        this.dni = dni == null ? null : dni.trim();
    }

    private static String nombreCompleto(String nombre, String apellido) {
        return ((nombre == null ? "" : nombre.trim()) + " " + (apellido == null ? "" : apellido.trim())).trim();
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    /** Historial de persona: "no" = sin problemas (bueno), "Si" = con problemas (malo). */
    @Override
    public String evaluarHistorial(String op) {
        String o = op == null ? "" : op.trim().toLowerCase();
        return switch (o) {
            case "no" -> "El historial de la persona es bueno";
            case "si", "sí" -> "El historial de la persona es malo";
            default -> "Historial no reconocido";
        };
    }

    @Override
    public String codigoHistorial(boolean buenHistorial) {
        return buenHistorial ? "no" : "Si";
    }

    // ------------------------------------------------------------------ validaciones

    private void validarIdentidad() {
        if (esVacio(nombrePersona)) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (esVacio(apellido)) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (dni == null || !DNI.matcher(dni).matches()) {
            throw new IllegalArgumentException("El DNI debe tener 7 u 8 dígitos, sin puntos ni espacios.");
        }
    }

    @Override
    public void validarBaja() {
        validarIdentidad();
    }

    @Override
    public void validarAlta() {
        validarIdentidad();
        validarFinanzas();
    }

    // ------------------------------------------------------------------ base de datos

    public Resultado altaPersona() throws SQLException {
        validarAlta();
        return llamar("call altaPersona(?,?,?,?,?,?,?)", "Cliente persona ingresado correctamente", cst -> {
            cst.setNString(1, nombreCliente);
            cst.setDouble(2, ingresos);
            cst.setDouble(3, gastos);
            cst.setNString(4, historial);
            cst.setNString(5, nombrePersona);
            cst.setNString(6, apellido);
            cst.setNString(7, dni);
        });
    }

    public Resultado bajaPersona() throws SQLException {
        validarBaja();
        return llamar("call bajaPersona(?,?)", "Cliente persona eliminado correctamente", cst -> {
            cst.setNString(1, dni);
            cst.setNString(2, nombreCliente);
        });
    }

    /** Personas registradas, tal como las devuelve el procedimiento verHistorialPersona. */
    public static List<Resumen> listarPersonas(ConexionBDD conx) throws SQLException {
        if (conx == null) {
            throw new IllegalStateException("No hay una conexión a la base de datos configurada.");
        }
        List<Resumen> lista = new ArrayList<>();
        try (Connection cnx = conx.abrir();
                CallableStatement cst = cnx.prepareCall("call verHistorialPersona()");
                ResultSet rs = cst.executeQuery()) {
            while (rs.next()) {
                lista.add(new Resumen(
                        rs.getString("nombrePersona"),
                        rs.getString("apellidoPersona"),
                        rs.getDouble("ingresos"),
                        rs.getDouble("gastos"),
                        rs.getString("historial")));
            }
        }
        return lista;
    }
}
