package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
    //@autor Miguel

public abstract class Cliente {
    public String nombreCliente;
    public double ingresos;
    public double gastos;
    public String historial;
    protected ConexionBDD conx;
    private final Scanner scan = new Scanner(System.in);

        public Cliente(String nombreCliente, double ingresos, double gastos, String historial, ConexionBDD conx) {            
            this.nombreCliente = nombreCliente;
            this.ingresos = ingresos;
            this.gastos = gastos;
            this.historial = historial;
            this.conx = conx;
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
        }

        public void ingresarClienteEmpresa(){
            System.out.print("Ingrese el nombre de la empresa: ");
            this.nombreCliente = scan.nextLine();
            System.out.print("Ingrese los ingresos de la empresa: ");
            String strIngresos = scan.nextLine();
            this.ingresos = Double.parseDouble(strIngresos);
            System.out.print("Ingrese los gastos de la empresa: ");
            String strGastos = scan.nextLine();
            gastos = Double.parseDouble(strGastos);
            System.out.print("Ingrese el historial de la empresa: ");
            this.historial = scan.nextLine();
        }

        abstract void evaluarHistorial(String op);
        public void setConexion(ConexionBDD conexion) {
            this.conx = conexion;
        }
        abstract void comprobarHistorial(Scanner sc);
        public Cliente(){
            
        }
    
}
