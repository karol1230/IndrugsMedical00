package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.PQRS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface PQRSRepository extends JpaRepository<PQRS, Long> {

    List<PQRS> findByUsuario_IdUsuario(Long idUsuario);


}

