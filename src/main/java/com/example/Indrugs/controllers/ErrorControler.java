package com.example.Indrugs.controllers;

import org.springframework.web.bind.annotation.GetMapping;


public class ErrorControler {

        @GetMapping("/404")
        public String error404() {
            return "error/404"; // carpeta y archivo donde guardas el HTML
        }

        @GetMapping("/505")
        public String error500() {
            return "error/500";
        }
    }


