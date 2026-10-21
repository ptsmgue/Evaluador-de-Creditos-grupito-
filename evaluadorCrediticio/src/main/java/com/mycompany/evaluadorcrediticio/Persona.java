package com.mycompany.evaluadorcrediticio;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
//@miguel
public class Persona extends Cliente{
    private String nombrePersona;
    ConexionBDD conx;

    public Persona(double ingresos, double gastos, String historial, String nombrePersona,ConexionBDD conx){
        super(ingresos,gastos,historial);
        this.nombrePersona = nombrePersona;
        this.conx = conx;
        Scanner scanner = new Scanner(System.in);
    }
    public void setConexion(ConexionBDD conexionDesdeMain) {
    this.conx = conexionDesdeMain;
    }
    public void evaluarHistorial(String op){
        if (op.equals("Si") || op.equals("sis")){
                System.out.println("Mal historial ;v");
        }
        else if (op.equals("no") || op.equals("non")){
            System.out.println("Buen historial :D");
        }
        else {
            System.out.println("Sos un pete un laburo tenes");
        }
    }
    public void comprobarHistorial(Scanner sc){
        System.out.println("================Historial crediticio de persona========================");
        System.out.print("Como te llamas we: ");
        nombrePersona = sc.nextLine();
        System.out.print(nombre + " es malo?: ");
        String op = sc.nextLine();
        evaluarHistorial(op);


    }
    public void comprobarHistorial(){
    String sql = "call verHistorialPersona()";
  
     try {
        conx.conectar();
        CallableStatement cst = conx.cnx.prepareCall(sql);
        try (ResultSet rs = cst.executeQuery()){
            System.out.println("Historial crediticio de personas");
              while (rs.next()) {
                // 1. Extraemos los textos (VARCHAR) usando getString
                String nombre = rs.getString("nombrePersona");
                String apellido = rs.getString("apellidoPersona");
                String historial = rs.getString("historial"); // Asumo que es texto, si es número usa getInt("historial")
                
                // 2. Extraemos los valores monetarios usando getDouble (o getInt si no usas decimales)
                double ingresos = rs.getDouble("ingresos");
                double gastos = rs.getDouble("gastos");
                
                // 3. Imprimimos el resultado con un formato ordenado
                System.out.println("Nombre: " + nombre + " " + apellido 
                                + " | Ingresos: $" + ingresos 
                                + " | Gastos: $" + gastos 
                                + " | Historial: " + historial);
                }
        }

            catch (SQLException e){
                System.out.println("Error al comprobar historial crediticio de personas: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        } finally {
            conx.cerrar();
        }
    }
  
    public Persona() {
    }
}


/* 

 /*
    public void altaPersona(String nombrePersona,String apellido,String dni,int idCliente){
    String sql, query = "call altaPersona(?,?,?,?)";

    try (Connection conexion = new ConexionBDD.conectar();
       CallableStatement cst = conexion.prepareCall(sql);{

       cst.setNString(1, nombrePersona);
       cst.setNString(2, apellido);
       cst.setNString(3, dni),
       cst.setInt(4, idCliente)
       try (ResultSet rs = cst.executeQuery(sql)){
            System.out.println("Alta de cliente persona");        
       }
        catch (SQLException e){
                System.out.println("Error al ingresar un cliente persona: " + e.getMessage());
            }
}
 //*/