package com.example.Indrugs.services;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.entities.Domicilio;
import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.entities.Orden;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.OrdenMapper;
import com.example.Indrugs.repositorios.DomicilioRepository;
import com.example.Indrugs.repositorios.MedicamentoRepository;
import com.example.Indrugs.repositorios.OrdenRepository;
import com.example.Indrugs.repositorios.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DomicilioRepository domicilioRepository;
    private final EmailService emailService;

    private static final String UPLOAD_DIR = "uploads";

    @Autowired
    public OrdenServiceImpl(
            OrdenRepository ordenRepository,
            MedicamentoRepository medicamentoRepository,
            UsuarioRepository usuarioRepository,
            DomicilioRepository domicilioRepository,
            EmailService emailService
    ) {
        this.ordenRepository = ordenRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.domicilioRepository = domicilioRepository;
        this.emailService = emailService;
    }

    @Override
    public List<OrdenDTO> listarOrdenes() {
        return OrdenMapper.toDTOList(ordenRepository.findAll());
    }

    @Override
    public List<OrdenDTO> listarOrdenesP(Long idUsuario) {
        return ordenRepository.findByPaciente_IdUsuario(idUsuario)
                .stream().map(OrdenMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public void marcarComoEntregada(Long idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + idOrden));

        orden.setEstadoOrden("ENTREGADA");
        ordenRepository.save(orden);
    }

    @Override
    public void crear(OrdenDTO ordenDTO, Long idUsuario, Long idMedicamento) {
        Orden orden = OrdenMapper.toEntity(ordenDTO);

        Usuario usuario = usuarioRepository.findByIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        orden.setPaciente(usuario);

        Medicamentos medicamento = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));
        orden.setMedicamentos(List.of(medicamento));

        orden.setFormulaMedica(ordenDTO.getFotoFormula());

        if (orden.getEstadoOrden() == null || orden.getEstadoOrden().isEmpty()) {
            orden.setEstadoOrden("PENDIENTE_ASIGNACION");
        }

        ordenRepository.save(orden);
        crearDomicilioConOrden(orden);
    }

    @Override
    public void crearDomicilioConOrden(Orden orden) {
        Domicilio domicilio = new Domicilio();
        domicilio.setOrden(orden);
        domicilio.setUbicacionDomicilio(orden.getDireccionOrden());
        domicilio.setEstadoDomicilio("EN ESPERA");
        domicilio.setFechaEntregaDomicilio(orden.getFechaEntrega());
        domicilioRepository.save(domicilio);
    }

    @Override
    public void eliminar(Long idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + idOrden));

        String correoPaciente = null;
        String nombrePaciente = "Paciente";

        if (orden.getPaciente() != null) {
            correoPaciente = orden.getPaciente().getCorreo();
            nombrePaciente = orden.getPaciente().getNombre();
        }

        String nombreArchivo = orden.getFotoFormula();
        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
            try {
                Path rutaArchivo = Paths.get(UPLOAD_DIR).resolve(nombreArchivo).toAbsolutePath();
                File archivo = rutaArchivo.toFile();
                if (archivo.exists()) Files.delete(rutaArchivo);
            } catch (Exception ignored) {}
        }

        ordenRepository.deleteById(idOrden);

        if (correoPaciente != null && !correoPaciente.isEmpty()) {
            try {
                String asunto = "Notificación: Orden eliminada";
                String mensajeHtml =
                        "<html><body>" +
                                "<h2>Hola " + nombrePaciente + ",</h2>" +
                                "<p>Tu orden con ID <strong>" + idOrden + "</strong> ha sido eliminada.</p>" +
                                "</body></html>";

                emailService.enviarCorreo(correoPaciente, asunto, mensajeHtml);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public long countOrdenActivo() {
        return ordenRepository.countByEstadoOrden("ACTIVO");
    }

    @Override
    public long countOrdenesCompletadas() {
        return ordenRepository.countByEstadoOrden("ENTREGADA");
    }

    @Override
    public long countOrdenesInactivas() {
        return ordenRepository.countByEstadoOrden("INACTIVA");
    }

    @Override
    public long countTotalOrdenes() {
        return ordenRepository.count();
    }

    @Override
    public List<OrdenDTO> ObtenerOrdenesRecientes() {
        return ordenRepository.findTop4ByOrderByIdOrdenDesc()
                .stream().map(OrdenMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> ObtenerResumenOrden() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalOrdenes", countTotalOrdenes());
        stats.put("ordenesActivas", countOrdenActivo());
        stats.put("ordenesCompletadas", countOrdenesCompletadas());
        stats.put("ordenesInactivas", countOrdenesInactivas());
        stats.put("ordenesRecientes", ordenRepository.findTop4ByOrderByIdOrdenDesc());

        return stats;
    }

    @Override
    public OrdenDTO obtenerOrdenPorId(Long id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
        return OrdenMapper.toDTO(orden);
    }

    @Override
    public void asignarMedicamentoAOrden(Long idOrden, Long idMedicamento, int cantidad) {
        // Puedes solicitarme implementarlo
    }

    public void actualizarEstado(Long idOrden, String nuevoEstado) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        orden.setEstadoOrden(nuevoEstado);
        ordenRepository.save(orden);
    }


    @Override
    public Orden obtenerOrdenEntityPorId(Long idOrden) {
        return ordenRepository.findById(idOrden).orElse(null);
    }

    @Override
    public void guardarOrden(Orden orden) {
        ordenRepository.save(orden);
    }

    @Override
    public List<OrdenDTO> listarOrdenesAsignadas() {
        List<Orden> ordenes = ordenRepository.findByEstadoOrden("ASIGNADA");

        return ordenes.stream().map(orden -> {
            OrdenDTO dto = new OrdenDTO();

            dto.setIdOrden(orden.getIdOrden());

            // Paciente
            if (orden.getPaciente() != null) {
                dto.setPacienteNombre(orden.getPaciente().getNombre());
            }

            dto.setDireccionOrden(orden.getDireccionOrden());
            dto.setTelefonoOrden(orden.getTelefonoOrden());
            dto.setFotoFormula(orden.getFotoFormula());
            dto.setEpsOrden(orden.getEpsOrden());

            // Lista de medicamentos
            if (orden.getMedicamentos() != null) {
                dto.setMedicamentos(
                        orden.getMedicamentos()
                                .stream()
                                .map(Medicamentos::getNombreMedicamento)
                                .toList()
                );
            }

            // 🔵 DOMICILIARIO ASIGNADO Y ESTADO
            Domicilio domicilio = orden.getDomicilio();
            if (domicilio != null) {
                // Nombre domiciliario
                if (domicilio.getDomiciliario() != null) {
                    dto.setNombreDomiciliario(domicilio.getDomiciliario().getNombre());
                } else {
                    dto.setNombreDomiciliario("No asignado");
                }

                // Estado domicilio
                if (domicilio.getEstadoDomicilio() != null && !domicilio.getEstadoDomicilio().isEmpty()) {
                    dto.setEstadoDomicilio(domicilio.getEstadoDomicilio());
                } else {
                    dto.setEstadoDomicilio("En camino"); // valor por defecto
                }
            } else {
                dto.setNombreDomiciliario("No asignado");
                dto.setEstadoDomicilio("No asignado");
            }

            return dto;
        }).toList();
    }




}
