package com.boleteria.exception;

/**
 * Error cuando los datos ingresados o leídos no cumplen el formato esperado.
 */
public class DatosInvalidosException extends Exception {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }

    public DatosInvalidosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
