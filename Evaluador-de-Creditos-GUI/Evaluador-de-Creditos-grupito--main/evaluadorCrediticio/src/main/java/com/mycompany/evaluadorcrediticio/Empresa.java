package com.mycompany.evaluadorcrediticio;

import java.sql.SQLException;

//@autor Leoncito

public class Empresa extends Cliente {
    private String rubro;
    private String director;

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

    
}
