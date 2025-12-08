package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.MedicamentoDTO;
import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.InventarioDTO;
import com.example.Indrugs.entities.Inventario;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.repositorios.InventarioRepository;
import com.example.Indrugs.services.*;
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

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioRepository inventarioRepository;


    // ============================================================
    // VISTAS
    // ============================================================

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



    // ============================================================
    // CREAR ORDEN
    // ============================================================

    @GetMapping("/nuevo")
    public String mostrarFormulario(@RequestParam("idMedicamento") Long idMedicamento,
                                    @RequestParam("cantidad") Integer cantidad,
                                    HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        try {

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
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // 👉 Usuario logueado desde la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        try {
            // Buscar medicamento
            MedicamentoDTO medicamento = medicamentosService.buscarPorIdMedicamento(idMedicamento);
            if (medicamento == null) {
                model.addAttribute("error", "Medicamento no encontrado");
                return "error";
            }

            // Configurar orden
            ordenDTO.setPacienteNombre(usuario.getNombre());
            ordenDTO.setNombreMedicamento(medicamento.getNombreMedicamento());
            ordenDTO.setEstadoOrden("ACTIVO");

            if (ordenDTO.getFechaEntrega() == null) {
                ordenDTO.setFechaEntrega(LocalDateTime.now().plusDays(1));
            }

            // Guardar archivo de fórmula si existe
            if (formulaFile != null && !formulaFile.isEmpty()) {
                String rutaArchivo = archivosService.guardarFormulaMedica(formulaFile);
                ordenDTO.setFotoFormula(rutaArchivo);
            }

            // Crear orden en la base de datos
            ordenService.crear(ordenDTO, usuario.getIdUsuario(), idMedicamento);

            // Datos para la vista de confirmación
            model.addAttribute("orden", ordenDTO);
            model.addAttribute("medicamento", medicamento);
            model.addAttribute("usuario", usuario);
            model.addAttribute("nombrePaciente", usuario.getNombre());
            model.addAttribute("correoPaciente", usuario.getCorreo());
            model.addAttribute("telefonoPaciente", usuario.getTelefono());
            model.addAttribute("direccionPaciente", usuario.getDireccion());
            model.addAttribute("descripcionCantidad", ordenDTO.getCantidad() + " unidades");
            model.addAttribute("fechaRegistro", LocalDateTime.now());

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje", "Orden creada exitosamente");

            // 👉 Retornar la vista de confirmación
            return "pacientes/confirmacionPedido";

        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar la orden: " + e.getMessage());
            return "pacientes/4.pagina_domicilio";
        }
    }




    // ============================================================
    // ADMINISTRADOR: ELIMINAR / ACEPTAR / DENEGAR
    // ============================================================

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


    // ============================================================
    // ASIGNAR DOMICILIARIO
    // ============================================================

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

            OrdenDTO orden = ordenService.obtenerOrdenPorId(idOrden);

            // Actualizar stock
            Inventario inventario = inventarioRepository.findAll().stream()
                    .filter(inv -> inv.getIdMedicamento() != null &&
                            inv.getIdMedicamento().getNombreMedicamento()
                                    .equalsIgnoreCase(orden.getNombreMedicamento()))
                    .findFirst()
                    .orElse(null);

            if (inventario != null && inventario.getStock() >= orden.getCantidad()) {

                inventario.setStock(inventario.getStock() - orden.getCantidad());
                inventario.setFechaSalida(LocalDateTime.now());
                inventarioRepository.save(inventario);

                redirectAttributes.addFlashAttribute("mensaje",
                        "Domiciliario asignado y stock actualizado correctamente.");

            } else {
                redirectAttributes.addFlashAttribute("error",
                        "No hay suficiente stock para este medicamento.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al asignar domiciliario: " + e.getMessage());
        }

        return "redirect:/18.pagina_orden_admin";
    }


    // ============================================================
    // DOMICILIARIO: VER SUS PEDIDOS
    // ============================================================

    @GetMapping("/pedidos-domiciliario") // ✅ ya no choca con el otro controller
    public String verPedidosDomiciliario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("pedidos", pedidoService.listarPorDomiciliario(usuario));
        return "domiciliario/25.pedidos_asignados";
    }

}
