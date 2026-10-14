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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        //System.out.println("\heramno!");          
        persona.generarHistorial(sc);
    }
}
