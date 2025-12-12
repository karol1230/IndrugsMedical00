package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.DomicilioDTO;
import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.PedidoDTO;
import com.example.Indrugs.entities.Pedido;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/domiciliario")
public class DomiciliarioController {

    private final UsuarioService usuarioService;
    private final DomicilioService domicilioService;
    private final VehiculoService vehiculoService;
    private final OrdenService ordenService;
    private final PedidoServiceImpl pedidoService;
    private final EmailService emailService;

    public DomiciliarioController(UsuarioService usuarioService,
                                  DomicilioService domicilioService,
                                  VehiculoService vehiculoService,
                                  OrdenService ordenService,
                                  PedidoServiceImpl pedidoService,
                                  EmailService emailService) {

        this.usuarioService = usuarioService;
        this.domicilioService = domicilioService;
        this.vehiculoService = vehiculoService;
        this.ordenService = ordenService;
        this.pedidoService = pedidoService;
        this.emailService = emailService;
    }

    // ===============================
    // DASHBOARD DOMICILIARIO
    // ===============================
    @GetMapping("/11.pagina_principal_domiciliario")
    public String mostrarPaginaDomiciliario(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("nombreDomiciliario", usuario.getNombre());

        Map<String, Object> dashboard = domicilioService.ObtenerResumen();
        model.addAttribute("totalDomiciliosActivos", dashboard.get("totalDomiciliosActivos"));
        model.addAttribute("domiciliosRecientes", dashboard.get("domiciliosRecientes"));

        List<OrdenDTO> ordenesRecientes = ordenService.ObtenerOrdenesRecientes();
        model.addAttribute("ordenesRecientes", ordenesRecientes);

        Map<String, Object> dashboardO = ordenService.ObtenerResumenOrden();
        model.addAttribute("cantidadOrdenes", dashboardO.get("totalOrdenesActivos"));

        return "domiciliario/11.pagina_principal_domiciliario";
    }

    // ===============================
    // TABLA DOMICILIOS
    // ===============================
    @GetMapping("/15.pagina_domicilio_domi")
    public String mostrarTabla(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<DomicilioDTO> domicilios = domicilioService.read();
        model.addAttribute("domicilios", domicilios);
        return "domiciliario/15.pagina_domicilio_domi";
    }

    @GetMapping("/actualizar/domicilio/{idDomicilio}")
    public String cambiarEstado(@PathVariable Long idDomicilio) {
        domicilioService.actualizar(idDomicilio);
        return "redirect:/domiciliario/15.pagina_domicilio_domi";
    }
    @GetMapping("/pedidos")
    public String mostrarPedidosDomiciliario(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        List<PedidoDTO> pedidosAsignados = pedidoService.obtenerPedidosAsignados(usuario.getIdUsuario());
        model.addAttribute("pedidos", pedidosAsignados);

        return "domiciliario/24.pagina_pedidos_domiciliario";
    }


    // ===============================
    // ACTUALIZAR ESTADO PEDIDO
    // ===============================
    @PostMapping("/pedido/actualizar/{id}")
    public String actualizarEstadoPedido(@PathVariable Long id,
                                         @RequestParam String estado,
                                         RedirectAttributes redirectAttributes) {

        Pedido pedido = pedidoService.obtenerPedidoPorId(id);

        if (pedido == null) {
            redirectAttributes.addFlashAttribute("mensajeInfo", "El pedido no existe.");
            return "redirect:/domiciliario/pedidos";
        }

        // Actualizar estado
        pedidoService.actualizarEstado(id, estado);

        // Verificar si el correo del paciente es válido antes de intentar enviar el correo
        if (estado.equalsIgnoreCase("HE_LLEGADO") && isValidEmail(pedido.getCorreoPaciente())) {
            String html = """
            <h2>El domiciliario ha llegado</h2>
            <p>Tu pedido ya está en la puerta. ¡Por favor abre para recibirlo!</p>
            """;

            emailService.enviarCorreoHtml(pedido.getCorreoPaciente(),
                    "Tu pedido ha llegado",
                    html);

            redirectAttributes.addFlashAttribute("mensajeExito", "Correo enviado exitosamente");
        } else if (estado.equalsIgnoreCase("HE_LLEGADO")) {
            redirectAttributes.addFlashAttribute("mensajeError", "El correo del paciente no está disponible o no es válido.");
        }

        return "redirect:/domiciliario/pedidos";
    }

    // Método auxiliar para validar el formato del correo
    private boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && email.contains("@");
    }




}
