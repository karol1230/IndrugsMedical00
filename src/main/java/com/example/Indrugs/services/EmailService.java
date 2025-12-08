package com.example.Indrugs.services;

import com.example.Indrugs.DTO.Usuario.UsuarioCreateDTO;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioService usuarioService;


    // =====================================================
    //  MÉTODO CENTRAL (EL QUE REALMENTE ENVÍA)
    // =====================================================
    public void enviarCorreo(String destinatario, String asunto, String contenidoHtml) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setFrom("indrugsmedica@gmail.com");
            helper.setText(contenidoHtml, true);

            mailSender.send(mensaje);

            System.out.println("📨 Correo enviado a → " + destinatario);

        } catch (Exception e) {
            System.out.println("❌ Error SMTP a " + destinatario + " → " + e.getMessage());
        }
    }



    // =====================================================
    //  MÉTODO COMPATIBLE — LLAMA AL MÉTODO CENTRAL
    // =====================================================
    public void enviarCorreoHtml(String destinatario, String asunto, String contenidoHtml) {
        System.out.println("➡ Enviando correo HTML a: " + destinatario);
        enviarCorreo(destinatario, asunto, contenidoHtml);
    }


    // =====================================================
    //  OTROS MÉTODOS — NO LOS TOQUÉ
    // =====================================================

    public List<String> obtenerCorreosActivos() {
        return usuarioService.obtenerCorreosActivos();
    }

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

    public void enviarPostulacionCompleta(
            UsuarioCreateDTO candidato,
            String vehiculo,
            MultipartFile archivoCV,
            MultipartFile licenciaPdf,
            MultipartFile tarjetaPdf
    ) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo("indrugsmedica@gmail.com");
        helper.setSubject("Nueva postulación - " + candidato.getNombre());

        String contenidoHtml = """
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <h2>Nueva postulación recibida</h2>
                <p><b>Nombre:</b> %s</p>
                <p><b>Tipo de documento:</b> %s</p>
                <p><b>Número de documento:</b> %s</p>
                <p><b>Correo:</b> %s</p>
                <p><b>Teléfono:</b> %s</p>
                <p><b>Tipo de vehículo:</b> %s</p>
            </body>
            </html>
            """.formatted(
                candidato.getNombre(),
                candidato.getTipoDoc(),
                candidato.getNumDoc(),
                candidato.getCorreo(),
                candidato.getTelefono(),
                vehiculo
        );

        helper.setText(contenidoHtml, true);

        helper.addAttachment(archivoCV.getOriginalFilename(), archivoCV);
        helper.addAttachment(licenciaPdf.getOriginalFilename(), licenciaPdf);
        helper.addAttachment(tarjetaPdf.getOriginalFilename(), tarjetaPdf);

        mailSender.send(message);
        System.out.println("📨 Postulación enviada correctamente.");
    }

    public void enviarCorreoDomiciliario(String correo, String nombre, Long numDoc) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(correo);
            helper.setSubject("Bienvenido a nuestro equipo");
            helper.setFrom("indrugsmedica@gmail.com");

            String html = """
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <h2>Hola %s</h2>
                <p>¡Bienvenido! Ahora eres parte de nuestro equipo de domiciliarios.</p>
                <ul>
                    <li><b>Correo:</b> %s</li>
                    <li><b>Contraseña:</b> %s</li>
                </ul>
            </body>
            </html>
            """.formatted(nombre, correo, numDoc);

            helper.setText(html, true);
            mailSender.send(mensaje);

            System.out.println("📨 Correo de bienvenida enviado a: " + correo);

        } catch (Exception e) {
            System.out.println("❌ Error enviando correo de domiciliario: " + e.getMessage());
        }
    }

    public void enviarCorreoBienestar(String destinatario, String nombreUsuario, String mes,
                                      String telefonoSoporte, String mensajeExtra) {

        try {
            String contenidoHtml = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2>¡Hola " + nombreUsuario + "!</h2>" +
                    "<p>Este es tu correo mensual de bienestar correspondiente al mes de <strong>" + mes + "</strong>.</p>" +
                    "<p>" + mensajeExtra + "</p>" +
                    "<br>" +
                    "<p>Si necesitas ayuda, comunícate al número de soporte: <strong>" + telefonoSoporte + "</strong></p>" +
                    "<br>" +
                    "<p>Atentamente,<br>Indrugs Médica</p>" +
                    "</body>" +
                    "</html>";

            enviarCorreo(destinatario, "Bienestar " + mes, contenidoHtml);

            System.out.println("📨 Correo de bienestar enviado a: " + destinatario);

        } catch (Exception e) {
            System.out.println("❌ Error enviando correo de bienestar a " + destinatario + " → " + e.getMessage());
        }
    }


    public void enviarCorreoRecuperarPassword(String correo, String nombre, String nuevaClave) {

        String asunto = "Recuperación de contraseña - Indrugs Medical";

        String html = """
        <html>
        <body style="font-family: Arial, sans-serif; color:#333;">
            <h2 style="color:#00796b;">Hola %s,</h2>
            <p>Tu nueva clave temporal es:</p>
            <div style="padding:10px;background:#00796b;color:white;display:inline-block;border-radius:6px;font-size:18px;">
                %s
            </div>
        </body>
        </html>
        """.formatted(nombre, nuevaClave);

        enviarCorreo(correo, asunto, html);
    }
    // -------------- MÉTODOS YA EXISTENTES -------------- //

    // ⚠️ ESTE ES EL MÉTODO PROBLEMÁTICO → Solo agrégale la anotación
    @Deprecated
    public void enviarCorreo(String destinatario) {
        enviarCorreo(destinatario, "INDRUGS MEDICA",
                "<html><body><h2>Nuevo control registrado</h2></body></html>");
    }


}
