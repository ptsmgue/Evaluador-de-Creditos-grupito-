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

int opcion = 0;
do{
    System.out.println("Menu de Admin ");
    System.out.println("1. Registar Usuario");
    System.out.println("2. Eliminar Usuario");
    System.out.println("3. Listar Usuario");
    System.out.println("4. Salir");
    System.out.println("Elige una opcion: ");

    if (scanner.hasNextInt()){
        opcion = 0;

        switch(opcion) {
            case 1:
                System.out.println("Proximamente");
                break;
            case 2:
                System.out.println("Proximamente");
                break;
            case 3:
                System.out.println("Proximamente");
                break;
            default:
                System.out.println("Opcion no valida");
        }
    }else{
        System.out.println("Ingrese un numero");
        scanner.next();
    }
} while (opcion != 4);
scanner.close();

}


public void registarUsuario(){

}

public void eliminarUsuario(){

}

public void listarUsuario(){
    
}
public Usuario(){

}

    
}
