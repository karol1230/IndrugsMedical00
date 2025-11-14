package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.VehiculoDTO;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.VehiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("/12.pagina_vehiculos")
    public String listarVehiculos(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        List<VehiculoDTO> vehiculos = vehiculoService.read(usuarioLogueado.getIdUsuario());
        model.addAttribute("vehiculos", vehiculos);
        return "domiciliario/12.pagina_vehiculos";
    }

    @GetMapping("/13.pagina_agregar_vehiculo")
    public String mostrarFormularioAgregar(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        model.addAttribute("vehiculo", new VehiculoDTO());
        model.addAttribute("usuarioLogueado", usuarioLogueado);

        return "domiciliario/13_pagina_agregar_vehiculo";
    }

    @PostMapping("/agregar_vehiculo")
    public String agregarVehiculo(@ModelAttribute("vehiculo") VehiculoDTO vehiculoDTO,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) { // ✅ Agregado

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        vehiculoDTO.setIdUsuario(usuarioLogueado.getIdUsuario());
        vehiculoService.crear(vehiculoDTO);

        // ✅ Agregamos mensaje de éxito
        redirectAttributes.addFlashAttribute("mensajeExito", "Vehículo registrado exitosamente");

        return "redirect:/12.pagina_vehiculos";
    }


    @PostMapping("/vehiculo/{idVehiculo}/estado")
    public String cambiarEstado(@PathVariable("idVehiculo") Long idVehiculo,
                                @RequestParam("nuevoEstado") String nuevoEstado) {
        vehiculoService.cambiarEstado(idVehiculo, nuevoEstado);
        return "redirect:/12.pagina_vehiculos"; // 🔹 Redirige para refrescar lista
    }

    @GetMapping("/vehiculo/eliminar/{idVehiculo}")
    public String eliminarVehiculo(@PathVariable("idVehiculo") Long idVehiculo,
                                   RedirectAttributes redirectAttributes) {

        vehiculoService.eliminar(idVehiculo);

        // Agregar mensaje de éxito
        redirectAttributes.addFlashAttribute("mensajeExito", "Vehículo eliminado exitosamente");

        // Redirigir a la lista de vehículos
        return "redirect:/12.pagina_vehiculos";
    }

}
