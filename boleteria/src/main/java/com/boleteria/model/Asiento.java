
package com.boleteria.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un asiento de un evento.
 */
public class Asiento {

    private String idEvento;
    private String codigoAsiento;
    private String localidad;
    private String fila;
    private int numero;
    private double precio;

    public Asiento(String idEvento, String codigoAsiento,
                   String localidad, String fila,
                   int numero, double precio) {
        this.idEvento = idEvento;
        this.codigoAsiento = codigoAsiento;
        this.localidad = localidad;
        this.fila = fila;
        this.numero = numero;
        this.precio = precio;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getCodigoAsiento() {
        return codigoAsiento;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public double getPrecio() {
        return precio;
    }

    /**
     * Lee los asientos desde data/asientos.csv.
     */
    public static List<Asiento> cargarAsientos() throws IOException {
        List<Asiento> asientos = new ArrayList<>();

        Path ruta = Path.of("data", "asientos.csv");

        try (BufferedReader lector = Files.newBufferedReader(ruta)) {
            String linea;
            boolean primeraLinea = true;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;

                if (linea.isBlank()) {
                    continue;
                }

                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezado
                }

                String[] datos = linea.split(";", -1);

                if (datos.length != 6) {
                    throw new IOException(
                        "Formato incorrecto en asientos.csv, línea "
                        + numeroLinea
                    );
                }

                try {
                    String idEvento = datos[0].trim();
                    String codigo = datos[1].trim();
                    String localidad = datos[2].trim();
                    String fila = datos[3].trim();
                    int numero = Integer.parseInt(datos[4].trim());
                    double precio = Double.parseDouble(datos[5].trim());

                    if (idEvento.isEmpty()
                            || codigo.isEmpty()
                            || localidad.isEmpty()
                            || fila.isEmpty()
                            || numero <= 0
                            || precio < 0) {
                        throw new IllegalArgumentException(
                            "Hay datos vacíos o valores inválidos."
                        );
                    }

                    asientos.add(new Asiento(
                        idEvento, codigo, localidad,
                        fila, numero, precio
                    ));

                } catch (NumberFormatException e) {
                    throw new IOException(
                        "Número o precio inválido en asientos.csv, línea "
                        + numeroLinea, e
                    );
                } catch (IllegalArgumentException e) {
                    throw new IOException(
                        "Datos inválidos en asientos.csv, línea "
                        + numeroLinea, e
                    );
                }
            }
        }

        return asientos;
    }
}