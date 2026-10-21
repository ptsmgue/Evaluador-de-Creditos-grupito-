package com.mycompany.evaluadorcrediticio;
import java.util.Scanner;
import java.sql.SQLException;

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


public void registarUsuario(String nombreUsuario, String contrasena){
/*String dniString = new String(contrasena);  .getPassword() devuelve cifrado
                                            entonces, lo pasamos a String sin cifrar 
/*System.out.println(contrasenaString);
System.out.println(dniString.length()); //era para verificar
String response = "";
if (validarPwd(contrasenaString, 8)){ //verifica formato de DNI
    if (validarEmail(nombreUsuario)){ //verifica formato de email
        response = ;
    }else{
        response = "Nombre inválido";
        }
    } else {
        response = "contrasena inválido";
    }

    return response;
    */
}

public void eliminarUsuario(String nombreUsuario, String contrasena){
/*

*/
}

public Usuario(){

}

    
}
