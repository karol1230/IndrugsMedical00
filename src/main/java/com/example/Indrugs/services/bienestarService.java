package com.example.Indrugs.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class bienestarService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Método original con parámetros
    public void enviarCorreosBienestarMensual(List<String> correos) throws MessagingException {

        // 📌 Asunto exacto que solicitaste
        String asunto = "Tu salud es importante: pequeños recordatorios para cuidarte";

        // 📌 Cuerpo exacto que solicitaste
        String cuerpo = "Cuidar de tu salud es más fácil de lo que parece. Aquí te dejamos algunos consejos para mantener tus tratamientos bajo control y sentirte mejor cada día:\n\n"
                + "•Sigue tu tratamiento al pie de la letra: No olvides tomar tus medicamentos en el horario indicado.\n"
                + "•Lleva un registro: Anota tus dosis o usa recordatorios en tu teléfono para mantener todo en orden.\n"
                + "•Consulta dudas con tu médico: Nunca dudes en preguntar si notas algo diferente o tienes efectos secundarios.\n"
                + "•Mantén hábitos saludables: Alimentación balanceada, hidratación y ejercicio moderado ayudan mucho a tu bienestar.\n\n"
                + "•Recuerda que estamos aquí para ayudarte a recibir tus medicamentos sin complicaciones, directamente en tu domicilio. 💚\n\n"
                + "Gracias por confiar en nosotros,\n\n"
                + "El equipo de Indrugs Medica\n\n";


        // Envío a cada destinatario
        for (String correoDestino : correos) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(correoDestino);
            helper.setSubject(asunto);
            helper.setText(cuerpo, false); // false = texto plano
            mailSender.send(message);
        }
    }

    // ✅ Método nuevo SIN parámetros para llamar desde el controller
    public void enviarCorreosBienestarMensual() {
        try {
            // 📌 Aquí debes reemplazar con los correos reales más adelante
            List<String> listaCorreos = List.of(
                    "correo1@ejemplo.com",
                    "correo2@ejemplo.com"
            );

            enviarCorreosBienestarMensual(listaCorreos);

        } catch (MessagingException e) {
            System.out.println("Error enviando correos de bienestar: " + e.getMessage());
        }
    }
}
