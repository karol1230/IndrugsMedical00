package com.example.Indrugs.services;

import com.example.Indrugs.DTO.InventarioDTO;
import com.example.Indrugs.entities.Medicamentos;

import java.util.List;

public interface InventarioService {

    // Leer inventario
    List<InventarioDTO> read();

    // Crear inventario nuevo
    void crear(InventarioDTO inventarioDTO);

    // Actualizar inventario
    void actualizar(Long idInventario, InventarioDTO inventarioDTO);

    // Total de unidades en stock (suma del stock)
    Long totalUnidadesEnStock();

    // Buscar inventario por id
    InventarioDTO buscarPorId(Long idInventario);

    // Buscar por estado (ACTIVO / INACTIVO)
    List<InventarioDTO> findByEstado(String estadoMed);

    // 🆕 Descontar stock cuando se asigna un medicamento a una orden
    void descontarStock(Medicamentos medicamento, int cantidad);

    // 🆕 NUEVO → Total de medicamentos registrados en BD
    long totalMedicamentosRegistrados();
}
