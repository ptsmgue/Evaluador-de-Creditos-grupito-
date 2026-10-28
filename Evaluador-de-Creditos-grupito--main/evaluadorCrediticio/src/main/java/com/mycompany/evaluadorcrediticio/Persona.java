package com.mycompany.evaluadorcrediticio;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
//@miguel
public class Persona extends Cliente{
    private String nombrePersona;
    private String apellido;
    private String dni;
    private final Scanner sc = new Scanner(System.in);

    public Persona(String nombreCliente, double ingresos, double gastos, String historial) {
        super(nombreCliente, ingresos, gastos, historial, null);
    }

    public Persona(String nombreCliente, double ingresos, double gastos, String historial, String nombrePersona,ConexionBDD conx){
        super(nombreCliente, ingresos, gastos, historial, conx);
        this.nombrePersona = nombrePersona;
        this.apellido = apellido;
        this.dni = dni;
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
    String sql = "call verHistorialPersona()";
  
     try {
        conx.conectar();
        CallableStatement cst = conx.cnx.prepareCall(sql);
        try (ResultSet rs = cst.executeQuery()){
              while (rs.next()) {
                String nombre = rs.getString("nombrePersona");
                String apellido = rs.getString("apellidoPersona");
                String historial = rs.getString("historial");
                
                double ingresos = rs.getDouble("ingresos");
                double gastos = rs.getDouble("gastos");
                
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
    public void altaPersona(){
            System.out.println("Ingrese su nombre");
            nombrePersona = sc.nextLine();
            System.out.println("Ingrese su apellido");
            apellido = sc.nextLine();
            System.out.println("Ingrese su DNI de la persona: ");
            dni = sc.nextLine();
            
                String query = "call altaPersona(?,?,?,?,?,?,?)";
                String msj = "";
            try {
                this.conx.conectar();
                CallableStatement cst = conx.cnx.prepareCall(query);{

                cst.setNString(1, nombreCliente);
                cst.setDouble(2, ingresos);
                cst.setDouble(3, gastos);
                cst.setNString(4, historial);
                cst.setNString(5, nombrePersona);
                cst.setNString(6, apellido);
                cst.setNString(7, dni);  
                boolean ejecucion = cst.execute();
                if (ejecucion) {
                ResultSet rs = cst.getResultSet();
                if (rs.next()) {
                    msj = rs.getString("MensajeError"); 
                }
            } else {
                msj = "Cliente persona ingresado correctamente";
            }
                }
            }
                catch (SQLException e){
                        msj = "Error al ingresar un cliente persona: " + e.getMessage();
                }
            this.conx.cerrar();
            System.out.println(msj);
    }

    
    public Persona() {
    }
}

/* 
    */