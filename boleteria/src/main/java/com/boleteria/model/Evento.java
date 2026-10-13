package com.boleteria.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.boleteria.exception.DatosInvalidosException;

/**
 * Información general de un evento disponible para la venta.
 */
public class Evento {

    private String idEvento, nombre, fecha, hora, lugar, tipoEvento;

    public Evento(String idEvento, String nombre, String fecha, String hora, String lugar, String tipoEvento) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.lugar = lugar;
        this.tipoEvento = tipoEvento;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getLugar() {
        return lugar;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public static List<Evento> cargarEventos() throws IOException, DatosInvalidosException {
    Path ruta = Path.of("data", "eventos.csv");
    if (!Files.exists(ruta)) {
        // Si no se encuentra en la raíz del workspace, busca dentro de la carpeta del subproyecto
        ruta = Path.of("laboratiorio2-boletas", "data", "eventos.csv");
    }
    return cargarEventos(ruta);
}

    public static List<Evento> cargarEventos(Path ruta) throws IOException, DatosInvalidosException {
        List<Evento> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(ruta)) {
            String l = br.readLine();
            int n = 1;
            if (l == null) {
                throw new DatosInvalidosException("El CSV de eventos está vacío.");
            
            }while ((l = br.readLine()) != null) {
                n++;
                if (l.isBlank()) {
                    continue;
                
                }String[] d = l.split(";", -1);
                if (d.length != 6) {
                    throw new DatosInvalidosException("Formato incorrecto en eventos.csv, línea " + n);
                
                }for (int i = 0; i < d.length; i++) {
                    d[i] = d[i].trim();
                
                }for (String s : d) {
                    if (s.isEmpty()) {
                        throw new DatosInvalidosException("Campo vacío en eventos.csv, línea " + n);
                
                    }
                }out.add(new Evento(d[0], d[1], d[2], d[3], d[4], d[5]));
            }
        }
        return out;
    }

    @Override
    public String toString() {
        return idEvento + " | " + nombre + " | " + fecha + " " + hora + " | " + lugar + " | " + tipoEvento;
    }
}