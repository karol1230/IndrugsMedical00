package com.example.Indrugs.controllers;

import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.repositorios.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder; // 🔥 NECESARIO PARA ENCRIPTAR

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

        actualizarDatosUsuario(usuarioLogueado, usuarioActualizado);

        // 🔥 Actualizar contraseña si el usuario ingresó algo
        actualizarPassword(usuarioLogueado, usuarioActualizado.getPassword());

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

        actualizarDatosUsuario(usuarioLogueado, usuarioActualizado);

        // 🔥 Actualizar contraseña si el usuario la escribió
        actualizarPassword(usuarioLogueado, usuarioActualizado.getPassword());

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

    // =========================
    // 🔥 MÉTODO PARA ACTUALIZAR CONTRASEÑA
    // =========================
    private void actualizarPassword(Usuario usuario, String nuevaPassword) {

        if (nuevaPassword != null && !nuevaPassword.trim().isEmpty()) {
            if (nuevaPassword.length() < 6) {
                // Puedes mostrar un error si quieres
                return;
            }

            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        }
    }
}
