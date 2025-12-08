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

    @Query("SELECT p FROM Pedido p WHERE p.domiciliario.idUsuario = :idDomiciliario")
    List<Pedido> findByDomiciliarioId(@Param("idDomiciliario") Long idDomiciliario);

    @Query("SELECT p FROM Pedido p WHERE p.domiciliario = :domiciliario")
    List<Pedido> findByDomiciliario(@Param("domiciliario") Usuario domiciliario);

    long countByEstado(String estado);
}
