package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
//miguel
public class Persona extends Cliente{
    private String nombrePersona;

    public Persona(String nombre, double ingresos, double gastos, String historial, String nombrePersona){
        super(nombre,ingresos,gastos,historial);
        this.nombrePersona = nombrePersona;
        Scanner scanner = new Scanner(System.in);
    }

    public String getNombre(){
        return "El nombre";
    }
    public void evaluarHistorial(String op){
        if (op.equals("si")){
                System.out.println("Mal historial ;v");
        }
        else if (op.equals("no")){
            System.out.println("Buen historial :D");
        }
        else {
            System.out.println("Sos un pete un laburo tenes");
        }
    }
    public void generarHistorial(Scanner sc){
        System.out.println("================Historial crediticio de persona========================");
        System.out.println("es malo?: ");
        String op = sc.nextLine();
        evaluarHistorial(op);

        /*
            
        */
        


    }
    public Persona() {

    }
    
}
