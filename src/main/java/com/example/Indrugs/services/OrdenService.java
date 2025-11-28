package com.example.Indrugs.services;

import com.example.Indrugs.DTO.OrdenDTO;
import com.example.Indrugs.entities.Orden;

import java.util.List;
import java.util.Map;

public interface OrdenService {

    // ---------------------------------------------------------------
    // CRUD PRINCIPAL
    // ---------------------------------------------------------------
    List<OrdenDTO> listarOrdenes();
    List<OrdenDTO> listarOrdenesP(Long idUsuario);
    void marcarComoEntregada(Long idOrden);
    void crear (OrdenDTO ordenDTO, Long idUsuario, Long idMedicameto);
    void eliminar (Long idOrden);

    // Orden por ID
    OrdenDTO obtenerOrdenPorId(Long id);

    // Crear domicilio asociado a una orden
    void crearDomicilioConOrden(Orden orden);

    // ---------------------------------------------------------------
    // STOCK Y MEDICAMENTOS
    // ---------------------------------------------------------------
    void asignarMedicamentoAOrden(Long idOrden, Long idMedicamento, int cantidad);

    // ---------------------------------------------------------------
    // ESTADÍSTICAS
    // ---------------------------------------------------------------

    // Cantidad de órdenes activas (PENDIENTE – EN PROCESO)
    long countOrdenActivo();

    // Órdenes completadas (ESTADO = "ENTREGADA")
    long countOrdenesCompletadas();

    // Órdenes inactivas (ESTADO = "CANCELADA" o eliminadas)
    long countOrdenesInactivas();

    // Total de órdenes registradas
    long countTotalOrdenes();

    // ---------------------------------------------------------------
    // RESUMENES Y LISTAS ESPECIALES
    // ---------------------------------------------------------------
    List<OrdenDTO> ObtenerOrdenesRecientes();

    Map<String,Object> ObtenerResumenOrden();
}
