package com.example.Indrugs.controllers;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioCreateDTO;
import com.example.Indrugs.services.EmailService;
import com.example.Indrugs.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioUpdateDTO;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.UsuarioMapper;
import com.example.Indrugs.repositorios.UsuarioRepository;
import com.example.Indrugs.services.bienestarService;
import com.example.Indrugs.services.InventarioService;
import com.example.Indrugs.services.OrdenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class AdminisradorController {


    private EmailService emailService;
    private final UsuarioService usuarioService;
    private final InventarioService inventarioService;
    private final OrdenService ordenService;
    private final bienestarService bienestarService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    public AdminisradorController( EmailService emailService,
            UsuarioService usuarioService,
                                  InventarioService inventarioService,
                                  OrdenService ordenService,
                                  bienestarService bienestarService) {
        this.emailService = emailService;
        this.usuarioService = usuarioService;
        this.inventarioService = inventarioService;
        this.ordenService = ordenService;
        this.bienestarService = bienestarService;
    }

    // ================================
//   PÁGINA PRINCIPAL ADMIN
// ================================
    @GetMapping("/20.pagina_principal_administrador")
    public String mostrarPaginaAdmin(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("nombreAdmin", usuario.getNombre());


        Map<String, Long> estadisticasUsuarios = usuarioService.obtenerResumenUsuarios();
        model.addAttribute("estadisticas", estadisticasUsuarios);

        List<UsuarioDTO> usuariosRecientes = usuarioService.obtenerUsuariosRecientes();
        model.addAttribute("usuariosRecientes", usuariosRecientes);

        model.addAttribute("cantidadInventario", inventarioService.totalUnidadesEnStock());

        List<OrdenDTO> ordenesRecientes = ordenService.ObtenerOrdenesRecientes();
        model.addAttribute("ordenesRecientes", ordenesRecientes);

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
        if (usuario == null) return "redirect:/login";

        // 🔥 FIX: convertir "Todos" en null para que no filtre
        if ("Todos".equals(rol)) rol = null;
        if ("Todos".equals(estado)) estado = null;

        List<UsuarioDTO> usuarios;

        if (rol != null && estado != null) {
            usuarios = usuarioService.findByRolNombreAndEstado(rol, estado);
        } else if (rol != null) {
            usuarios = usuarioService.findByRolNombre(rol);
        } else if (estado != null) {
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
        long totalMedicamentos = inventarioService.totalMedicamentosRegistrados();
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
        if (usuario == null) return "redirect:/login";

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

    // ================================
//   CORREOS MASIVOS BIENESTAR (BOTÓN MORADO)
// ================================
    @GetMapping("/admin/correos-masivos")
    public String enviarCorreosMasivos(RedirectAttributes redirectAttributes) {
        try {
            bienestarService.enviarCorreosBienestarMensual();
            redirectAttributes.addFlashAttribute("mensajeExito", "Correos de bienestar enviados correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error enviando correos masivos: " + e.getMessage());
        }
        return "redirect:/21.pagina_usuarios";
    }
    //AAAAA//



    @GetMapping("/admin/registrar-usuario")
    public String mostrarRegistrar(Model model){
        model.addAttribute("usuarioNuevo", new UsuarioCreateDTO());
        return "administrador/26.pagina_registros";

    }


    @PostMapping("/admin/registrar-usuario")
    public String crearUsuario(
            @Valid @ModelAttribute("usuarioNuevo") UsuarioCreateDTO userCreate,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "administrador/26.pagina_registros";
        }

        try {
            if (usuarioService.existsByCorreo(userCreate.getCorreo())) {
                model.addAttribute("error", "El correo ya está registrado");
                return "administrador/26.pagina_registros";
            }

            // Crear usuario en la base de datos
            usuarioService.crear(userCreate);

            // Enviar correo de registro general
            emailService.enviarCorreoRegistro(userCreate.getCorreo(), userCreate.getNombre());

            // ✅ Si el usuario es domiciliario (por ejemplo, rol = 3)
            if (userCreate.getRol() != null && userCreate.getRol().equals(3L)) {
                emailService.enviarCorreoDomiciliario(
                        userCreate.getCorreo(),
                        userCreate.getNombre(),
                        Long.valueOf(userCreate.getNumDoc()) // numDoc ya es String, no hace falta convertir
                );
            }

            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado exitosamente");
            return "redirect:/20.pagina_principal_administrador";

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "administrador/26.pagina_registros";
        }




}@GetMapping("/unete")
    public String mostrarFormularioUnete(Model model) {
        model.addAttribute("candidato", new UsuarioCreateDTO());
        return "unete_equipo"; // ← CORRECTO
    }

    @PostMapping("/unete")
    public String recibirPostulacion(
            @ModelAttribute("candidato") UsuarioCreateDTO candidato,
            @RequestParam("vehiculo") String vehiculo,
            @RequestParam("cv") MultipartFile archivoCV,
            @RequestParam("licenciaPdf") MultipartFile licenciaPdf,
            @RequestParam("tarjetaPdf") MultipartFile tarjetaPdf,
            RedirectAttributes redirectAttributes) {

        try {
            // Validación de los archivos
            if (archivoCV.isEmpty() || licenciaPdf.isEmpty() || tarjetaPdf.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debes adjuntar todos los PDFs requeridos.");
                return "redirect:/unete";
            }

            // Enviar correo con todos los PDFs y el vehículo
            emailService.enviarPostulacionCompleta(
                    candidato,
                    vehiculo,
                    archivoCV,
                    licenciaPdf,
                    tarjetaPdf
            );

            redirectAttributes.addFlashAttribute("mensaje", "Postulación enviada correctamente.");
            return "redirect:/unete";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error enviando el formulario: " + e.getMessage());
            return "redirect:/unete";
        }
    }



}
