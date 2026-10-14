    package com.mycompany.evaluadorcrediticio;

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

        abstract void evaluarHistorial();

        abstract void generarHistorial();

        public String getNombre(){
            return this.nombre;
        }
    
}
