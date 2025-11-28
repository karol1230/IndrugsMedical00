package com.example.Indrugs.export;

import com.example.Indrugs.DTO.InventarioDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import java.util.List;

public class InventarioPDFExporter {

    public void exportar(List<InventarioDTO> lista, Document document) throws DocumentException {

        // Título
        Font fuenteTitulo = new Font(Font.HELVETICA, 14, Font.BOLD);
        Paragraph titulo = new Paragraph("Inventario", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        // Tabla
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(5);

        tabla.addCell("ID");
        tabla.addCell("Medicamento");
        tabla.addCell("Fecha Entrada");
        tabla.addCell("Stock");
        tabla.addCell("Fecha Salida");
        tabla.addCell("Estado");

        // Llenar filas con datos de la lista
        for (InventarioDTO inv : lista) {
            tabla.addCell(String.valueOf(inv.getIdInventario()));
            tabla.addCell(inv.getNombreMedicamento());
            tabla.addCell(String.valueOf(inv.getFechaEntrada()));
            tabla.addCell(String.valueOf(inv.getStock()));
            tabla.addCell(String.valueOf(inv.getFechaSalida()));
            tabla.addCell(inv.getEstadoMed());
        }

        document.add(tabla);
    }
}
