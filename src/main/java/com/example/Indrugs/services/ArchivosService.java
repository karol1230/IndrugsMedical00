package com.example.Indrugs.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ArchivosService {

    private final String BASE_UPLOAD_DIR = "uploads";

    /**
     * Guarda cualquier archivo en la carpeta específica.
     * @param archivo Archivo recibido desde el formulario
     * @param carpeta Carpeta donde se guardará el archivo
     * @return URL relativa del archivo guardado
     */
    public String guardarArchivo(MultipartFile archivo, String carpeta) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("Archivo vacío o no recibido");
        }

        // Ya no explota si el contentType es null
        validarTipoArchivoSeguro(archivo, carpeta);

        // Ruta absoluta del directorio de trabajo
        String directorioTrabajo = System.getProperty("user.dir");
        String rutaCompleta = directorioTrabajo + File.separator + BASE_UPLOAD_DIR + File.separator + carpeta;

        // Crear directorio si no existe
        Path directorio = Paths.get(rutaCompleta);
        Files.createDirectories(directorio);

        // Generar nombre seguro y único
        String originalName = archivo.getOriginalFilename();
        if (originalName == null) originalName = "archivo.pdf";

        String nombreArchivo = System.currentTimeMillis() + "_" +
                originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

        Path archivoDestino = directorio.resolve(nombreArchivo);

        // Guardar archivo en el sistema
        Files.copy(archivo.getInputStream(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + carpeta + "/" + nombreArchivo;
    }

    /**
     * Guarda imagen de medicamento en la carpeta: medicamentos
     */
    public String guardarImagenMedicamento(MultipartFile imagen) throws IOException {
        return guardarArchivo(imagen, "medicamentos");
    }

    /**
     * Guarda PDF de fórmula médica en la carpeta: formulas
     */
    public String guardarFormulaMedica(MultipartFile pdf) throws IOException {
        return guardarArchivo(pdf, "formulas");
    }

    /**
     * Valida tipo sin explotar si llega null el contentType
     */
    private void validarTipoArchivoSeguro(MultipartFile archivo, String carpeta) {
        String contentType = archivo.getContentType();

        if (contentType == null) {
            // Si es fórmula, forzamos a PDF como válido
            if (carpeta.equalsIgnoreCase("formulas")) return;
            // Si es medicamentos, forzamos a aceptar como imagen
            if (carpeta.equalsIgnoreCase("medicamentos")) return;
        }

        if (carpeta.equalsIgnoreCase("formulas") && !contentType.equals("application/pdf")) {
            throw new RuntimeException("Solo se permiten archivos PDF para fórmulas");
        }

        if (carpeta.equalsIgnoreCase("medicamentos") && !contentType.startsWith("image/")) {
            throw new RuntimeException("Solo se permiten imágenes para medicamentos");
        }
    }

    /**
     * Elimina el archivo físico del sistema
     */
    public void eliminarArchivo(String rutaRelativa) {
        try {
            // Convertir la ruta relativa a local
            String directorioTrabajo = System.getProperty("user.dir");
            Path path = Paths.get(directorioTrabajo + rutaRelativa.replace("/uploads", "/uploads"));
            Files.deleteIfExists(path);
        } catch (Exception e) {
            System.err.println("No se pudo eliminar el archivo: " + e.getMessage());
        }
    }
}
