package com.example.Indrugs.repositorios;


import com.example.Indrugs.entities.PQRS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PQRSRepository extends JpaRepository<PQRS, Long> {

    List<PQRS> findByUsuarioIdUsuario(Long idUsuario);
}