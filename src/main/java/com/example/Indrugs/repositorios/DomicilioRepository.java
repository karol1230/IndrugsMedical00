package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Domicilio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DomicilioRepository extends JpaRepository<Domicilio, Long> {

    List<Domicilio> findByEstadoDomicilio(String estadoDomicilio);

    List<Domicilio> findTop3ByOrderByIdDomicilioDesc();

    long countByEstadoDomicilio(String estadoDomicilio);

    Optional<Domicilio> findByIdDomicilio(Long idDomicilio);

    // ➕ NUEVO: Para generar el resumen (pedidos activos, totales, etc.)
    @Query("SELECT COUNT(d) FROM Domicilio d")
    long totalDomicilios();

    @Query("SELECT COUNT(d) FROM Domicilio d WHERE d.estadoDomicilio = 'Activo'")
    long totalActivos();

    @Query("SELECT COUNT(d) FROM Domicilio d WHERE d.estadoDomicilio = 'Inactivo'")
    long totalInactivos();
}
