package com.example.Indrugs.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_paciente", nullable = false)
    private String nombrePaciente;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "correo_paciente")
    private String correoPaciente; // 🔥 NUEVO

    @Column(name = "hora_pedido")
    private LocalDateTime horaPedido;

    @Column(name = "estado")
    private String estado;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "orden_id")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "usuario_domiciliario", nullable = false)
    private Usuario domiciliario;



    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoPaciente() { return correoPaciente; }  // 🔥
    public void setCorreoPaciente(String correoPaciente) { this.correoPaciente = correoPaciente; } // 🔥

    public LocalDateTime getHoraPedido() { return horaPedido; }
    public void setHoraPedido(LocalDateTime horaPedido) { this.horaPedido = horaPedido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }


    public Usuario getDomiciliario() { return domiciliario; }
    public void setDomiciliario(Usuario domiciliario) { this.domiciliario = domiciliario; }


}
