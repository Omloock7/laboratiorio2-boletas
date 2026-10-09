package com.boleteria.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Compra realizada por un titular. Los totales se calculan a partir de los
 * asientos.
 */
public class Compra {

    private String idCompra, idEvento, cedulaComprador, nombreComprador, correoComprador, fechaCompra;
    private List<String> codigosAsientos;
    private double subtotal, servicio, total;

    public Compra(String idEvento, String cedula, String nombre, String correo, List<Asiento> asientos) {
        this("CMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), idEvento, cedula, nombre, correo, codigos(asientos), subtotalDe(asientos), null);
    }

    public Compra(String idCompra, String idEvento, String cedula, String nombre, String correo, List<String> codigos, double subtotal, String fechaCompra) {
        this.idCompra = idCompra;
        this.idEvento = idEvento;
        this.cedulaComprador = cedula;
        this.nombreComprador = nombre;
        this.correoComprador = correo;
        this.codigosAsientos = new ArrayList<>(codigos);
        this.subtotal = subtotal;
        this.servicio = Math.round(subtotal * 0.10 * 100.0) / 100.0;
        this.total = subtotal + servicio;
        this.fechaCompra = fechaCompra == null ? LocalDateTime.now().toString() : fechaCompra;
    }

    private static List<String> codigos(List<Asiento> a) {
        List<String> c = new ArrayList<>();
        for (Asiento x : a) {
            c.add(x.getCodigoAsiento());
        
        }return c;
    }

    private static double subtotalDe(List<Asiento> a) {
        double s = 0;
        for (Asiento x : a) {
            s += x.getPrecio();
        
        }return s;
    }

    public String getIdCompra() {
        return idCompra;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getCedulaComprador() {
        return cedulaComprador;
    }

    public String getNombreComprador() {
        return nombreComprador;
    }

    public String getCorreoComprador() {
        return correoComprador;
    }

    public List<String> getCodigosAsientos() {
        return codigosAsientos;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getServicio() {
        return servicio;
    }

    public double getTotal() {
        return total;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void mostrarResumen() {
        System.out.println("\n--- Resumen de compra ---");
        System.out.println("ID: " + idCompra + " | Titular: " + nombreComprador + " | Documento: " + cedulaComprador);
        System.out.println("Asientos: " + String.join(", ", codigosAsientos));
        System.out.printf("Subtotal: $%.0f%nServicio (10%%): $%.0f%nTotal: $%.0f%n", subtotal, servicio, total);
    }
}
