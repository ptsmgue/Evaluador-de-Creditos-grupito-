    package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
    //@autor Miguel

public abstract class Cliente {
    public String nombre;
    public double ingresos;
    public double gastos;
    public String historial;

        public Cliente(String nombre, double ingresos, double gastos, String historial){
            this.nombre = nombre;
            this.ingresos = ingresos;
            this.gastos = gastos;
            this.historial = historial;
        }

        abstract void evaluarHistorial(String op);

        abstract void generarHistorial(Scanner sc);

        public String getNombre(){
            return this.nombre;
        }
        public Cliente(){
            
        }
    
}
