package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.MedicamentoDTO;
import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.PedidoDTO;
import com.example.Indrugs.entities.*;
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

            // 🔥 Estado inicial correcto
            ordenDTO.setEstadoOrden("PENDIENTE_ASIGNACIÓN");

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

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        try {
            MedicamentoDTO medicamento = medicamentosService.buscarPorIdMedicamento(idMedicamento);
            if (medicamento == null) {
                model.addAttribute("error", "Medicamento no encontrado");
                return "error";
            }

            ordenDTO.setPacienteNombre(usuario.getNombre());
            ordenDTO.setNombreMedicamento(medicamento.getNombreMedicamento());

            ordenDTO.setEstadoOrden("PENDIENTE_ASIGNACIÓN");

            if (ordenDTO.getFechaEntrega() == null) {
                ordenDTO.setFechaEntrega(LocalDateTime.now().plusDays(1));
            }

            if (formulaFile != null && !formulaFile.isEmpty()) {
                String rutaArchivo = archivosService.guardarFormulaMedica(formulaFile);
                ordenDTO.setFotoFormula(rutaArchivo);
            }

            ordenService.crear(ordenDTO, usuario.getIdUsuario(), idMedicamento);

            redirectAttributes.addFlashAttribute("mensaje", "Orden creada exitosamente");

            // 🔥 Datos que la factura SI necesita
            model.addAttribute("nombrePaciente", ordenDTO.getPacienteNombre());
            model.addAttribute("direccionPaciente", usuario.getDireccion());
            model.addAttribute("telefonoPaciente", usuario.getTelefono());

            model.addAttribute("orden", ordenDTO);
            model.addAttribute("medicamento", medicamento);

            return "pacientes/confirmacionPedido";

        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar la orden: " + e.getMessage());
            return "pacientes/4.pagina_domicilio";
        }
    }


    // ============================================================
    // ADMIN – ELIMINAR ORDEN
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
            Orden orden = ordenService.obtenerOrdenEntityPorId(idOrden);
            Usuario domiciliario = usuarioService.obtenerUsuarioPorId(idDomiciliario);

            if (orden == null || domiciliario == null) {
                redirectAttributes.addFlashAttribute("error", "Orden o domiciliario no válido");
                return "redirect:/18.pagina_orden_admin";
            }

            // Crear o actualizar domicilio
            Domicilio domicilio = orden.getDomicilio();
            if (domicilio == null) {
                domicilio = new Domicilio();
                domicilio.setOrden(orden); // Relación bidireccional
                orden.setDomicilio(domicilio);
            }

            domicilio.setDomiciliario(domiciliario);
            domicilio.setEstado("En camino");        // Estado interno
            domicilio.setEstadoDomicilio("En camino");
            orden.setEstadoOrden("ASIGNADA");


            // Guardar la orden y el domicilio
            ordenService.guardarOrden(orden);

            // 🔥 CREAR PEDIDO AUTOMÁTICAMENTE para el domiciliario
            // Esto garantiza que el Domiciliario lo vea en "mis pedidos"
            pedidoService.crearPedidoDesdeOrden(idOrden, idDomiciliario);

            // Actualizar stock del primer medicamento
            Medicamentos med = orden.getMedicamentos().isEmpty() ? null : orden.getMedicamentos().get(0);

            if (med != null) {
                Inventario inventario = inventarioRepository.findAll().stream()
                        .filter(inv -> inv.getIdMedicamento() != null &&
                                inv.getIdMedicamento().getNombreMedicamento()
                                        .equalsIgnoreCase(med.getNombreMedicamento()))
                        .findFirst()
                        .orElse(null);

                if (inventario != null && inventario.getStock() >= orden.getCantidad()) {
                    inventario.setStock(inventario.getStock() - orden.getCantidad());
                    inventario.setFechaSalida(LocalDateTime.now());
                    inventarioRepository.save(inventario);

                    redirectAttributes.addFlashAttribute("mensaje",
                            "Domiciliario asignado, pedido creado y stock actualizado correctamente.");
                } else {
                    redirectAttributes.addFlashAttribute("error",
                            "No hay suficiente stock para este medicamento.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "La orden no tiene medicamentos asignados.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al asignar domiciliario: " + e.getMessage());
        }

        return "redirect:/18.pagina_orden_admin";
    }


    // ============================================================
    // DOMICILIARIO – MIS PEDIDOS
    // ============================================================

    @GetMapping("/pedidos-domiciliario")
    public String verPedidosDomiciliario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<PedidoDTO> pedidosAsignados = pedidoService.obtenerPedidosAsignados(usuario.getIdUsuario());
        model.addAttribute("pedidos", pedidosAsignados);

        return "domiciliario/25.pedidos_asignados";
    }








}