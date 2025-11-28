package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Pedido;
import com.example.Indrugs.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // =============================
    // BUSCAR POR DOMICILIARIO
    // =============================
    List<Pedido> findByDomiciliario(Usuario domiciliario);

    @Query("SELECT p FROM Pedido p WHERE p.domiciliario.id = :idDomiciliario")
    List<Pedido> findByDomiciliarioId(@Param("idDomiciliario") Long idDomiciliario);


    // =============================
    // ➤ MÉTODOS PARA ESTADÍSTICAS
    // =============================

    // Contar pedidos por estado
    long countByEstado(String estado);

    // count() ya viene en JpaRepository
}
