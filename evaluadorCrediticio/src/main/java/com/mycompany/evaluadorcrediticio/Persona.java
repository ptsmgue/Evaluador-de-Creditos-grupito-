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
    ConexionBDD conx;
    private final Scanner sc = new Scanner(System.in);

    public Persona(String nombreCliente, double ingresos, double gastos, String historial) {
        super(nombreCliente, ingresos, gastos, historial);
    }

    public Persona(String nombreCliente, double ingresos, double gastos, String historial, String nombrePersona,ConexionBDD conx){
        super(nombreCliente, ingresos, gastos, historial);
        this.nombrePersona = nombrePersona;
        this.apellido = apellido;
        this.dni = dni;
        this.conx = conx;
    }
    public void setConexion(ConexionBDD conexion) {
    this.conx = conexion;
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
            this.nombrePersona = sc.nextLine();
            System.out.println("Ingrese su apellido");
            this.apellido = sc.nextLine();
            System.out.println("Ingrese su DNI de la persona: ");
            this.dni = sc.nextLine();
            
                String query = "call altaPersona(?,?,?,?,?,?,?)";
                String msj = "";
            try {
                this.conx.conectar();
                CallableStatement cst = conx.cnx.prepareCall(query);{

                cst.setNString(1, this.nombreCliente);
                cst.setDouble(2, this.ingresos);
                cst.setDouble(3, this.gastos);
                cst.setNString(4, this.historial);
                cst.setNString(5, this.nombrePersona);
                cst.setNString(6, this.apellido);
                cst.setNString(7, this.dni);  
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

        public void bajaPersona(){
            System.out.print("Ingrese el dni de la persona a eliminar");
            this.dni = sc.nextLine();

                String query = "call bajaPersona(?,?)";
                String msj = "";
            try {
                this.conx.conectar();
                CallableStatement cst = conx.cnx.prepareCall(query);{

                cst.setNString(1, this.dni);
                cst.setNString(2, this.nombreCliente);  
                boolean ejecucion = cst.execute();
                if (ejecucion) {
                ResultSet rs = cst.getResultSet();
                if (rs.next()) {
                    msj = rs.getString("MensajeError"); 
                }
            } else {
                msj = "Cliente persona eliminado correctamente";
            }
                }
            }
                catch (SQLException e){
                        msj = "Error al eliminar un cliente persona: " + e.getMessage();
                }
            this.conx.cerrar();
            System.out.println(msj);
    }

            
    public void modificarPersona(){
            System.out.print("Ingrese el dni de la persona a la que quiere modificar: ");
            this.dni = sc.nextLine();
            System.out.print("Ingrese el nombre que quiere modificar: ");
            this.nombrePersona = sc.nextLine();
            System.out.print("Ingrese el apellido que quiere modificar: ");
            this.apellido = sc.nextLine();            

                String query = "call modificarPersona(?,?,?)";
                String msj = "";
            try {
                this.conx.conectar();
                CallableStatement cst = conx.cnx.prepareCall(query);{

                cst.setNString(1, this.nombrePersona);
                cst.setNString(2, this.apellido);
                cst.setNString(3, this.dni)  ;
                boolean ejecucion = cst.execute();
                if (ejecucion) {
                ResultSet rs = cst.getResultSet();
                if (rs.next()) {
                    msj = rs.getString("MensajeError"); 
                }
            } else {
                msj = "Cliente persona modificado correctamente";
            }
                }
            }
                catch (SQLException e){
                        msj = "Error al modificar un cliente persona: " + e.getMessage();
                }
            this.conx.cerrar();
            System.out.println(msj);
    }
    public Persona() {
    }
}

/* 
    */