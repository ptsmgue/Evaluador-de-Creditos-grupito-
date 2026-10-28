package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.CallableStatement;
    //@autor Leoncito
    import java.sql.ResultSet;

public class Empresa extends Cliente {
    private String rubro;
    private String director;
    
    public Empresa(String nombreCliente, double ingresos, double gastos, String historial, String rubro, String director, ConexionBDD conx) {
        super(nombreCliente, ingresos, gastos, historial, conx);
        this.rubro = rubro;
        this.director = director;
    }

    @Override
    public void evaluarHistorial(String op) {
        if(op.equals("A")){
            System.out.println("El historial de la empresa es bueno");
        }else if(op.equals("B")){
            System.out.println("El historial de la empresa es regular");
        }else if(op.equals("C")){
            System.out.println("El historial de la empresa es malo");
        }else{
            System.out.println("Opción inválida");
        }
    }

    @Override
    public void comprobarHistorial(Scanner sc) {
        System.out.println("Ingrese el historial de la empresa (A, B o C): ");
        String op = sc.nextLine();
        evaluarHistorial(op);
    }

    public void ingresarEmpresa(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el rubro de la empresa: ");
        this.rubro = sc.nextLine();
        System.out.print("Ingrese el nombre del director de la empresa: ");
        this.director = sc.nextLine();
        
    }

    public void altaEmpresa(){
        String query = "call alta_empresa(?, ?, ?, ?, ?, ?)";
        String mensaje = "";
        System.out.println("Nombre de la empresa: " + nombreCliente);
        System.out.println("Ingresos: " + ingresos);
        System.out.println("Gastos: " + gastos);
        System.out.println("Historial: " + historial);
        System.out.println("Rubro: " + rubro);
        System.out.println("Director: " + director);
        try{
            conx.conectar();
            CallableStatement cst = conx.cnx.prepareCall(query);
            cst.setString(1, nombreCliente);
            cst.setDouble(2, ingresos);
            cst.setDouble(3, gastos);
            cst.setString(4, historial);
            cst.setString(5, rubro);
            cst.setString(6, director);
            boolean ejecucion = cst.execute();
            if (ejecucion) {
                ResultSet rs = cst.getResultSet();
                if (rs.next()) {
                    mensaje = rs.getString("MensajeError"); 
                }
            } else {
                mensaje = "Cliente persona ingresado correctamente";
            }
        }catch(SQLException e){
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            return;
        }finally{
            conx.cerrar();
        }
        System.out.println(mensaje);
    }

    public Empresa(){}


}
