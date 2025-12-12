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

    /**
     * Enviar correos de bienestar a usuarios activos con parámetros personalizados
     */
    public void enviarCorreosBienestarMensual(String mes, String telefonoSoporte, String mensajeExtra) {

        List<UsuarioDTO> usuarios = usuarioService.findByStatus("Activo");

        System.out.println("📩 Enviando correos masivos de bienestar a " + usuarios.size() + " usuarios...");

        for (UsuarioDTO usuario : usuarios) {
            try {
                emailService.enviarCorreoBienestar(
                        usuario.getCorreo(),
                        usuario.getNombre(),
                        mes,
                        telefonoSoporte,
                        mensajeExtra
                );

                System.out.println("Correo enviado realmente a: " + usuario.getCorreo());

            } catch (Exception e) {
                System.out.println("Error real enviando a: " + usuario.getCorreo() + " → " + e.getMessage());
            }
        }

        System.out.println("🚀 Proceso masivo de correos terminado.");
    }

    /**
     * Llamado desde botón del admin, usa valores por defecto
     */
    public void enviarCorreosBienestarMensual() {

        String mes = "Diciembre 2025";
        String telefonoSoporte = "3101234567";
        String mensajeExtra = "Hola,\n" +
                "Esperamos que estés teniendo un gran día.\n" +
                "\n" +
                "En la vida diaria, cuidar de nuestra salud debería ser simple, cómodo y accesible. " +
                "Por eso queremos recordarte que ahora puedes recibir tus medicamentos directamente en casa, sin filas, sin prisas y sin complicaciones." +
                "Además, queremos acompañarte en cada paso con información clara y sencilla para que mantengas hábitos saludables todos los días:\n" +
                "\n" +
                "\uD83C\uDF3F Mantén una buena hidratación.\n" +
                "\uD83D\uDE34 Dale prioridad al descanso.\n" +
                "\uD83C\uDF4E Elige alimentos que te hagan sentir bien.\n" +
                "\uD83D\uDEB6\u200D♂\uFE0F Muévete un poco cada día.\n" +
                "\uD83D\uDC8A Y recuerda seguir siempre las indicaciones de tus profesionales de salud.\n" +
                "\n" +
                "Estamos aquí para hacerte la vida más fácil.\n" +
                "Tu bienestar es nuestra prioridad." +
                "Gracias por confiar en Indrugs Médica. ¡Cuida tu salud este mes!";

        enviarCorreosBienestarMensual(mes, telefonoSoporte, mensajeExtra);
    }
}
