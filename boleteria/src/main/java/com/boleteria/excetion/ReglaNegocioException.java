package com.boleteria.exception;

/**
 * Error causado por una regla de la boletería, por ejemplo vender un asiento
 * ocupado.
 */
public class ReglaNegocioException extends Exception {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
