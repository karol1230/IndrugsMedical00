package com.example.Indrugs.services;

import com.example.Indrugs.DTO.PedidoDTO;
import com.example.Indrugs.entities.Orden;
import com.example.Indrugs.entities.Pedido;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.repositorios.OrdenRepository;
import com.example.Indrugs.repositorios.PedidoRepository;
import com.example.Indrugs.repositorios.UsuarioRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    public PedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // ==========================================================
    // CREAR PEDIDO (desde DTO)
    // ==========================================================
    @Override
    public PedidoDTO crearPedido(PedidoDTO dto) {

        Pedido pedido = new Pedido();

        pedido.setNombrePaciente(dto.getNombrePaciente());
        pedido.setDireccion(dto.getDireccion());
        pedido.setTelefono(dto.getTelefono());
        pedido.setHoraPedido(LocalDateTime.now());
        pedido.setEstado(dto.getEstado());
        pedido.setObservaciones(dto.getObservaciones());

        pedidoRepository.save(pedido);

        return convertToDTO(pedido);
    }

    // ==========================================================
    // CREAR PEDIDO DESDE ORDEN
    // ==========================================================
    @Override
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

    // ==========================================================
    // LISTAR TODOS LOS PEDIDOS
    // ==========================================================
    @Override
    public List<PedidoDTO> listarPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // LISTAR PEDIDOS POR DOMICILIARIO
    // ==========================================================
    @Override
    public List<PedidoDTO> listarPorDomiciliario(Usuario domiciliario) {
        return pedidoRepository.findByDomiciliario(domiciliario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // BUSCAR POR ID
    // ==========================================================
    @Override
    public PedidoDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        return convertToDTO(pedido);
    }

    // ==========================================================
    // ACTUALIZAR ESTADO + ENVIAR CORREO SI "HE LLEGADO"
    // ==========================================================
    @Override
    public void actualizarEstado(Long id, String nuevoEstado) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        if (nuevoEstado.equalsIgnoreCase("He llegado")) {

            Usuario cliente = pedido.getOrden().getPaciente();

            if (cliente != null && cliente.getCorreo() != null) {
                try {
                    emailService.enviarCorreo(
                            cliente.getCorreo(),
                            "Tu orden con medicamento ha llegado",
                            "<p>Hola " + cliente.getNombre() +
                                    ", indrugs medical te informa que tu pedido acaba de llegar a tu lugar de vivienda.</p>"
                    );
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }

    // ==========================================================
    // ELIMINAR PEDIDO
    // ==========================================================
    @Override
    public void eliminarPedido(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("No existe el pedido con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    // ==========================================================
    // MÉTODOS PARA EL DASHBOARD
    // ==========================================================
    @Override
    public long countByEstado(String estado) {
        return pedidoRepository.countByEstado(estado);
    }

    @Override
    public long countTotal() {
        return pedidoRepository.count();
    }

    // ==========================================================
    // CONVERTIR PEDIDO A DTO
    // ==========================================================
    private PedidoDTO convertToDTO(Pedido p) {
        String nombreDomiciliario = (p.getDomiciliario() != null)
                ? p.getDomiciliario().getNombre()
                : null;

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
}
