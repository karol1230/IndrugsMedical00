package com.example.Indrugs.controllers;

import com.example.Indrugs.repositorios.UsuarioRepository;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/recuperar")
public class RecuperarPasswordController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String mostrarFormulario() {
        return "recuperar_password";
    }

    @PostMapping
    public String procesarCorreo(@RequestParam String correo,
                                 RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "El correo ingresado no está registrado.");
            return "redirect:/recuperar";
        }

        Usuario usuario = usuarioOpt.get();

        // Generar clave de 6 dígitos
        String nuevaClave = String.format("%06d", new Random().nextInt(999999));

        usuario.setPassword(passwordEncoder.encode(nuevaClave));
        usuarioRepository.save(usuario);

        emailService.enviarCorreoRecuperarPassword(
                correo, usuario.getNombre(), nuevaClave
        );

        redirectAttributes.addFlashAttribute("exito",
                "Te enviamos una contraseña temporal. Revisa tu correo.");

        return "redirect:/recuperar";
    }


    private String generarNuevaClave() {
        return String.valueOf((int)(Math.random() * 900000 + 100000));
    }

}
