package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.ControlDTO;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.ControlService;
import com.example.Indrugs.services.MedicamentosService;
import com.example.Indrugs.services.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping
public class ControlController {

    private final ControlService controlService;
    private final MedicamentosService medicamentosService;
    private final EmailService emailService;

    public ControlController(ControlService controlService,
                             MedicamentosService medicamentosService,
                             EmailService emailService) {
        this.controlService = controlService;
        this.medicamentosService = medicamentosService;
        this.emailService = emailService;
    }

    @GetMapping("/24.pagina_control")
    public String mostrarPaginaControl(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login";
        }

        List<ControlDTO> controles = controlService.obtenerTodosLosControlesporUsuario(usuario.getIdUsuario());
        model.addAttribute("controles", controles);

        return "pacientes/24.pagina_control";
    }

    @GetMapping("/3.pagina_de_control")
    public String mostrarFormularioAgregar(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogueado", usuario);
        model.addAttribute("medicamentos", medicamentosService.readAdmin());
        model.addAttribute("control", new ControlDTO());

        return "pacientes/3.pagina_de_control";
    }

    @PostMapping("/agregar_control")
    public String agregarControl(@ModelAttribute ControlDTO controlDTO,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            // 1️⃣ Obtener usuario desde sesión
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuario != null) {
                controlDTO.setIdUsuario(usuario.getIdUsuario());
            }

            // 2️⃣ Convertir hora de alarma (si llega como String)
            String alarmaStr = request.getParameter("alarmaControl");
            if (controlDTO.getAlarmaControl() == null && alarmaStr != null && !alarmaStr.isBlank()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
                controlDTO.setAlarmaControl(LocalTime.parse(alarmaStr, formatter));
            }

            // 3️⃣ Guardar control
            controlService.guardarControl(controlDTO);

            // 4️⃣ Enviar correo de confirmación
            if (usuario != null && usuario.getCorreo() != null && !usuario.getCorreo().isBlank()) {
                String html = """
                        <html><body>
                        <h3>Hola %s!</h3>
                        <p>Tu control ha sido registrado correctamente.</p>
                        </body></html>
                        """.formatted(usuario.getNombre());

                emailService.enviarCorreoHtml(usuario.getCorreo(), "Nuevo control registrado", html);
            }

            // 5️⃣ Mensaje flash
            redirectAttributes.addFlashAttribute("mensaje", "Control agregado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar control: " + e.getMessage());
        }

        return "redirect:/24.pagina_control";
    }

    @GetMapping("/actualizar_control")
    public String mostrarFormularioEdicion(@RequestParam Long idControl, Model model) {
        try {
            ControlDTO control = controlService.findById(idControl);
            model.addAttribute("control", control);
            return "pacientes/actualizar_control";
        } catch (Exception e) {
            return "redirect:/17.pagina_control?error=Control no encontrado";
        }
    }

    @PostMapping("/actualizar_control")
    public String actualizarControl(@RequestParam Long idControl,
                                    ControlDTO controlDTO,
                                    HttpSession session,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Convertir hora si es necesario
            String alarmaStr = request.getParameter("alarmaControl");
            if (controlDTO.getAlarmaControl() == null && alarmaStr != null && !alarmaStr.isBlank()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
                controlDTO.setAlarmaControl(LocalTime.parse(alarmaStr, formatter));
            }

            // Asegurar idUsuario
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuario != null) {
                controlDTO.setIdUsuario(usuario.getIdUsuario());
            }

            controlService.actualizar(idControl, controlDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Control actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar control: " + e.getMessage());
        }
        return "redirect:/17.pagina_control";
    }

    @PostMapping("/eliminar_control")
    public String eliminarControl(@RequestParam Long idControl, RedirectAttributes redirectAttributes) {
        try {
            controlService.eliminarControl(idControl);
            redirectAttributes.addFlashAttribute("mensajeExito", "Control eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar control: " + e.getMessage());
        }
        return "redirect:/24.pagina_control";
    }
}
