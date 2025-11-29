package com.example.Indrugs.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ====================================================
    // 1) ✅ CORREO HTML LIBRE (3 parámetros) — lo usa Orden y Pedido
    // ====================================================
    public void enviarCorreo(String destinatario, String asunto, String contenidoHtml) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setFrom("indrugsmedica@gmail.com");
        helper.setText(contenidoHtml, true);

        try {
            mailSender.send(mensaje);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    // ====================================================
    // 2) ✅ CORREO HTML SIMPLE — lo usa PQRS Admin
    // ====================================================
    public void enviarCorreoHtml(String correo, String asunto, String contenidoHtml) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(correo);
            helper.setSubject(asunto);
            helper.setFrom("indrugsmedica@gmail.com");
            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);
        } catch (MessagingException | MailException e) {
            e.printStackTrace();
        }
    }

    // ====================================================
    // 3) ✅ CORREO REGISTRO PERSONALIZADO
    // ====================================================
    public void enviarCorreoRegistro(
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "Debe ser un correo válido") String correo,
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 100) String nombre) {

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(correo);
            helper.setSubject("Bienvenido a INDRUGS MEDICA");
            helper.setFrom("indrugsmedica@gmail.com");

            String html = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #00796b;'>¡Bienvenido, " + nombre + "!</h2>" +
                    "<p>Tu registro fue exitoso. Gracias por ser parte de INDRUGS MEDICA.</p>" +
                    "</body>" +
                    "</html>";

            helper.setText(html, true);
            mailSender.send(mensaje);

        } catch (MessagingException | MailException e) {
            e.printStackTrace();
        }
    }

    // ====================================================
    // 4) ✅ CORREO BIENESTAR MENSUAL (5 parámetros) — lo usa tu bienestarService loop
    // ====================================================
    public void enviarCorreoBienestar(String correo,
                                      String nombre,
                                      String mes,
                                      String telefonoSoporte,
                                      String mensajeExtra) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(correo);
            helper.setSubject("Bienestar mensual - INDRUGS");
            helper.setFrom("indrugsmedica@gmail.com");

            String html = "<html>" +
                    "<body style='font-family: Arial, sans-serif; background:#f6f9fc; padding:20px;'>" +
                    "<div style='background:#ffffff; padding:22px; border-radius:12px; box-shadow:0 4px 10px rgba(0,0,0,0.08);'>" +
                    "<h2 style='color:#00796b;'>💚 Hola " + nombre + "</h2>" +
                    "<p>Este es tu mensaje de bienestar del mes <b>" + mes + "</b>.</p>" +
                    "<p>📞 Soporte: " + telefonoSoporte + "</p>" +
                    "<p style='margin-top:18px; color:#333;'>" + mensajeExtra + "</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(html, true);
            mailSender.send(mensaje);

        } catch (MessagingException | MailException e) {
            e.printStackTrace();
        }
    }

    // ====================================================
    // 5) ✅ CORREO GENÉRICO PREDEFINIDO (1 parámetro)
    // ====================================================
    public void enviarCorreo(String destinatario) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("INDRUGS MEDICA");
            helper.setFrom("indrugsmedica@gmail.com");

            String html = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color:#00796b;'>¡Gracias por usar INDRUGS MEDICA!</h2>" +
                    "<p>Hola, has registrado un nuevo control en la plataforma.</p>" +
                    "</body>" +
                    "</html>";

            helper.setText(html, true);
            mailSender.send(mensaje);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // ====================================================
    // 6) ✅ NUEVO: CORREOS MASIVOS A UNA LISTA DE DESTINATARIOS
    // ====================================================
    public void enviarCorreosMasivos(List<String> destinatarios, String asunto, String contenidoHtml) {
        for (String correo : destinatarios) {
            try {
                MimeMessage mensaje = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

                helper.setTo(correo);
                helper.setSubject(asunto);
                helper.setFrom("indrugsmedica@gmail.com");
                helper.setText(contenidoHtml, true);

                mailSender.send(mensaje);
            } catch (MessagingException | MailException e) {
                e.printStackTrace(); // Loguea si falla el envío de alguno
            }
        }
    }
}
