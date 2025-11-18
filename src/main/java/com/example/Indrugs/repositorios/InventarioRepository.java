package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Inventario;
import com.example.Indrugs.entities.Medicamentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    List<Inventario> findByEstadoMed(String estadoMed);

    @Query("SELECT SUM(i.stock) FROM Inventario i")
    Long contarUnidadesEnStock();

    boolean existsByIdMedicamento_IdMedicamento(Long idMedicamento);

    // 🆕 NUEVO: obtener el inventario más reciente del medicamento
    Optional<Inventario> findTopByIdMedicamentoOrderByFechaEntradaDesc(Medicamentos medicamento);
}
