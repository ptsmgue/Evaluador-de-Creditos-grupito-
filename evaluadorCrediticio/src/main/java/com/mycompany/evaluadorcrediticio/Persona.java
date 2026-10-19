package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
//miguel
public class Persona extends Cliente{
    private String nombrePersona;

    public Persona(double ingresos, double gastos, String historial, String nombrePersona){
        super(ingresos,gastos,historial);
        this.nombrePersona = nombrePersona;
        Scanner scanner = new Scanner(System.in);
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
        String nombre = sc.nextLine();
        System.out.print(nombre + " Es malo?: ");
        String op = sc.nextLine();
        evaluarHistorial(op);

    }
    
}
