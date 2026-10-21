
package com.mycompany.evaluadorcrediticio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;


public class ConexionBDD {
    File archivo;
    String url, server, bdd, user, pwd;
    Connection cnx;
    
    public ConexionBDD() throws IOException {
        this.archivo = new File("conexion.txt");
        if(!archivo.exists()){
            archivo.createNewFile();
        }
        
        try(BufferedReader bf_extrae = new BufferedReader(new FileReader(this.archivo))){
            String linea = bf_extrae.readLine();
            String[] partes = linea.split(",");
            
            server = partes[0];
            bdd = partes[1];
            user = partes[2];
            pwd = partes[3];
            url = "jdbc:mysql://" + server + "/" + bdd + "?serverTimezone=UTC";        }
    }
    
    public void conectar(){
        try{
            System.out.println("\nConectando a la base de datos...");
            cnx = DriverManager.getConnection(url, user, pwd);
            System.out.println("Conexión exitosa a la base de datos.");
        }catch(SQLException e){
            System.out.println("\nError al conectar: " + e.getMessage());
        }
    }
    
    public void cerrar(){
        try {
            // Solo cerramos si la conexión existe y no está cerrada ya
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
            }
        } catch(SQLException e) {
            System.out.println("Error al cerrar la base de datos: " + e.getMessage());
        }
    }
}
