package com.example.Indrugs.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtener correos activos desde la BD
     */
    public List<String> obtenerCorreosActivos() {
        return usuarioService.obtenerCorreosActivos();
    }

    /**
     * Enviar correo HTML genérico (versión limpia usada en otros módulos)
     */
    public void enviarCorreo(String destinatario, String asunto, String contenidoHtml) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setFrom("indrugsmedica@gmail.com");
            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);
            System.out.println("✅ Correo enviado a → " + destinatario);

        } catch (Exception e) {
            System.out.println("❌ Error real SMTP a " + destinatario + " → " + e.getMessage());
        }
    }

    /**
     * ✅ Alias que necesitaba PQRSAdminController
     * Internamente llama a enviarCorreo(destinatario, asunto, html)
     */
    public void enviarCorreoHtml(String destinatario, String asunto, String contenidoHtml) {
        enviarCorreo(destinatario, asunto, contenidoHtml);
    }

    /**
     * Enviar bienestar a 1 usuario específico (usado por BienestarService)
     */
    public void enviarCorreoBienestar(String destinatario, String nombre, String mes, String telefonoSoporte, String mensajeExtra) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Guía de bienestar de " + mes);
            helper.setFrom("indrugsmedica@gmail.com");

            String html = "<html><body style='font-family: Arial, sans-serif;'>"
                    + "<h2>Hola " + nombre + "</h2>"
                    + "<p>" + mensajeExtra + "</p>"
                    + "<p><b>Soporte:</b> " + telefonoSoporte + "</p>"
                    + "</body></html>";

            helper.setText(html, true);

            mailSender.send(mensaje);
            System.out.println("✅ Bienestar enviado a: " + destinatario);

        } catch (Exception e) {
            System.out.println("❌ Falló realmente a: " + destinatario + " → " + e.getMessage());
        }
    }

    /**
     * 📩 Enviar correos masivos de bienestar mensual
     */
    public void enviarCorreosMasivosBienestar(String mes, String telefonoSoporte, String mensajeExtra) {

        List<String> correos = obtenerCorreosActivos();

        for (String correo : correos) {
            try {
                String html = "<html><body>"
                        + "<h2>Guía de bienestar de " + mes + "</h2>"
                        + "<p>" + mensajeExtra + "</p>"
                        + "<footer>Soporte: " + telefonoSoporte + "</footer>"
                        + "</body></html>";

                enviarCorreo(correo, "Tu bienestar en " + mes, html);

            } catch (Exception e) {
                System.out.println("❌ Fallo real en masivo a: " + correo + " → " + e.getMessage());
            }
        }
    }


    // -------------- MÉTODOS QUE YA TENÍAS (QUEDAN IGUAL, SIN CAMBIOS) -------------- //

    public void enviarCorreo(String destinatario) {
        enviarCorreo(destinatario, "INDRUGS MEDICA", "<html><body><h2>Nuevo control registrado</h2></body></html>");
    }

    public void enviarCorreoRegistro(String correo, String nombre) {
        enviarCorreo(correo, "Bienvenido a INDRUGS MEDICA",
                "<html><body><h2>¡Bienvenido " + nombre + "!</h2><p>Registro exitoso.</p></body></html>");
    }

    public void enviarCorreoPqrsRespuesta(String destinatario, String asunto, String contenidoHtml) {
        enviarCorreo(destinatario, asunto, contenidoHtml);
    }

    public void enviarCorreoControlCreado(String destinatario, String nombrePaciente, String fechaInicio, String fechaFin, String problemaSalud) {
        String html = "<html><body>"
                + "<h3>Hola " + nombrePaciente + ",</h3>"
                + "<p>Tu control médico ha sido creado correctamente.</p>"
                + "<ul>"
                + "<li><b>Fecha Inicio:</b> " + fechaInicio + "</li>"
                + "<li><b>Fecha Fin:</b> " + fechaFin + "</li>"
                + "<li><b>Problema de Salud:</b> " + problemaSalud + "</li>"
                + "</ul>"
                + "</body></html>";

        enviarCorreo(destinatario, "Nuevo control médico registrado", html);
    }
}
