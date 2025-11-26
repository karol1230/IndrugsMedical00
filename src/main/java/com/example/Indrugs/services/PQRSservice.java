package com.example.Indrugs.services;

import com.example.Indrugs.DTO.PQRSDTO;
import com.example.Indrugs.entities.PQRS;

import java.util.List;

public interface PQRSservice {

    void crear (PQRSDTO pqrsdto,Long idUsuario);
            // accesibilidad (pubic, private,),lo que se devuelve (list,void,dto),nombre del metodo(crear,actualizar),parametro()

    List<PQRSDTO> listarTodo(); // opcional: para listar PQRS enviados

    PQRSDTO obtenerPorId(Long id);

    void eliminar(Long id);

    List<PQRSDTO> listarPorUsuario(Long idUsuario);

    void responder(Long id, String respuesta);

    void cambiarEstado(Long id, String estado);

    Object listarTodas();

    void responderPQRS(Long idPqrs, String respuesta, String estado);

    PQRS buscarPorId(Long id);

    void guardar(PQRS pqrs);

    PQRS obtenerPqrsPorId(Long id);
}
