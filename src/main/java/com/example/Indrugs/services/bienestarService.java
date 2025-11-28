package com.example.Indrugs.services;

import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class bienestarService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioService usuarioService;

    // ====================================================
    // ✅ ENVÍO MASIVO BIENESTAR — llama la firma de 5 parámetros correctamente
    // ====================================================
    public void enviarCorreosBienestarMensual(String mes, String telefonoSoporte, String mensajeExtra) {

        List<UsuarioDTO> usuarios = usuarioService.findByStatus("Activo");

        System.out.println("Enviando correos de bienestar a " + usuarios.size() + " usuarios...");

        for (UsuarioDTO usuario : usuarios) {
            try {
                // ✅ Construimos el HTML personalizado
                String contenidoHtml = String.format("""
                        <html>
                        <body style='font-family: Arial, sans-serif; background:#f6f9fc; padding:20px;'>
                            <div style='background:#ffffff; padding:22px; border-radius:12px; box-shadow:0 4px 10px rgba(0,0,0,0.08);'>
                                <h2 style='color:#00796b;'>💚 Hola %s</h2>
                                <p>Este es tu mensaje de bienestar del mes <b>%s</b>.</p>
                                <p>📞 Soporte: %s</p>
                                <p style='margin-top:18px; color:#333;'>%s</p>
                            </div>
                        </body>
                        </html>
                        """, usuario.getNombre(), mes, telefonoSoporte, mensajeExtra);

                // ✅ Llamada correcta → 5 parámetros
                emailService.enviarCorreoBienestar(
                        usuario.getCorreo(),
                        usuario.getNombre(),
                        mes,
                        telefonoSoporte,
                        mensajeExtra
                );

                System.out.println("Correo enviado a: " + usuario.getCorreo());

            } catch (Exception e) {
                System.err.println("Error enviando correo a: " + usuario.getCorreo());
                e.printStackTrace();
            }
        }

        System.out.println("Proceso finalizado.");
    }

    // ====================================================
    // ✅ ADMIN usa valores por defecto y llama envío masivo
    // ====================================================
    public void enviarCorreosBienestarMensual() {
        String mes = "Noviembre 2025";
        String telefonoSoporte = "3101234567";
        String mensajeExtra = "Gracias por confiar en Indrugs Médica. ¡Cuida tu salud este mes!";

        enviarCorreosBienestarMensual(mes, telefonoSoporte, mensajeExtra);
    }
}
