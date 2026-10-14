package com.mycompany.evaluadorcrediticio;
//miguel
public class Persona extends Cliente{
    private String nombrePersona;

    public Persona(String nombre, double ingresos, double gastos, String historial, String nombrePersona){
        super(nombre,ingresos,gastos,historial);
        this.nombrePersona = nombrePersona;
    }

    public String getNombre(){
        return "El nombre";
    }
    public void evaluarHistorial(){
        System.out.println("Me maneje");        
    }
    public void generarHistorial(){
        System.out.println("================Historial crediticio de persona========================");
        

    }
    
}
