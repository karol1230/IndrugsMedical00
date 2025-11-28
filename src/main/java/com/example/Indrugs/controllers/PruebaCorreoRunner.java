package com.example.Indrugs.controllers;

import com.example.Indrugs.services.bienestarService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class PruebaCorreoRunner implements CommandLineRunner {

    private final bienestarService bienestarService;

    public PruebaCorreoRunner(bienestarService bienestarService) {
        this.bienestarService = bienestarService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Enviar correo de prueba
        bienestarService.enviarCorreosBienestarMensual(
                "Noviembre 2025",
                "3101234567",
                "Este es un mensaje de prueba desde CommandLineRunner."
        );

        System.out.println("Prueba de correo ejecutada al iniciar la app.");
    }
}
