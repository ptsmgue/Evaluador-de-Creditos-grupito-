package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
    //@autor Miguel

public abstract class Cliente {
    public String nombreCliente;
    public double ingresos;
    public double gastos;
    public String historial;
    private final Scanner scan = new Scanner(System.in);

        public Cliente(String nombreCliente, double ingresos, double gastos, String historial){            
            this.nombreCliente = nombreCliente;
            this.ingresos = ingresos;
            this.gastos = gastos;
            this.historial = historial;
        }

        public void ingresarClientePersona(){
            System.out.print("Ingrese el nombre como cliente: ");
            this.nombreCliente = scan.nextLine();
            System.out.print("Ingrese los ingresos del cliente: ");
            String strIngresos = scan.nextLine();
            this.ingresos = Double.parseDouble(strIngresos);
            System.out.print("Ingrese los gastos del cliente: ");
            String strGastos = scan.nextLine();
            gastos = Double.parseDouble(strGastos);
            System.out.print("Ingrese el historial del cliente: ");
            this.historial = scan.nextLine();
           /*  String opcion = "";
            System.out.print("Quiere ingresar una empresa o una persona? (E/P): ");
            opcion = scan.nextLine();
            if(opcion.equalsIgnoreCase("P")){
            }
            else if (opcion.equalsIgnoreCase("E")){
                System.out.println("Aun no we");
            }
            else {
                System.out.println("daa proba bien pajero");
            }*/
        }

        abstract void evaluarHistorial(String op);

        abstract void comprobarHistorial(Scanner sc);
        public Cliente(){
            
        }
    
}
