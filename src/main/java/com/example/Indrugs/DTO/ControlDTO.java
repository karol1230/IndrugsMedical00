package com.example.Indrugs.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ControlDTO {

    private Long idControl;
    private Long idMedicamento;
    private String nombreMedicamento;
    private Long idUsuario;
    private String nombreUsuario;
    private String cantidadMedic;
    private String problemaSalud;
    private String frecuenciaMedic;


    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fechaInicioTratamiento;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fechaFinTratamiento;
    private LocalTime alarmaControl;
    private String estado;


    }


