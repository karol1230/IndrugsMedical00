package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioUpdateDTO;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.UsuarioMapper;
import com.example.Indrugs.repositorios.UsuarioRepository;
import com.example.Indrugs.services.InventarioService;
import com.example.Indrugs.services.OrdenService;
import com.example.Indrugs.services.UsuarioService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class AdminisradorController {

    private final UsuarioService usuarioService;
    private final InventarioService inventarioService;
    private final OrdenService ordenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AdminisradorController(UsuarioService usuarioService,
                                  InventarioService inventarioService,
                                  OrdenService ordenService) {

        this.usuarioService = usuarioService;
        this.inventarioService = inventarioService;
        this.ordenService = ordenService;
    }

    // ================================
    //   PÁGINA PRINCIPAL ADMIN
    // ================================
    @GetMapping("/20.pagina_principal_administrador")
    public String mostrarPaginaAdmin(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login";
        }

        // Estadísticas de usuarios
        Map<String, Long> estadisticasUsuarios = usuarioService.obtenerResumenUsuarios();
        model.addAttribute("estadisticas", estadisticasUsuarios);

        // Usuarios recientes
        List<UsuarioDTO> usuariosRecientes = usuarioService.obtenerUsuariosRecientes();
        model.addAttribute("usuariosRecientes", usuariosRecientes);

        // Inventario total (stock)
        model.addAttribute("cantidadInventario", inventarioService.totalUnidadesEnStock());

        // Órdenes recientes
        List<OrdenDTO> ordenesRecientes = ordenService.ObtenerOrdenesRecientes();
        model.addAttribute("ordenesRecientes", ordenesRecientes);

        // Resumen ordenes
        Map<String, Object> dashboard = ordenService.ObtenerResumenOrden();
        model.addAttribute("cantidadOrdenes", dashboard.get("totalOrdenes"));

        return "administrador/20.pagina_principal_administrador";
    }

    // ================================
    //   LISTAR / FILTRAR USUARIOS
    // ================================
    @GetMapping("/21.pagina_usuarios")
    public String gestionUsuarios(@RequestParam(required = false) String rol,
                                  @RequestParam(required = false) String estado,
                                  HttpSession session,
                                  Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login";
        }

        List<UsuarioDTO> usuarios;

        if (rol != null && !rol.isEmpty() && estado != null && !estado.isEmpty()) {
            usuarios = usuarioService.findByRolNombreAndEstado(rol, estado);
        } else if (rol != null && !rol.isEmpty()) {
            usuarios = usuarioService.findByRolNombre(rol);
        } else if (estado != null && !estado.isEmpty()) {
            usuarios = usuarioService.findByStatus(estado);
        } else {
            usuarios = usuarioService.read();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("rolSeleccionado", rol);
        model.addAttribute("estadoSeleccionado", estado);

        return "administrador/21.pagina_usuarios";
    }

    // ================================
    //   ESTADÍSTICAS PARA LA GRÁFICA
    // ================================
    @GetMapping("/admin/estadisticas-usuarios")
    @ResponseBody
    public Map<String, Object> estadisticasUsuarios() {

        long totalPacientes = usuarioRepository.countByRol_nombreRol("Paciente");
        long totalActivos = usuarioRepository.countByEstado("ACTIVO");
        long totalInactivos = usuarioRepository.countByEstado("INACTIVO");

        // 🔥 FIX 1: total medicamentos reales, NO stock
        long totalMedicamentos = inventarioService.totalMedicamentosRegistrados();

        // 🔥 FIX 2: todas las órdenes COMPLETADAS (estado = ENTREGADA)
        long ordenesCompletadas = ordenService.countOrdenesCompletadas();

        Map<String, Object> stats = new HashMap<>();

        stats.put("pacientes", totalPacientes);
        stats.put("cantidadMedicamentos", totalMedicamentos);
        stats.put("ordenesCompletadas", ordenesCompletadas);
        stats.put("activos", totalActivos);
        stats.put("inactivos", totalInactivos);

        return stats;
    }

    // ================================
    //   EDITAR USUARIO
    // ================================
    @GetMapping("/actualizar")
    public String mostrarFormularioEdicion(@RequestParam Long idUsuario,
                                           HttpSession session,
                                           Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            UsuarioDTO usuariodto = usuarioService.findById(idUsuario);
            UsuarioUpdateDTO usuarioUpdate = UsuarioMapper.toUpdateDTO(usuariodto);

            model.addAttribute("usuario", usuarioUpdate);
            model.addAttribute("estados", List.of("ACTIVO", "INACTIVO"));

            return "administrador/actualizar_usuario";

        } catch (Exception e) {
            return "redirect:/21.pagina_usuarios?error=Usuario no encontrado";
        }
    }

    @PostMapping("/actualizar")
    public String actualizarUsuario(@RequestParam Long idUsuario,
                                    UsuarioUpdateDTO userUpdate,
                                    RedirectAttributes redirectAttributes) {

        try {
            usuarioService.actualizar(idUsuario, userUpdate);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/21.pagina_usuarios";
    }

    // ================================
    //   ASIGNAR MEDICAMENTO A ORDEN
    // ================================
    @PostMapping("/asignarMedicamento")
    public String asignarMedicamento(@RequestParam Long idOrden,
                                     @RequestParam Long idMedicamento,
                                     @RequestParam int cantidad,
                                     RedirectAttributes redirectAttributes) {

        try {
            ordenService.asignarMedicamentoAOrden(idOrden, idMedicamento, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Medicamento asignado correctamente y stock actualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/20.pagina_principal_administrador";
    }
}
