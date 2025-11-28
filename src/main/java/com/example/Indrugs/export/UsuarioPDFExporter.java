package com.example.Indrugs.export;

import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import java.util.List;

public class UsuarioPDFExporter {

    public void exportar(List<UsuarioDTO> lista, Document document) throws DocumentException {

        // Título
        Font fuenteTitulo = new Font(Font.HELVETICA, 14, Font.BOLD);
        Paragraph titulo = new Paragraph("Usuarios", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        // Tabla con columnas principales
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);

        tabla.addCell("Rol");
        tabla.addCell("Nombre");
        tabla.addCell("Tipo Doc");
        tabla.addCell("Documento");
        tabla.addCell("Correo");
        tabla.addCell("Estado");

        // Llenar filas con la lista
        for (UsuarioDTO u : lista) {
            tabla.addCell(u.getNombreRol());
            tabla.addCell(u.getNombre());
            tabla.addCell(u.getTipoDoc());
            tabla.addCell(u.getNumDoc());
            tabla.addCell(u.getCorreo());
            tabla.addCell(u.getEstado());
        }

        document.add(tabla);
    }
}
