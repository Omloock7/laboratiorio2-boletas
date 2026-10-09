package com.boleteria.model;

/**
 * Indicadores calculados para un evento.
 */
public class ReporteEvento {

    private String idEvento, nombreEvento;
    private int asientosTotales, boletasVendidas;
    private double ingresos, ocupacion;

    public ReporteEvento(String id, String nombre, int total, int vendidas, double ingresos) {
        this.idEvento = id;
        this.nombreEvento = nombre;
        this.asientosTotales = total;
        this.boletasVendidas = vendidas;
        this.ingresos = ingresos;
        this.ocupacion = total == 0 ? 0 : (vendidas * 100.0 / total);
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public int getAsientosTotales() {
        return asientosTotales;
    }

    public int getBoletasVendidas() {
        return boletasVendidas;
    }

    public double getIngresos() {
        return ingresos;
    }

    public double getOcupacion() {
        return ocupacion;
    }
}
