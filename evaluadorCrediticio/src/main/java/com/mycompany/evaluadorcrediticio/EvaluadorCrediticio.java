/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.evaluadorcrediticio; 
import java.util.Scanner;
/**
 *
 * @author Miguel
 */
public class EvaluadorCrediticio {

    static Cliente cliente;
    static Persona persona = new Persona();
    static Usuario usuario = new Usuario();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        //System.out.println("\heramno!");     
        //usuario.inciarSesion();
        persona.comprobarHistorialPersona(sc);
        System.out.println("JIJO");
    }
}
