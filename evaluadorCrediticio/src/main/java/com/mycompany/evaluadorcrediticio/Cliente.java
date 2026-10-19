    package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
    //@autor Miguel

public abstract class Cliente {
    public String nombre;
    public double ingresos;
    public double gastos;
    public String historial;

        public Cliente(double ingresos, double gastos, String historial){            
            this.ingresos = ingresos;
            this.gastos = gastos;
            this.historial = historial;
        }

        abstract void evaluarHistorialPersona(String op);

        abstract void comprobarHistorialPersona(Scanner sc);

        public String getNombre(){
            return this.nombre;
        }
        public Cliente(){
            
        }
    
}
