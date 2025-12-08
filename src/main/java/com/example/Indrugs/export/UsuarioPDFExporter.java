package com.example.Indrugs.export;

import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.util.List;

public class UsuarioPDFExporter {

    public void exportar(List<UsuarioDTO> lista, Document document) throws DocumentException {

        // Título del PDF
        Font fuenteTitulo = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(0, 153, 112)); // verde característico
        Paragraph titulo = new Paragraph("Listado de Usuarios", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Crear tabla con 6 columnas
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10f);
        tabla.setSpacingAfter(10f);
        tabla.setWidths(new float[]{1.5f, 3f, 2f, 2f, 4f, 1.5f}); // ancho relativo de columnas

        // Encabezado de la tabla
        Font fuenteEncabezado = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Color colorEncabezado = new Color(0, 102, 68); // verde oscuro que combine
        String[] encabezados = {"Rol", "Nombre", "Tipo Doc", "Documento", "Correo", "Estado"};
        for (String h : encabezados) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fuenteEncabezado));
            cell.setBackgroundColor(colorEncabezado);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            tabla.addCell(cell);
        }

        // Llenar filas con datos
        Font fuenteFila = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
        boolean filaAlternada = false;
        for (UsuarioDTO u : lista) {
            Color filaColor = filaAlternada ? new Color(230, 245, 240) : Color.WHITE; // filas alternadas
            tabla.addCell(crearCelda(u.getNombreRol(), fuenteFila, filaColor));
            tabla.addCell(crearCelda(u.getNombre(), fuenteFila, filaColor));
            tabla.addCell(crearCelda(u.getTipoDoc(), fuenteFila, filaColor));
            tabla.addCell(crearCelda(u.getNumDoc(), fuenteFila, filaColor));
            tabla.addCell(crearCelda(u.getCorreo(), fuenteFila, filaColor));
            tabla.addCell(crearCelda(u.getEstado(), fuenteFila, filaColor));
            filaAlternada = !filaAlternada;
        }

        document.add(tabla);

        // Pie de página con marca
        Paragraph pie = new Paragraph("INDRUGS MEDICA © 2025", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY));
        pie.setAlignment(Element.ALIGN_CENTER);
        document.add(pie);
    }

    // Método auxiliar para crear celdas con fondo
    private PdfPCell crearCelda(String texto, Font fuente, Color fondo) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBackgroundColor(fondo);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
