package com.boleteria.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.boleteria.exception.DatosInvalidosException;
import com.boleteria.exception.ReglaNegocioException;
import com.boleteria.model.Asiento;
import com.boleteria.model.Compra;
import com.boleteria.model.Evento;
import com.boleteria.model.ReporteEvento;

/**
 * Coordina consultas, validaciones y ventas.
 */
public class BoleteriaService {

    private final List<Evento> eventos;
    private final List<Asiento> asientos;
    private final ArchivoServicio archivos;

    public BoleteriaService() throws IOException, DatosInvalidosException {
        this(Evento.cargarEventos(), Asiento.cargarAsientos(), new ArchivoServicio());
    }

    public BoleteriaService(List<Evento> eventos, List<Asiento> asientos, ArchivoServicio archivos) {
        this.eventos = new ArrayList<>(eventos);
        this.asientos = new ArrayList<>(asientos);
        this.archivos = archivos;
    }

    public List<Evento> listarEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public Evento buscarEvento(String id) throws ReglaNegocioException {
        return eventos.stream().filter(e -> e.getIdEvento().equalsIgnoreCase(id)).findFirst().orElseThrow(() -> new ReglaNegocioException("No existe el evento " + id));
    }

    public List<Asiento> asientosDeEvento(String id) throws ReglaNegocioException, IOException {
        buscarEvento(id);
        Set<String> vendidos = asientosVendidos(id);
        List<Asiento> r = new ArrayList<>();
        for (Asiento a : asientos) {
            if (a.getIdEvento().equalsIgnoreCase(id)) {
                r.add(a);
        
            }
        }return r;
    }

    public boolean estaVendido(String evento, String codigo) throws IOException {
        return asientosVendidos(evento).contains(codigo);
    }

    private Set<String> asientosVendidos(String evento) throws IOException {
        Set<String> s = new HashSet<>();
        for (Compra c : archivos.cargarCompras()) {
            if (c.getIdEvento().equalsIgnoreCase(evento)) {
                s.addAll(c.getCodigosAsientos());
        
            }
        }return s;
    }

    public static boolean correoValido(String correo) {
        return correo != null && Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", correo.trim());
    }

    public Compra comprar(String idEvento, String documento, String nombre, String correo, List<String> codigos) throws ReglaNegocioException, IOException {
        Evento evento = buscarEvento(idEvento);
        if (vacio(documento) || vacio(nombre) || vacio(correo)) {
            throw new ReglaNegocioException("Nombre, documento y correo son obligatorios.");
        
        }if (!correoValido(correo)) {
            throw new ReglaNegocioException("El correo no tiene un formato válido.");
        
        }if (codigos == null || codigos.isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar al menos un asiento.");

        }if (!nombre.trim().matches("[\\p{L}\\s]+")) {
             throw new ReglaNegocioException("El nombre solo puede contener letras y espacios.");
             
        }if (!documento.trim().matches("[0-9]+")) {
             throw new ReglaNegocioException("El documento solo puede contener números.");
        
        }Set<String> unicos = new HashSet<>(codigos);
        if (unicos.size() != codigos.size()) {
            throw new ReglaNegocioException("No puede seleccionar el mismo asiento más de una vez.");
        
        }Set<String> vendidos = asientosVendidos(idEvento);
        List<Asiento> seleccionados = new ArrayList<>();
        for (String codigo : codigos) {
            Asiento encontrado = null;
            for (Asiento a : asientos) {
                if (a.getIdEvento().equals(evento.getIdEvento()) && a.getCodigoAsiento().equalsIgnoreCase(codigo)) {
                    encontrado = a;
                    break;
                }
            
            }if (encontrado == null) {
                throw new ReglaNegocioException("Asiento inválido para este evento: " + codigo);
            
            }if (vendidos.contains(encontrado.getCodigoAsiento())) {
                throw new ReglaNegocioException("El asiento ya fue vendido: " + codigo);
            
            }seleccionados.add(encontrado);
        }
        Compra compra = new Compra(evento.getIdEvento(), documento.trim(), nombre.trim(), correo.trim(), seleccionados);
        archivos.guardarCompra(compra);
        archivos.auditar("COMPRA OK | " + compra.getIdCompra() + " | evento=" + idEvento + " | titular=" + nombre + " | asientos=" + String.join(",", codigos));
        return compra;
    }

    private boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public List<ReporteEvento> generarReporte() throws IOException {
        List<Compra> compras = archivos.cargarCompras();
        List<ReporteEvento> r = new ArrayList<>();
        for (Evento e : eventos) {
            int total = 0, vendidas = 0;
            double ingresos = 0;
            for (Asiento a : asientos) {
                if (a.getIdEvento().equals(e.getIdEvento())) {
                    total++;
            
                }
            }for (Compra c : compras) {
                if (c.getIdEvento().equals(e.getIdEvento())) {
                    vendidas += c.getCodigosAsientos().size();
                    ingresos += c.getTotal();
                }
            
            }r.add(new ReporteEvento(e.getIdEvento(), e.getNombre(), total, vendidas, ingresos));
        }
        return r;
    }

    public void registrarError(String mensaje) {
        archivos.auditar("ERROR | " + mensaje);
    }
}