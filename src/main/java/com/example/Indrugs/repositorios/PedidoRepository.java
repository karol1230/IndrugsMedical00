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

    // ✅ Buscar pedidos por domiciliario usando la entidad
    List<Pedido> findByDomiciliario(Usuario domiciliario);

    // ✅ Nuevo método: Buscar pedidos por ID del domiciliario
    @Query("SELECT p FROM Pedido p WHERE p.domiciliario.id = :idDomiciliario")
    List<Pedido> findByDomiciliarioId(@Param("idDomiciliario") Long idDomiciliario);
}
