package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.MedicamentoDTO;
import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.ArchivosService;
import com.example.Indrugs.services.MedicamentosService;
import com.example.Indrugs.services.OrdenService;
import com.example.Indrugs.services.PedidoServiceImpl;
import com.example.Indrugs.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private MedicamentosService medicamentosService;

    @Autowired
    private ArchivosService archivosService;

    @Autowired
    private PedidoServiceImpl pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    // ========================
    // VISTAS
    // ========================

    @GetMapping("/14.pagina_ordenes")
    public String verOrdenesDirecto(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("ordenes", ordenService.listarOrdenes());
        return "domiciliario/14.pagina_ordenes";
    }

    @GetMapping("/16.pagina_carrito_med")
    public String verOrdenesPaciente(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("ordenes", ordenService.listarOrdenesP(usuario.getIdUsuario()));
        return "pacientes/16.pagina_carrito_med";
    }

    @GetMapping("/18.pagina_orden_admin")
    public String verOrdenesAdmin(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("ordenes", ordenService.listarOrdenes());
        return "administrador/18.pagina_orden_admin";
    }

    // ========================
    // CREAR ORDEN
    // ========================

    @GetMapping("/nuevo")
    public String mostrarFormulario(@RequestParam("idMedicamento") Long idMedicamento,
                                    @RequestParam("cantidad") Integer cantidad,
                                    HttpSession session, Model model) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuario == null) return "redirect:/login";

            MedicamentoDTO medicamento = medicamentosService.buscarPorIdMedicamento(idMedicamento);
            if (medicamento == null) {
                model.addAttribute("error", "Medicamento no encontrado");
                return "error";
            }

            OrdenDTO ordenDTO = new OrdenDTO();
            ordenDTO.setPacienteNombre(usuario.getNombre());
            ordenDTO.setCantidad(cantidad);
            ordenDTO.setNombreMedicamento(medicamento.getNombreMedicamento());
            ordenDTO.setEstadoOrden("ACTIVO");

            model.addAttribute("orden", ordenDTO);
            model.addAttribute("usuarioLogueado", usuario);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("idMedicamento", idMedicamento);
            model.addAttribute("cantidad", cantidad);

            return "pacientes/4.pagina_domicilio";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/orden/guardar")
    public String guardarOrden(@ModelAttribute OrdenDTO ordenDTO,
                               @RequestParam("formulaFile") MultipartFile formulaFile,
                               @RequestParam("idMedicamento") Long idMedicamento,
                               HttpSession session, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuario == null) return "redirect:/login";

            MedicamentoDTO medicamento = medicamentosService.buscarPorIdMedicamento(idMedicamento);
            if (medicamento == null) {
                model.addAttribute("error", "Medicamento no encontrado");
                return "error";
            }

            ordenDTO.setPacienteNombre(usuario.getNombre());
            ordenDTO.setNombreMedicamento(medicamento.getNombreMedicamento());
            ordenDTO.setEstadoOrden("ACTIVO");

            if (ordenDTO.getFechaEntrega() == null) {
                ordenDTO.setFechaEntrega(LocalDateTime.now().plusDays(1));
            }

            if (formulaFile != null && !formulaFile.isEmpty()) {
                String rutaArchivo = archivosService.guardarFormulaMedica(formulaFile);
                ordenDTO.setFotoFormula(rutaArchivo);
            }

            ordenService.crear(ordenDTO, usuario.getIdUsuario(), idMedicamento);
            redirectAttributes.addFlashAttribute("mensaje", "Orden creada exitosamente");

            model.addAttribute("medicamento", medicamento);
            model.addAttribute("orden", ordenDTO);
            return "pacientes/confirmacionPedido";

        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar la orden: " + e.getMessage());
            model.addAttribute("orden", ordenDTO);
            model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
            return "pacientes/4.pagina_domicilio";
        }
    }

    // ========================
    // ADMINISTRADOR
    // ========================

    @GetMapping("/ordenes/eliminar/{idOrden}")
    public String eliminarOrden(@PathVariable Long idOrden, RedirectAttributes redirectAttributes) {
        try {
            OrdenDTO orden = ordenService.obtenerOrdenPorId(idOrden);
            if (orden != null && orden.getFotoFormula() != null) {
                archivosService.eliminarArchivo(orden.getFotoFormula());
            }

            ordenService.eliminar(idOrden);
            redirectAttributes.addFlashAttribute("mensaje", "Orden eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la orden: " + e.getMessage());
        }

        return "redirect:/18.pagina_orden_admin";
    }

    @GetMapping("/orden/admin/aceptar/{id}")
    public String aceptarOrden(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            OrdenDTO orden = ordenService.obtenerOrdenPorId(id);
            orden.setEstadoOrden("Aceptada");
            ordenService.crear(orden, orden.getPaciente(), orden.getIdMedicamento());
            redirectAttributes.addFlashAttribute("mensaje", "Orden aceptada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aceptar la orden: " + e.getMessage());
        }
        return "redirect:/18.pagina_orden_admin";
    }

    @GetMapping("/orden/admin/denegar/{id}")
    public String denegarOrden(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            OrdenDTO orden = ordenService.obtenerOrdenPorId(id);
            orden.setEstadoOrden("Denegada");
            ordenService.crear(orden, orden.getPaciente(), orden.getIdMedicamento());
            redirectAttributes.addFlashAttribute("mensaje", "Orden denegada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al denegar la orden: " + e.getMessage());
        }
        return "redirect:/18.pagina_orden_admin";
    }

    // ========================
    // ASIGNAR DOMICILIARIO
    // ========================

    @GetMapping("/admin/asignar-domiciliario/{idOrden}")
    public String mostrarFormularioAsignacion(@PathVariable Long idOrden, Model model, HttpSession session) {
        Usuario admin = (Usuario) session.getAttribute("usuarioLogueado");
        if (admin == null) return "redirect:/login";

        OrdenDTO orden = ordenService.obtenerOrdenPorId(idOrden);
        List<Usuario> domiciliarios = usuarioService.listarPorRol("DOMICILIARIO");

        model.addAttribute("orden", orden);
        model.addAttribute("domiciliarios", domiciliarios);
        return "administrador/25.asignar_domiciliario_orden";
    }

    @PostMapping("/orden/asignar-domiciliario")
    public String asignarDomiciliario(@RequestParam Long idOrden,
                                      @RequestParam Long idDomiciliario,
                                      RedirectAttributes redirectAttributes) {
        try {
            pedidoService.crearPedidoDesdeOrden(idOrden, idDomiciliario);
            redirectAttributes.addFlashAttribute("mensaje", "Domiciliario asignado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al asignar domiciliario: " + e.getMessage());
        }
        return "redirect:/18.pagina_orden_admin";
    }

    // ========================
    // DOMICILIARIO: PEDIDOS ASIGNADOS
    // ========================

    @GetMapping("/domiciliario/pedidos")
    public String verPedidosDomiciliario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("pedidos", pedidoService.listarPorDomiciliario(usuario));
        return "domiciliario/25.pedidos_asignados";
    }

    @PostMapping("/actualizar/pedido/{id}")
    public String actualizarEstadoPedido(@PathVariable Long id,
                                         @RequestParam String estado,
                                         HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        pedidoService.actualizarEstado(id, estado);
        return "redirect:/domiciliario/pedidos";
    }
}
