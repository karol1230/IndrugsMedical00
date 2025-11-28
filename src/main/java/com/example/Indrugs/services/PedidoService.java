package com.example.Indrugs.services;

import com.example.Indrugs.DTO.PedidoDTO;
import com.example.Indrugs.entities.Usuario;
import java.util.List;

public interface PedidoService {

    // ========================
    // CREACIÓN
    // ========================
    PedidoDTO crearPedido(PedidoDTO dto);
    void crearPedidoDesdeOrden(Long idOrden, Long idDomiciliario);

    // ========================
    // LECTURA
    // ========================
    List<PedidoDTO> listarPedidos();
    List<PedidoDTO> listarPorDomiciliario(Usuario domiciliario);
    PedidoDTO buscarPorId(Long id);

    // ========================
    // ACTUALIZACIÓN
    // ========================
    void actualizarEstado(Long id, String nuevoEstado);

    // ========================
    // ELIMINACIÓN
    // ========================
    void eliminarPedido(Long id);

    // ========================
    // ESTADÍSTICAS
    // ========================
    long countByEstado(String estado);
    long countTotal();
}
