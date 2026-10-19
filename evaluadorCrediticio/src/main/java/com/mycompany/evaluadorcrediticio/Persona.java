package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
//@miguel
public class Persona extends Cliente{
    private String nombrePersona;

    public Persona(double ingresos, double gastos, String historial, String nombrePersona){
        super(ingresos,gastos,historial);
        this.nombrePersona = nombrePersona;
        Scanner scanner = new Scanner(System.in);
    }
    public void evaluarHistorialPersona(String op){
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
    public void comprobarHistorialPersona(Scanner sc){
        System.out.println("================Historial crediticio de persona========================");
        System.out.print("Como te llamas we: ");
        nombrePersona = sc.nextLine();
        System.out.print(nombre + " es malo?: ");
        String op = sc.nextLine();
        evaluarHistorialPersona(op);


    }
    public Persona() {
    }
}


/**
 * public void comprobarHistorial(){
 *  String sql = "call comprobarHistorial()"
 * 
 *  try{
 *      conexion.conectar();
 *      CallableStatement cst = conexion.cnx.prepareCall(sql);
 *      cst.setNString(1,)
 * 
 * }
 *  
 * }
 * 
 */