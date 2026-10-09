package com.boleteria.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.style.Styler;

import com.boleteria.model.ReporteEvento;

/**
 * Presenta indicadores y genera la gráfica solicitada usando XChart.
 */
public class ReporteService {

    public Path generarGrafica(List<ReporteEvento> datos) throws IOException {
        Path dir = Path.of("reportes");
        Files.createDirectories(dir);
        CategoryChart chart = new CategoryChartBuilder().width(1000).height(650).title("Ventas por evento").xAxisTitle("Evento").yAxisTitle("Ingresos (COP)").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        
        chart.addSeries("Ingresos", datos.stream().map(ReporteEvento::getNombreEvento).toList(), datos.stream().map(ReporteEvento::getIngresos).toList());
        Path salida = dir.resolve("ventas_por_evento.pdf");
        VectorGraphicsEncoder.saveVectorGraphic(chart, salida.toString(), VectorGraphicsEncoder.VectorGraphicsFormat.PDF);
        return salida;
    }

    public void imprimir(List<ReporteEvento> datos) {
        System.out.println("\n--- Reporte por evento ---");
        for (ReporteEvento r : datos) {
            System.out.printf("%s | Vendidas: %d/%d | Ocupación: %.2f%% | Ingresos: $%.0f%n", r.getNombreEvento(), r.getBoletasVendidas(), r.getAsientosTotales(), r.getOcupacion(), r.getIngresos());
    
        }}
}