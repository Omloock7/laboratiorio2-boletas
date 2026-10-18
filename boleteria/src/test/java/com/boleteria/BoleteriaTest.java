package com.boleteria;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.boleteria.exception.DatosInvalidosException;
import com.boleteria.exception.ReglaNegocioException;
import com.boleteria.model.Asiento;
import com.boleteria.model.Compra;
import com.boleteria.model.Evento;
import com.boleteria.model.ReporteEvento;
import com.boleteria.service.ArchivoServicio;
import com.boleteria.service.BoleteriaService;
import com.google.gson.Gson;

class BoleteriaTest {

    Path temp;
    ArchivoServicio archivos;
    Evento evento;
    List<Asiento> asientos;
    BoleteriaService servicio;

    @BeforeEach
    void setUp() throws Exception {
        temp = Files.createTempDirectory("boleteria-test");
        archivos = new ArchivoServicio(temp.resolve("compras.json"), temp.resolve("auditoria.txt"));
        evento = new Evento("EV001", "Evento de prueba", "2026-10-20", "19:00", "Teatro", "Concierto");
        asientos = List.of(new Asiento("EV001", "A-01", "VIP", "A", 1, 100000), new Asiento("EV001", "A-02", "VIP", "A", 2, 200000));
        servicio = new BoleteriaService(List.of(evento), asientos, archivos);
    }

    @Test
    void calculaSubtotalServicioYTotal() {
        Compra c = new Compra("EV001", "1", "Ana", "ana@mail.com", asientos);
        assertEquals(300000, c.getSubtotal());
        assertEquals(30000, c.getServicio());
        assertEquals(330000, c.getTotal());
    }

    @Test
    void permiteComprarAsientoDisponible() throws Exception {
        Compra c = servicio.comprar("EV001", "1", "Ana", "ana@mail.com", List.of("A-01"));
        assertEquals(110000, c.getTotal());
        assertEquals(1, archivos.cargarCompras().size());
    }

    @Test
    void rechazaAsientoVendido() throws Exception {
        servicio.comprar("EV001", "1", "Ana", "ana@mail.com", List.of("A-01"));
        assertThrows(ReglaNegocioException.class, () -> servicio.comprar("EV001", "2", "Luis", "luis@mail.com", List.of("A-01")));
    }

    @Test
    void buscaEventoExistente() {
        assertEquals("Evento de prueba", assertDoesNotThrow(() -> servicio.buscarEvento("EV001")).getNombre());
    }

    @Test
    void rechazaEventoInexistente() {
        assertThrows(ReglaNegocioException.class, () -> servicio.buscarEvento("EV999"));
    }

    @Test
    void validaCorreo() {
        assertTrue(BoleteriaService.correoValido("persona@correo.com"));
        assertFalse(BoleteriaService.correoValido("correo-mal"));
    }

    @Test
    void calculaPorcentajeOcupacion() throws Exception {
        servicio.comprar("EV001", "1", "Ana", "ana@mail.com", List.of("A-01"));
        ReporteEvento r = servicio.generarReporte().get(0);
        assertEquals(50.0, r.getOcupacion(), 0.001);
    }

    @Test
    void leeCsvValido() throws Exception {
        Path csv = temp.resolve("asientos.csv");
        Files.writeString(csv, "idEvento;codigoAsiento;localidad;fila;numero;precio\nEV001;A-01;VIP;A;1;125000");
        assertEquals(1, Asiento.cargarAsientos(csv).size());
        assertEquals(125000, Asiento.cargarAsientos(csv).get(0).getPrecio());
    }

    @Test
    void rechazaPrecioInvalidoEnCsv() throws Exception {
        Path csv = temp.resolve("malo.csv");
        Files.writeString(csv, "idEvento;codigoAsiento;localidad;fila;numero;precio\nEV001;A-01;VIP;A;1;caro");
        assertThrows(DatosInvalidosException.class, () -> Asiento.cargarAsientos(csv));
    }

    @Test
    void serializaYDeserializaCompraJson() throws Exception {
        Compra c = servicio.comprar("EV001", "1", "Ana", "ana@mail.com", List.of("A-01"));
        String json = new Gson().toJson(c);
        Compra copia = new Gson().fromJson(json, Compra.class);
        assertEquals(c.getIdCompra(), copia.getIdCompra());
        assertEquals("Ana", copia.getNombreComprador());
    }

    @Test
    void rechazaDatosObligatoriosVacios() {
        assertThrows(ReglaNegocioException.class, () -> servicio.comprar("EV001", "1", " ", "ana@mail.com", List.of("A-01")));
    }
}