package com.example.Indrugs.services;

import com.example.Indrugs.DTO.PedidoDTO;
import com.example.Indrugs.entities.Pedido;
import com.example.Indrugs.entities.Usuario;

import java.util.List;

public interface PedidoService {


    PedidoDTO crearPedido(PedidoDTO dto);
    void crearPedidoDesdeOrden(Long idOrden, Long idDomiciliario);


    List<PedidoDTO> listarPedidos();
    List<PedidoDTO> listarPorDomiciliario(Usuario domiciliario);
    Pedido obtenerPedidoPorId(Long id);


    List<PedidoDTO> obtenerPedidosAsignados(Long idDomiciliario);


    Pedido actualizarEstado(Long id, String nuevoEstado);


    void eliminarPedido(Long id);

    long countByEstado(String estado);
    long countTotal();
}
