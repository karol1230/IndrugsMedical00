package com.example.Indrugs.controllers;

import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.repositorios.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // =========================
    // VER PERFIL SEGÚN ROL
    // =========================
    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        // Seleccionar la vista según rol
        String rol = usuario.getRol().getNombreRol();
        return switch (rol) {
            case "Administrador" -> "administrador/perfil";
            case "Paciente" -> "pacientes/perfil";
            case "Domiciliario" -> "domiciliario/perfil";
            default -> "redirect:/login";
        };
    }

    // =========================
    // ACTUALIZAR PERFIL PACIENTE
    // =========================
    @PostMapping("/perfil/paciente/actualizar")
    public String actualizarPerfilPaciente(@ModelAttribute Usuario usuarioActualizado,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !"Paciente".equals(usuarioLogueado.getRol().getNombreRol())) {
            return "redirect:/login";
        }

        // Actualizar datos permitidos
        actualizarDatosUsuario(usuarioLogueado, usuarioActualizado);

        // Guardar cambios
        usuarioRepository.save(usuarioLogueado);
        session.setAttribute("usuarioLogueado", usuarioLogueado);

        redirectAttributes.addFlashAttribute("mensajeExito", "Perfil actualizado exitosamente!");
        return "redirect:/perfil";
    }

    // =========================
    // ACTUALIZAR PERFIL DOMICILIARIO
    // =========================
    @PostMapping("/perfil/domiciliario/actualizar")
    public String actualizarPerfilDomiciliario(@ModelAttribute Usuario usuarioActualizado,
                                               HttpSession session,
                                               RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !"Domiciliario".equals(usuarioLogueado.getRol().getNombreRol())) {
            return "redirect:/login";
        }

        // Actualizar datos permitidos
        actualizarDatosUsuario(usuarioLogueado, usuarioActualizado);

        // Guardar cambios
        usuarioRepository.save(usuarioLogueado);
        session.setAttribute("usuarioLogueado", usuarioLogueado);

        redirectAttributes.addFlashAttribute("mensajeExito", "Perfil actualizado exitosamente!");
        return "redirect:/perfil";
    }

    // =========================
    // MÉTODO AUXILIAR PARA ACTUALIZAR DATOS
    // =========================
    private void actualizarDatosUsuario(Usuario usuarioOriginal, Usuario usuarioActualizado) {
        usuarioOriginal.setNombre(usuarioActualizado.getNombre());
        usuarioOriginal.setTipoDoc(usuarioActualizado.getTipoDoc());
        usuarioOriginal.setNumDoc(usuarioActualizado.getNumDoc());
        usuarioOriginal.setDireccion(usuarioActualizado.getDireccion());
        usuarioOriginal.setTelefono(usuarioActualizado.getTelefono());
        usuarioOriginal.setCorreo(usuarioActualizado.getCorreo());
    }
}
