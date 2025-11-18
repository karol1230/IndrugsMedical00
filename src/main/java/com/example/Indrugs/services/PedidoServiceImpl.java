package com.example.Indrugs.services;

import com.example.Indrugs.DTO.PedidoDTO;
import com.example.Indrugs.entities.Orden;
import com.example.Indrugs.entities.Pedido;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.repositorios.OrdenRepository;
import com.example.Indrugs.repositorios.PedidoRepository;
import com.example.Indrugs.repositorios.UsuarioRepository;
import jakarta.mail.MessagingException;   // ←←← FALTABA ESTE IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl {

    private final PedidoRepository pedidoRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;  // ← NECESARIO PARA ENVIAR CORREO

    public PedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // ========================
    // Listar todos los pedidos
    // ========================
    public List<PedidoDTO> listarPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ======================================
    // Listar pedidos asignados a un domiciliario
    // ======================================
    public List<PedidoDTO> listarPorDomiciliario(Usuario domiciliario) {
        return pedidoRepository.findByDomiciliario(domiciliario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========================
    // Convertir Pedido a PedidoDTO
    // ========================
    private PedidoDTO convertToDTO(Pedido p) {
        String nombreDomiciliario = (p.getDomiciliario() != null) ? p.getDomiciliario().getNombre() : null;
        return new PedidoDTO(
                p.getId(),
                p.getNombrePaciente(),
                p.getDireccion(),
                p.getHoraPedido(),
                p.getTelefono(),
                p.getObservaciones(),
                p.getEstado(),
                nombreDomiciliario
        );
    }

    // ========================
    // Actualizar estado del pedido + enviar correo si "He llegado"
    // ========================
    public void actualizarEstado(Long id, String estado) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Enviar correo SOLO cuando el domiciliario indique "He llegado"
        if (estado.equalsIgnoreCase("He llegado")) {

            Usuario cliente = pedido.getOrden().getPaciente(); // ← ESTE ES EL CLIENTE REAL

            if (cliente != null && cliente.getCorreo() != null) {
                try {
                    emailService.enviarCorreo(
                            cliente.getCorreo(),
                            "Tu orden con medicamento ha llegado",
                            "<p>Hola " + cliente.getNombre() + ", indrugs medical te informa que tu pedido acaba de llegar a tu lugar de vivienda.</p>"
                    );
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

        pedido.setEstado(estado);
        pedidoRepository.save(pedido);
    }

    // ========================
    // Asignar un domiciliario al pedido existente
    // ========================
    public void asignarDomiciliario(Long idPedido, Usuario domiciliario) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + idPedido));
        pedido.setDomiciliario(domiciliario);
        pedido.setEstado("Asignado");
        pedidoRepository.save(pedido);
    }

    // ========================
    // Crear pedido desde una orden existente para un domiciliario
    // ========================
    public void crearPedidoDesdeOrden(Long idOrden, Long idDomiciliario) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + idOrden));

        Usuario domiciliario = usuarioRepository.findById(idDomiciliario)
                .orElseThrow(() -> new RuntimeException("Domiciliario no encontrado con ID: " + idDomiciliario));

        Pedido pedido = new Pedido();
        pedido.setOrden(orden);
        pedido.setDomiciliario(domiciliario);
        pedido.setNombrePaciente(orden.getPaciente().getNombre());
        pedido.setDireccion(orden.getDireccionOrden());
        pedido.setTelefono(orden.getTelefonoOrden());
        pedido.setHoraPedido(LocalDateTime.now());
        pedido.setEstado("Asignado");
        pedido.setObservaciones("Pedido generado automáticamente desde la orden #" + idOrden);

        pedidoRepository.save(pedido);
    }
}
