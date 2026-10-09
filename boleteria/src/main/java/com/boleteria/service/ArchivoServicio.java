package com.boleteria.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.boleteria.model.Compra;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Centraliza lectura/escritura de compras JSON y auditoría TXT.
 */
public class ArchivoServicio {

    private final Path comprasPath, auditoriaPath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ArchivoServicio() {
        this(Path.of("data", "compras.json"), Path.of("logs", "auditoria.txt"));
    }

    public ArchivoServicio(Path compras, Path auditoria) {
        comprasPath = compras;
        auditoriaPath = auditoria;
    }

    public synchronized List<Compra> cargarCompras() throws IOException {
        if (!Files.exists(comprasPath)) {
            if (comprasPath.getParent() != null) {
                Files.createDirectories(comprasPath.getParent());
            
            }Files.writeString(comprasPath, "[]", StandardCharsets.UTF_8);
        }
        String json = Files.readString(comprasPath, StandardCharsets.UTF_8);
        Compra[] a = gson.fromJson(json, Compra[].class);
        return a == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(a));
    }

    public synchronized void guardarCompra(Compra compra) throws IOException {
        List<Compra> c = cargarCompras();
        c.add(compra);
        if (comprasPath.getParent() != null) {
            Files.createDirectories(comprasPath.getParent());
        
        }Files.writeString(comprasPath, gson.toJson(c), StandardCharsets.UTF_8);
    }

    public void auditar(String mensaje) {
        try {
            if (auditoriaPath.getParent() != null) {
                Files.createDirectories(auditoriaPath.getParent());
            
            }Files.writeString(auditoriaPath, LocalDateTime.now() + " | " + mensaje + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo escribir la auditoría: " + e.getMessage());
        }
    }

    public String serializar(Compra c) {
        return gson.toJson(c);
    }

    public Compra deserializar(String json) {
        return gson.fromJson(json, Compra.class);
    }
}
