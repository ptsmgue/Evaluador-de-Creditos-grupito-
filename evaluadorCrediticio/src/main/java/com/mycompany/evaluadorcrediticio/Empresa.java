package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
    //@autor Leoncito

public class Empresa extends Cliente {
    private String nombreEmpresa;
    private String rubro;
    private String director;
    
    public Empresa(double ingresos, double gastos, String historial, String nombreEmpresa, String rubro, String director) {
        super(ingresos, gastos, historial);
        this.nombreEmpresa = nombreEmpresa;
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
}
