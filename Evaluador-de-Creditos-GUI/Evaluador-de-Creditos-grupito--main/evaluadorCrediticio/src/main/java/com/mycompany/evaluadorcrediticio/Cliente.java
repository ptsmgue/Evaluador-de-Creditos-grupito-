package com.mycompany.evaluadorcrediticio;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

//@autor Miguel

/**
 * Datos y reglas comunes a Persona y Empresa.
 *
 * Esta clase ya no lee de consola ni imprime: recibe datos por parámetro,
 * lanza {@link IllegalArgumentException} si son inválidos y devuelve
 * resultados, para que cualquier interfaz (la gráfica, en este caso) decida
 * cómo mostrarlos.
 */
public abstract class Cliente {
    public String nombreCliente;
    public double ingresos;
    public double gastos;
    public String historial;
    protected ConexionBDD conx;

    /** Carga los parámetros de un procedimiento almacenado. */
    @FunctionalInterface
    interface Parametros {
        void aplicar(CallableStatement cst) throws SQLException;
    }

    public Cliente() {
    }

    public Cliente(String nombreCliente, double ingresos, double gastos, String historial, ConexionBDD conx) {
        this.nombreCliente = nombreCliente;
        this.ingresos = ingresos;
        this.gastos = gastos;
        this.historial = historial;
        this.conx = conx;
    }

    public void setConexion(ConexionBDD conexion) {
        this.conx = conexion;
    }

    /** Describe en una frase el historial guardado (según el vocabulario de cada tipo de cliente). */
    public abstract String evaluarHistorial(String op);

    /** Código de historial que se guarda en la BD para "buen historial" (true) o "mal historial" (false). */
    public abstract String codigoHistorial(boolean buenHistorial);

    /** true si el código recibido corresponde a un buen historial. */
    public boolean esBuenHistorial(String op) {
        return op != null && codigoHistorial(true).equalsIgnoreCase(op.trim());
    }

    // ------------------------------------------------------------------ validaciones

    /** Lanza IllegalArgumentException si los datos no alcanzan para dar de alta al cliente. */
    public abstract void validarAlta();

    /** Lanza IllegalArgumentException si los datos no alcanzan para dar de baja al cliente. */
    public abstract void validarBaja();

    protected static boolean esVacio(String s) {
        return s == null || s.isBlank();
    }

    /** Valida ingresos, gastos e historial (lo que comparten Persona y Empresa). */
    protected void validarFinanzas() {
        if (Double.isNaN(ingresos) || ingresos < 0) {
            throw new IllegalArgumentException("Los ingresos no pueden ser negativos.");
        }
        if (Double.isNaN(gastos) || gastos < 0) {
            throw new IllegalArgumentException("Los gastos no pueden ser negativos.");
        }
        if (esVacio(historial)) {
            throw new IllegalArgumentException("El historial es obligatorio.");
        }
    }

    private static final Pattern MONTO = Pattern.compile("\\d+([.,]\\d{1,2})?");

    /**
     * Convierte el texto de un campo de monto en número. Acepta punto o coma
     * como separador decimal (máx. 2 decimales) y rechaza separadores de miles
     * para no interpretar "1.500" como 1,5 por error.
     */
    public static double parsearMonto(String texto, String campo) {
        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) {
            throw new IllegalArgumentException(campo + ": ingrese un monto.");
        }
        if (!MONTO.matcher(t).matches()) {
            throw new IllegalArgumentException(campo + ": monto inválido. Use solo números, con punto o coma "
                    + "para los decimales (máximo 2) y sin separador de miles.");
        }
        return Double.parseDouble(t.replace(',', '.'));
    }

    // ------------------------------------------------------------------ base de datos

    /**
     * Ejecuta un procedimiento almacenado. Siguiendo la convención de la BD, si
     * el procedimiento devuelve un result set, su columna MensajeError indica
     * que la operación fue rechazada; si no devuelve nada, salió bien.
     */
    protected Resultado llamar(String sql, String mensajeExito, Parametros parametros) throws SQLException {
        if (conx == null) {
            throw new IllegalStateException("No hay una conexión a la base de datos configurada.");
        }
        try (Connection cnx = conx.abrir(); CallableStatement cst = cnx.prepareCall(sql)) {
            parametros.aplicar(cst);
            if (cst.execute()) {
                try (ResultSet rs = cst.getResultSet()) {
                    if (rs != null && rs.next()) {
                        String msj = rs.getString("MensajeError");
                        return new Resultado(false, msj != null ? msj : "La base de datos rechazó la operación.");
                    }
                }
            }
            return new Resultado(true, mensajeExito);
        }
    }
}
