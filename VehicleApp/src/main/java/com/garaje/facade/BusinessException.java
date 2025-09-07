
package com.garaje.facade;

/**
 * Excepción para errores de reglas de negocio en la gestión de vehículos.
 * Permite separar los errores de validación de los errores técnicos (SQLException).
 */
public class BusinessException extends Exception {
    public BusinessException(String message) {
        super(message);
    }
}
