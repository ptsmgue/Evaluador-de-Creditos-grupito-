package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;

public class Usuario{
public String nombreUsuario;
public String contrasena;

public Usuario(String nombreUsuario, String contrasena){
    this.nombreUsuario = nombreUsuario;
    this.contrasena = contrasena;
}

public static void inciarSesion(){
Scanner scanner = new Scanner(System.in);

String usuario = "admin";
String contra = "1234";


System.out.print("Ingrese el usario: ");
String usuariointput = scanner.nextLine();

System.out.print("Ingrese la contraseña: ");
String contraintput = scanner.nextLine();

if (usuariointput.equals(usuario) && contraintput.equals(contra)){
    System.out.println("Incio");
} else{
    System.out.println("pipipi");
}
}

public static void registarUsuario(){

}

public void eliminarUsuario(){

}

public void listarUsuario(){
    
}
public Usuario(){

}

    
}