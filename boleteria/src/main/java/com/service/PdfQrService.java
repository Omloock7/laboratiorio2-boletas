package com.boleteria.service;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.boleteria.model.Compra;
import com.boleteria.model.Evento;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Genera el ticket PDF e inserta un QR con los datos de la compra.
 */
public class PdfQrService {

    public Path generarTicket(Compra c, Evento e) throws Exception {
        Path dir = Path.of("tickets");
        Files.createDirectories(dir);
        Path destino = dir.resolve(c.getIdCompra() + ".pdf");
        String qr = "ID_COMPRA=" + c.getIdCompra() + " | TITULAR=" + c.getNombreComprador() + " | EVENTO=" + e.getIdEvento() + " - " + e.getNombre() + " | ASIENTOS=" + String.join(",", c.getCodigosAsientos()) + " | TOTAL=" + String.format("%.0f", c.getTotal());
        BitMatrix matrix = new QRCodeWriter().encode(qr, BarcodeFormat.QR_CODE, 250, 250);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
                cs.newLineAtOffset(55, 770);
                cs.showText("TICKET DIGITAL - BOLETERIA");
                cs.endText();
                String[] lineas = {"Compra: " + c.getIdCompra(), "Evento: " + e.getNombre(), "Fecha: " + e.getFecha() + " " + e.getHora(), "Lugar: " + e.getLugar(), "Titular: " + c.getNombreComprador(), "Documento: " + c.getCedulaComprador(), "Correo: " + c.getCorreoComprador(), "Asientos: " + String.join(", ", c.getCodigosAsientos()), "Subtotal: $" + String.format("%.0f", c.getSubtotal()), "Servicio (10%): $" + String.format("%.0f", c.getServicio()), "TOTAL: $" + String.format("%.0f", c.getTotal())};
                float y = 725;
                for (String linea : lineas) {
                    cs.beginText();
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    cs.newLineAtOffset(55, y);
                    cs.showText(limitar(linea));
                    cs.endText();
                    y -= 27;
                }
                PDImageXObject qrImg = LosslessFactory.createFromImage(doc, image);
                cs.drawImage(qrImg, 300, 280, 200, 200);
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(300, 260);
                cs.showText("Escanee el QR para verificar la compra");
                cs.endText();
            }
            doc.save(destino.toFile());
        }
        return destino;
    }

    private String limitar(String s) {
        return s.length() > 95 ? s.substring(0, 92) + "..." : s;
    }
}
