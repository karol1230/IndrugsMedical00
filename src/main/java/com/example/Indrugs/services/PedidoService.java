package com.example.Indrugs.services;

import com.example.Indrugs.DTO.PedidoDTO;
import com.example.Indrugs.entities.PQRS;
import com.example.Indrugs.entities.Usuario;
import java.util.List;

public interface PedidoService {

    // ========================
    // CREACIÓN
    // ========================

    // Crear un pedido normal a partir de un DTO
    PedidoDTO crearPedido(PedidoDTO dto);

    // Crear pedido a partir de una orden y asignar domiciliario
    void crearPedidoDesdeOrden(Long idOrden, Long idDomiciliario);

    // ========================
    // LECTURA
    // ========================

    // Listar todos los pedidos
    List<PedidoDTO> listarPedidos();

    // Listar pedidos asignados a un domiciliario específico
    List<PedidoDTO> listarPorDomiciliario(Usuario domiciliario);

    // Buscar pedido por ID
    PedidoDTO buscarPorId(Long id);

    // ========================
    // ACTUALIZACIÓN
    // ========================

    // Actualizar estado de un pedido
    void actualizarEstado(Long id, String nuevoEstado);

    // ========================
    // ELIMINACIÓN
    // ========================

    // Eliminar pedido
    void eliminarPedido(Long id);



}
