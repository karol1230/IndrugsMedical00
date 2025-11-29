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

        bienestarService.enviarCorreosBienestarMensual();

        System.out.println("Correo masivo");
    }


}
