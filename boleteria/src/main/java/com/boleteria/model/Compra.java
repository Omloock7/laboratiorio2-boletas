package com.boleteria.model;
import java.util.List;

public class Compra {

    private String cedulaComprador; // Cambiado a String por si acaso, o déjalo en int si tu lógica lo requiere
    private String nombreComprador;
    private String correoComprador;
    private List<Asiento> asientosSeleccionados;
    private double subtotal;
    private double servicio;
    private double total;

    // CORRECCIÓN: El constructor debe llamarse igual que la clase (Compra)
    public Compra(String cedula, String nombre, String correo, List<Asiento> asientosSeleccionados) {
        this.nombreComprador = nombre;
        this.cedulaComprador = cedula;
        this.correoComprador = correo;
        this.asientosSeleccionados = asientosSeleccionados;
        calcularTotales();
    }

    private void calcularTotales() {
        this.subtotal = 0.0;
        for (Asiento asiento : asientosSeleccionados) {
            this.subtotal += asiento.getPrecio(); // Asegúrate de tener getPrecio() en Asiento.java
        }
        this.servicio = this.subtotal * 0.10;
        this.total = this.subtotal + this.servicio;
    }

    // Getters para mostrar el resumen de la compra
    public double getSubtotal() { return subtotal; }
    public double getServicio() { return servicio; }
    public double getTotal() { return total; }
    
    public void mostrarResumen() {
        System.out.println("--- Resumen de Compra ---");
        System.out.println("Comprador: " + nombreComprador + " (Doc: " + cedulaComprador + ")");
        System.out.println("Asientos seleccionados: " + asientosSeleccionados.size());
        System.out.println("Subtotal: $" + subtotal);
        System.out.println("Servicio (10%): $" + servicio);
        System.out.println("Total a pagar: $" + total);
    }
}