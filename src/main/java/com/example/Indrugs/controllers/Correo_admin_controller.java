package com.example.Indrugs.controllers;

import com.example.Indrugs.services.bienestarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class Correo_admin_controller {

    @Autowired
    private bienestarService bienestarService;

    // Endpoint que activa el envío de correos de bienestar (masivo)
    @GetMapping("/admin/correos/enviar")
    public String enviarCorreosMasivos(RedirectAttributes redirectAttributes) {
        try {
            // ✅ Llamamos el método masivo real del service, no enviamos 1 correo fijo
            bienestarService.enviarCorreosBienestarMensual();

            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Los correos masivos de bienestar fueron enviados exitosamente.");

        } catch (Exception e) {
            System.err.println("Error en envío masivo de correos: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Hubo un error enviando los correos de bienestar.");
        }

        // regresar a la pantalla del admin
        return "redirect:/21.pagina_usuarios";
    }
}
