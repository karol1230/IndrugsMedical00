package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.InventarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.export.InventarioPDFExporter;
import com.example.Indrugs.export.UsuarioPDFExporter;
import com.example.Indrugs.services.InventarioService;
import com.example.Indrugs.services.UsuarioService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ExportController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private UsuarioService usuarioService;

    // =============================
    // 📦 EXPORTAR INVENTARIO A PDF
    // =============================
    @GetMapping("/export/inventario")
    public void exportInventario(HttpServletResponse response) throws IOException, DocumentException {

        response.setContentType("application/pdf");

        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        response.setHeader("Content-Disposition",
                "attachment; filename=inventario_" + fecha + ".pdf");

        List<InventarioDTO> lista = inventarioService.read();

        Document document = new Document(PageSize.A4);
        ServletOutputStream out = response.getOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        InventarioPDFExporter exporter = new InventarioPDFExporter();
        exporter.exportar(lista, document);
        document.close();

        out.flush();
        out.close();
    }

    // ===========================
    // 👤 EXPORTAR USUARIOS A PDF
    // ===========================
    @GetMapping("/export/usuarios")
    public void exportUsuarios(HttpServletResponse response) throws IOException, DocumentException {

        response.setContentType("application/pdf");

        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        response.setHeader("Content-Disposition",
                "attachment; filename=usuarios_" + fecha + ".pdf");

        List<UsuarioDTO> lista = usuarioService.read();

        Document document = new Document(PageSize.A4);
        ServletOutputStream out = response.getOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        UsuarioPDFExporter exporter = new UsuarioPDFExporter();
        exporter.exportar(lista, document);
        document.close();

        out.flush();
        out.close();
    }
}
