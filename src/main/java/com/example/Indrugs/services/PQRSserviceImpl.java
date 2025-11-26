package com.example.Indrugs.services;

import com.example.Indrugs.DTO.PQRSDTO;
import com.example.Indrugs.entities.PQRS;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.PQRSMapper;
import com.example.Indrugs.repositorios.PQRSRepository;
import com.example.Indrugs.repositorios.UsuarioRepository;
import com.example.Indrugs.services.PQRSservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PQRSserviceImpl implements PQRSservice {

    private final PQRSRepository pqrsRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PQRSserviceImpl(PQRSRepository pqrsRepository, UsuarioRepository usuarioRepository) {
        this.pqrsRepository = pqrsRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void crear(PQRSDTO dto, Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getFechaPqrs() == null) {
            dto.setFechaPqrs(LocalDateTime.now());
        }

        // Estado inicial
        dto.setEstado("Pendiente");

        PQRS pqrs = PQRSMapper.toEntity(dto, usuario);

        pqrsRepository.save(pqrs);
    }


    @Override
    public PQRSDTO obtenerPorId(Long id) {
        return pqrsRepository.findById(id)
                .map(PQRSMapper::toDTO)
                .orElse(null);
    }


    @Override
    public void eliminar(Long id) {

    }

    @Override
    public List<PQRSDTO> listarPorUsuario(Long idUsuario) {
        return pqrsRepository.findByUsuarioIdUsuario(idUsuario)
                .stream()
                .map(PQRSMapper::toDTO)
                .toList();
    }

    @Override
    public void responder(Long id, String respuesta) {
        PQRS pqrs = pqrsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrada"));

        pqrs.setRespuesta(respuesta);
        pqrs.setEstado("Respondida");
        pqrs.setFechaRespuesta(LocalDateTime.now());

        pqrsRepository.save(pqrs);
    }

    @Override
    public void cambiarEstado(Long id, String estado) {
        PQRS pqrs = pqrsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrada"));

        pqrs.setEstado(estado);
        pqrsRepository.save(pqrs);
    }

    @Override
    public Object listarTodas() {
        return null;
    }

    @Override
    public List<PQRSDTO> listarTodo() {
        return pqrsRepository.findAll()
                .stream()
                .map(p -> {
                    PQRSDTO dto = new PQRSDTO();

                    dto.setIdPqrs(p.getIdPqrs());
                    dto.setTipoPqrs(p.getTipoPqrs());
                    dto.setMotivo(p.getMotivo());
                    dto.setFechaPqrs(p.getFechaPqrs());

                    // Usuario
                    if (p.getUsuario() != null) {
                        dto.setUsuarioid(p.getUsuario().getIdUsuario());
                        dto.setNombreUsuario(p.getUsuario().getNombre());
                    } else {
                        dto.setNombreUsuario("Desconocido");
                    }

                    // Estado y respuesta
                    dto.setEstado(p.getEstado());
                    dto.setRespuesta(p.getRespuesta());

                    return dto;
                })
                .toList();
    }


    public void responderPQRS(Long id, String respuesta, String estado) {
        PQRS pqrs = pqrsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrada"));

        pqrs.setRespuesta(respuesta);
        pqrs.setEstado(estado);

        pqrsRepository.save(pqrs);
    }

    @Override
    public PQRS buscarPorId(Long id) {
        return pqrsRepository.findById(id).orElse(null);
    }

    @Override
    public void guardar(PQRS pqrs) {
        pqrsRepository.save(pqrs); // Ahora sí guarda los cambios
    }

    @Override
    public PQRS obtenerPqrsPorId(Long id) {
        return pqrsRepository.findById(id).orElse(null);
    }



}
