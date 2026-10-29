package com.mycompany.evaluadorcrediticio;

/**
 * Resultado de una operación de alta/baja contra la base de datos.
 *
 * @param exito   true si el procedimiento almacenado no devolvió error
 * @param mensaje mensaje para mostrar al usuario (éxito o el error que informó la BD)
 */
public record Resultado(boolean exito, String mensaje) {
}
