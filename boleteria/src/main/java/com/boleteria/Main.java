package com.boleteria;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.boleteria.exception.DatosInvalidosException;
import com.boleteria.model.Asiento;
import com.boleteria.model.Compra;
import com.boleteria.model.Evento;
import com.boleteria.model.ReporteEvento;
import com.boleteria.service.BoleteriaService;
import com.boleteria.service.PdfQrService;
import com.boleteria.service.ReporteService;

/**
 * Punto de entrada de la aplicación de consola.
 */
public class Main {

    public static void main(String[] args) {
        try {
            BoleteriaService servicio = new BoleteriaService();
            PdfQrService pdf = new PdfQrService();
            ReporteService reportes = new ReporteService();
            Scanner sc = new Scanner(System.in);
            boolean seguir = true;
            while (seguir) {
                System.out.println("\n=== SISTEMA DE BOLETERÍA ===\n1. Listar eventos\n2. Ver asientos y disponibilidad\n3. Comprar boletas\n4. Reporte de ventas\n0. Salir");
                System.out.print("Seleccione una opción: ");
                String opcion = sc.nextLine().trim();
                try {
                    switch (opcion) {
                        case "1" ->
                            servicio.listarEventos().forEach(System.out::println);
                        case "2" ->
                            verAsientos(sc, servicio);
                        case "3" ->
                            comprar(sc, servicio, pdf);
                        case "4" -> {
                            List<ReporteEvento> datos = servicio.generarReporte();
                            reportes.imprimir(datos);
                            System.out.println("Gráfica creada: " + reportes.generarGrafica(datos).toAbsolutePath());
                        }
                        case "0" ->
                            seguir = false;
                        default ->
                            System.out.println("Opción no válida. Escriba 1, 2, 3, 4 o 0.");
                    }
                } catch (Exception e) {
                    System.out.println("No se pudo completar la operación: " + e.getMessage());
                    servicio.registrarError(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
                }
            }
            System.out.println("Gracias por usar el sistema.");
        } catch (IOException | DatosInvalidosException e) {
            System.err.println("No fue posible cargar los datos: " + e.getMessage());
        }
    }

    private static void verAsientos(Scanner sc, BoleteriaService s) throws Exception {
        for (Evento e : s.listarEventos()) {
            System.out.println(e);
        
        }System.out.print("ID del evento: ");
        String id = sc.nextLine().trim();
        List<Asiento> asientos = s.asientosDeEvento(id);
        System.out.println("Código | Localidad | Precio | Estado");
        for (Asiento a : asientos) {
            System.out.printf("%s | %s | $%.0f | %s%n", a.getCodigoAsiento(), a.getLocalidad(), a.getPrecio(), s.estaVendido(id, a.getCodigoAsiento()) ? "VENDIDO" : "DISPONIBLE");
    
        }}

    private static void comprar(Scanner sc, BoleteriaService s, PdfQrService pdf) throws Exception {
        for (Evento e : s.listarEventos()) {
            System.out.println(e);
        
        }System.out.print("ID del evento: ");
        String id = sc.nextLine().trim();
        List<Asiento> asientos = s.asientosDeEvento(id);
        Set<String> vendidos = new HashSet<>();
        for (Asiento a : asientos) {
            if (s.estaVendido(id, a.getCodigoAsiento())) {
                vendidos.add(a.getCodigoAsiento());
        
            }
        }for (Asiento a : asientos) {
            System.out.printf("%s | %s | $%.0f | %s%n", a.getCodigoAsiento(), a.getLocalidad(), a.getPrecio(), vendidos.contains(a.getCodigoAsiento()) ? "VENDIDO" : "DISPONIBLE");
        
        }System.out.print("Códigos de asiento separados por coma: ");
        List<String> codigos = Arrays.stream(sc.nextLine().split(",")).map(String::trim).filter(x -> !x.isEmpty()).toList();
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Documento: ");
        String doc = sc.nextLine().trim();
        System.out.print("Correo: ");
        String correo = sc.nextLine().trim();
        Compra c = s.comprar(id, doc, nombre, correo, codigos);
        c.mostrarResumen();
        Evento evento = s.buscarEvento(id);
        Path ticket = pdf.generarTicket(c, evento);
        System.out.println("Ticket PDF generado en: " + ticket.toAbsolutePath());
    }
}