/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.evaluadorcrediticio; 
import java.util.Scanner;
import java.io.IOException;
/**
 *
 * @author Miguel
 */
public class EvaluadorCrediticio {

    static Cliente cliente;
    static Persona persona;
    static Usuario usuario;
    static ConexionBDD conexion;
    static Empresa empresa;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            conexion = new ConexionBDD();
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
        //System.out.println("\heramno!");     
        //usuario.inciarSesion();
        ingresarCliente(sc);
        //persona.comprobarHistorial(sc);
        System.out.println("JIJO");
    }
public static void ingresarCliente(Scanner sc){
    System.out.println("¿Quiere ingresar una empresa o una persona? (E/P)");
    String opcion = sc.nextLine();

    if (opcion.equalsIgnoreCase("P")) {
        Persona p = new Persona();
        p.setConexion(conexion);
        //p.ingresarCliente(); // Llama a pedir datos y luego hace el alta internamente
        //p.altaPersona();
        p.bajaCliente();
        p.bajaPersona();
    } else if (opcion.equalsIgnoreCase("E")) {
        Empresa e = new Empresa();
        e.setConexion(conexion);
        e.ingresarClienteEmpresa();
        e.ingresarEmpresa();
        e.altaEmpresa();
    }
    }
}
