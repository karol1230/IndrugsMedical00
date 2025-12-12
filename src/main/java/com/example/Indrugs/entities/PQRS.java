package com.example.Indrugs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pqrs")
public class PQRS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PQRS")
    private Long idPqrs;

    @Column(name = "TIPO_SOLICITUD", nullable = false)
    private String tipoPqrs;

    @Column(name = "motivo_pqrs")
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIOS", nullable = false)
    private Usuario usuario;

    @Column(name = "FECHA_PQRS", nullable = false)
    private LocalDateTime fechaPqrs;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "respuesta", columnDefinition = "text")
    private String respuesta;

}
