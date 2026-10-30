package com.mycompany.evaluadorcrediticio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//@autor Leoncito

public class Empresa extends Cliente {
    private String rubro;
    private String director;

    /** Listado de empresas (no hay procedimiento almacenado para esto, es una consulta directa). */
    private static final String SQL_LISTADO = "select c.nombreCliente, e.rubro, e.director, c.ingresos, c.gastos, c.historial "
            + "from Cliente as c join Empresa as e on c.idCliente = e.idCliente";

    /** Una fila del listado de empresas. */
    public record Resumen(String nombreCliente, String rubro, String director,
            double ingresos, double gastos, String historial) {
    }

    public Empresa() {
    }

    public Empresa(String nombreCliente, double ingresos, double gastos, String historial,
            String rubro, String director, ConexionBDD conx) {
        super(nombreCliente, ingresos, gastos, historial, conx);
        this.rubro = rubro;
        this.director = director;
    }

    public String getRubro() {
        return rubro;
    }

    public String getDirector() {
        return director;
    }

    /** Historial de empresa: A = bueno, B = regular, C = malo. */
    @Override
    public String evaluarHistorial(String op) {
        String o = op == null ? "" : op.trim().toUpperCase();
        return switch (o) {
            case "A" -> "El historial de la empresa es bueno";
            case "B" -> "El historial de la empresa es regular";
            case "C" -> "El historial de la empresa es malo";
            default -> "Historial no reconocido";
        };
    }

    @Override
    public String codigoHistorial(boolean buenHistorial) {
        return buenHistorial ? "A" : "C";
    }

    @Override
    public void validarBaja() {
        if (esVacio(nombreCliente)) {
            throw new IllegalArgumentException("Ingrese el nombre de la empresa a eliminar.");
        }
    }

    @Override
    public void validarAlta() {
        if (esVacio(nombreCliente)) {
            throw new IllegalArgumentException("El nombre de la empresa es obligatorio.");
        }
        if (esVacio(rubro)) {
            throw new IllegalArgumentException("El rubro es obligatorio.");
        }
        if (esVacio(director)) {
            throw new IllegalArgumentException("El director es obligatorio.");
        }
        validarFinanzas();
    }

    public Resultado altaEmpresa() throws SQLException {
        validarAlta();
        return llamar("call alta_empresa(?, ?, ?, ?, ?, ?)", "Empresa ingresada correctamente", cst -> {
            cst.setString(1, nombreCliente.trim());
            cst.setDouble(2, ingresos);
            cst.setDouble(3, gastos);
            cst.setString(4, historial);
            cst.setString(5, rubro.trim());
            cst.setString(6, director.trim());
        });
    }

    public Resultado bajaEmpresa() throws SQLException {
        validarBaja();
        return llamar("call baja_empresa(?)", "Empresa eliminada correctamente",
                cst -> cst.setString(1, nombreCliente.trim()));
    }

    /** Todas las empresas registradas. */
    public static List<Resumen> listarEmpresas(ConexionBDD conx) throws SQLException {
        if (conx == null) {
            throw new IllegalStateException("No hay una conexión a la base de datos configurada.");
        }
        List<Resumen> lista = new ArrayList<>();
        try (Connection cnx = conx.abrir();
                PreparedStatement ps = cnx.prepareStatement(SQL_LISTADO);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Resumen(
                        rs.getString("nombreCliente"),
                        rs.getString("rubro"),
                        rs.getString("director"),
                        rs.getDouble("ingresos"),
                        rs.getDouble("gastos"),
                        rs.getString("historial")));
            }
        }
        return lista;
    }
}
