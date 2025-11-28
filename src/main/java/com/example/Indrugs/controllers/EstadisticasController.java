package com.example.Indrugs.controllers;

import com.example.Indrugs.services.MedicamentosService;
import com.example.Indrugs.services.UsuarioService;
import com.example.Indrugs.services.PedidoService;
import com.example.Indrugs.services.MedicamentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private MedicamentosService medicamentoService;


    @GetMapping("/dashboard")
    public Map<String, Object> obtenerEstadisticas() {

        Map<String, Object> datos = new HashMap<>();

        // ======== USUARIOS ========
        datos.put("usuariosActivos", usuarioService.countByEstado("ACTIVO"));
        datos.put("usuariosInactivos", usuarioService.countByEstado("INACTIVO"));
        datos.put("totalUsuarios", usuarioService.countTotal());

// Estadísticas por rol
        Map<String, Long> usuariosPorRol = new HashMap<>();
        usuariosPorRol.put("Pacientes", usuarioService.countByRolNombre("Paciente"));
        usuariosPorRol.put("Domiciliarios", usuarioService.countByRolNombre("Domiciliario"));
        usuariosPorRol.put("Administradores", usuarioService.countByRolNombre("Administrador"));

        datos.put("usuariosPorRol", usuariosPorRol);


        // ======== PEDIDOS ========
        datos.put("pedidosCompletados", pedidoService.countByEstado("Completado"));
        datos.put("pedidosPendientes", pedidoService.countByEstado("Pendiente"));
        datos.put("pedidosCancelados", pedidoService.countByEstado("Cancelado"));
        datos.put("totalPedidos", pedidoService.countTotal());



        return datos;
    }
}
