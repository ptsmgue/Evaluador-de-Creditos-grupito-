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
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            conexion = new ConexionBDD();
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
        
        persona = new Persona();
        //System.out.println("\heramno!");     
        //usuario.inciarSesion();
        persona.setConexion(conexion);
        persona.comprobarHistorial();
        System.out.println("JIJO");
    }
}
