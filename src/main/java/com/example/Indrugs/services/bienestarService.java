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

        String mes = "Noviembre 2025";
        String telefonoSoporte = "3101234567";
        String mensajeExtra = "Gracias por confiar en Indrugs Médica. ¡Cuida tu salud este mes!";

        enviarCorreosBienestarMensual(mes, telefonoSoporte, mensajeExtra);
    }
}
