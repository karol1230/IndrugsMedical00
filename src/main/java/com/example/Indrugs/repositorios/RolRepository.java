package com.example.Indrugs.repositorios;

import com.example.Indrugs.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {

    // Buscar rol por nombre (NECESARIO para registro de domiciliario)
    Optional<Rol> findByNombreRol(String nombreRol);

}
