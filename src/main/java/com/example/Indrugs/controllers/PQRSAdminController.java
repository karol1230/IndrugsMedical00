package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.PQRSDTO;
import com.example.Indrugs.entities.PQRS;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.EmailService;
import com.example.Indrugs.services.PQRSservice;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/administrador/pqrs")
public class PQRSAdminController {

    @Autowired
    private PQRSservice pqrsService;

    @Autowired
    private EmailService emailService;


    @GetMapping
    public String listarTodas(Model model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null ||
                !usuario.getRol().getNombreRol().equalsIgnoreCase("Administrador")) {

            return "redirect:/login";
        }

        model.addAttribute("listaPQRS", pqrsService.listarTodo());
        model.addAttribute("usuarioLogueado", usuario);

        return "administrador/pqrs_listar";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null ||
                !usuario.getRol().getNombreRol().equalsIgnoreCase("Administrador")) {

            return "redirect:/login";
        }

        PQRSDTO dto = pqrsService.obtenerPorId(id);

        if (dto == null) {
            return "redirect:/administrador/pqrs?error=notfound";
        }

        model.addAttribute("pqrs", dto);
        model.addAttribute("usuarioLogueado", usuario);

        return "administrador/pqrs_detalle";
    }

    @PostMapping("/{id}/responder")
    public String responderPqrs(
            @PathVariable Long id,
            @RequestParam String respuesta,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {

        PQRS pqrs = pqrsService.obtenerPqrsPorId(id);

        if (pqrs == null) {
            redirectAttributes.addFlashAttribute("error", "La PQRS no existe");
            return "redirect:/administrador/pqrs";
        }

        pqrs.setRespuesta(respuesta);
        pqrs.setEstado(estado); // <-- usar el estado enviado desde el formulario
        pqrs.setFechaRespuesta(LocalDateTime.now());
        pqrsService.guardar(pqrs);

        try {
            String correo = pqrs.getUsuario().getCorreo();

            String contenidoHtml = "<!DOCTYPE html>" +
                    "<html lang='es'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "</head>" +
                    "<body style='font-family: Arial, sans-serif; background-color:#f0f2f5; margin:0; padding:0;'>" +
                    "  <div style='max-width:600px; margin:30px auto; background-color:#ffffff; border-radius:12px; box-shadow:0 6px 15px rgba(0,0,0,0.1); padding:25px;'>" +

                    "    <h2 style='color:#00796b; text-align:center; margin-bottom:5px;'>✨ ¡Tu PQRS ha sido actualizada! ✨</h2>" +
                    "    <p style='text-align:center; color:#555; margin-top:0;'>Aquí tienes los detalles de tu solicitud:</p>" +

                    "    <div style='background-color:#e0f2f1; border-left:6px solid #00796b; padding:18px; margin:20px 0; border-radius:8px;'>" +
                    "      <p><strong>Tipo de PQRS:</strong> " + pqrs.getTipoPqrs() + "</p>" +
                    "      <p><strong>Motivo:</strong> " + pqrs.getMotivo() + "</p>" +
                    "      <p><strong>Estado actual:</strong> <span style='color:#00796b; font-weight:bold;'>" + pqrs.getEstado() + "</span></p>" +
                    "      <p><strong>Respuesta del administrador:</strong></p>" +
                    "      <p style='font-style:italic; color:#333;'>" + respuesta + "</p>" +
                    "    </div>" +

                    "    <p style='text-align:center; color:#555; margin-top:30px;'>💜 Gracias por confiar en <strong>INDRUGS MEDICA</strong>. ¡Estamos aquí para ayudarte siempre! 💜</p>" +
                    "    <hr style='border:none; border-top:1px solid #ddd; margin-top:25px;'/>" +
                    "    <p style='font-size:12px; color:#888; text-align:center;'>Este correo es automático, por favor no respondas a este mensaje.</p>" +

                    "  </div>" +
                    "</body>" +
                    "</html>";


            emailService.enviarCorreoHtml(correo, "Respuesta a tu PQRS", contenidoHtml);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "La PQRS fue respondida, pero ocurrió un error enviando el correo.");
        }


        // Mensaje dinámico según el estado seleccionado
        String mensajeExito;
        switch (estado) {
            case "Resuelto":
                mensajeExito = "PQRS resuelta exitosamente ✅";
                break;
            case "Pendiente":
                mensajeExito = "La PQRS cambió de estado a pendiente ⏳";
                break;
            case "En proceso":
                mensajeExito = "La PQRS está ahora en proceso 🔄";
                break;
            default:
                mensajeExito = "PQRS actualizada correctamente";
        }

        redirectAttributes.addFlashAttribute("exito", mensajeExito);

        return "redirect:/administrador/pqrs";
    }


}


