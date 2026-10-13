package com.boleteria.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.boleteria.exception.DatosInvalidosException;

/**
 * Representa un asiento de un evento.
 */
public class Asiento {


    private String idEvento, codigoAsiento, localidad, fila;
    private int numero;
    private double precio;

    public Asiento(String idEvento, String codigoAsiento, String localidad, String fila, int numero, double precio) {
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
     * Lee los asientos del CSV y reporta la fila problemática sin ocultar el
     * error.
     */
    public static List<Asiento> cargarAsientos() throws IOException, DatosInvalidosException {
    Path ruta = Path.of("data", "asientos.csv");
    if (!Files.exists(ruta)) {
        // Si no existe en el directorio raíz, busca dentro de la subcarpeta del subproyecto
        ruta = Path.of("laboratiorio2-boletas", "data", "asientos.csv");
    }
    return cargarAsientos(ruta);
}

    public static List<Asiento> cargarAsientos(Path ruta) throws IOException, DatosInvalidosException {
        List<Asiento> lista = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(ruta)) {
            String linea = br.readLine();
            int n = 1;
            if (linea == null) {
                throw new DatosInvalidosException("El CSV de asientos está vacío.");
            }
            while ((linea = br.readLine()) != null) {
                n++;
                if (linea.isBlank()) {
                    continue;

                }
                String[] d = linea.split(";", -1);
                if (d.length != 6) {
                    throw new DatosInvalidosException("Formato incorrecto en asientos.csv, línea " + n);
                }
                try {
                    String evento = d[0].trim(), codigo = d[1].trim(), loc = d[2].trim(), fila = d[3].trim();
                    int num = Integer.parseInt(d[4].trim());
                    double precio = Double.parseDouble(d[5].trim());
                    if (evento.isEmpty() || codigo.isEmpty() || loc.isEmpty() || fila.isEmpty() || num < 1 || precio < 0) {
                        throw new IllegalArgumentException("Campos vacíos o valores negativos");
                    }
                    lista.add(new Asiento(evento, codigo, loc, fila, num, precio));
                } catch (IllegalArgumentException e) {
                    throw new DatosInvalidosException("Dato inválido en asientos.csv, línea " + n + ": " + e.getMessage(), e);
                }
            }
        }
        return lista;
    }
}