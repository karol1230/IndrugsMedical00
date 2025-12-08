package com.example.Indrugs.DTO;

import java.time.LocalDateTime;

public class PedidoDTO {

    private Long id;
    private String nombrePaciente;
    private String direccion;
    private LocalDateTime horaPedido;
    private String telefono;
    private String observaciones;
    private String estado;
    private String usuario; // nombre del domiciliario

    public PedidoDTO() {}

    public PedidoDTO(Long id, String nombrePaciente, String direccion, LocalDateTime horaPedido,
                     String telefono, String observaciones, String estado, String usuario) {
        this.id = id;
        this.nombrePaciente = nombrePaciente;
        this.direccion = direccion;
        this.horaPedido = horaPedido;
        this.telefono = telefono;
        this.observaciones = observaciones;
        this.estado = estado;
        this.usuario = usuario;
    }

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public LocalDateTime getHoraPedido() { return horaPedido; }
    public void setHoraPedido(LocalDateTime horaPedido) { this.horaPedido = horaPedido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
