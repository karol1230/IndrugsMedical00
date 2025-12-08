package com.example.Indrugs.services;

import com.example.Indrugs.DTO.Usuario.UsuarioCreateDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioDTO;
import com.example.Indrugs.DTO.Usuario.UsuarioUpdateDTO;
import com.example.Indrugs.entities.Rol;
import com.example.Indrugs.entities.Usuario;
import com.example.Indrugs.mapper.UsuarioMapper;
import com.example.Indrugs.repositorios.RolRepository;
import com.example.Indrugs.repositorios.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(RolRepository rolRepository,
                              UsuarioRepository usuarioRepository,
                              BCryptPasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioDTO> read() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void crear(UsuarioCreateDTO userCreate) {
        if (usuarioRepository.existsByCorreo(userCreate.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con ese correo");
        }
        if (usuarioRepository.existsByNumDoc(userCreate.getNumDoc())) {
            throw new RuntimeException("Ya existe un usuario con ese número de documento");
        }

        Usuario usuario = UsuarioMapper.mapNewToEntitie(userCreate);
        usuario.setPassword(passwordEncoder.encode(userCreate.getPassword()));

        Rol rol = rolRepository.findById(userCreate.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);

        usuario.setEstado("ACTIVO");
        usuarioRepository.save(usuario);
    }

    @Override
    public void actualizar(Long idUsuario, UsuarioUpdateDTO userUpdate) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioMapper.mapUpdateTo(usuario, userUpdate);
        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    @Override
    public List<UsuarioDTO> findByRol(Long idRol) {
        return usuarioRepository.findByRol_idRol(idRol)
                .stream()
                .map(UsuarioMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDTO> findByStatus(String estado) {
        return usuarioRepository.findByEstado(estado)
                .stream()
                .map(UsuarioMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Usuario autenticar(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no registrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return usuario;
    }

    @Override
    public UsuarioDTO findById(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(UsuarioMapper::mapToDto)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existsByNumDoc(String numDoc) {
        return usuarioRepository.existsByNumDoc(numDoc);
    }

    // ----------- ESTADÍSTICAS -----------

    @Override
    public long countByEstado(String estado) {
        return usuarioRepository.countByEstado(estado);
    }

    @Override
    public long countTotal() {
        return usuarioRepository.count();
    }

    @Override
    public long countByRolNombre(String nombreRol) {
        return usuarioRepository.countByRol_nombreRol(nombreRol);
    }

    @Override
    public long contarUsuariosPorRol(String nombreRol) {
        return usuarioRepository.countByRol_nombreRol(nombreRol);
    }

    @Override
    public long contarUsuariosActivos() {
        return usuarioRepository.countByEstado("ACTIVO");
    }

    @Override
    public Map<String, Long> obtenerResumenUsuarios() {
        Map<String, Long> resumen = new HashMap<>();
        resumen.put("pacientes", countByRolNombre("Paciente"));
        resumen.put("domiciliarios", countByRolNombre("Domiciliario"));
        resumen.put("administradores", countByRolNombre("Administrador"));
        resumen.put("activos", countByEstado("ACTIVO"));
        resumen.put("inactivos", countByEstado("INACTIVO"));
        return resumen;
    }

    @Override
    public List<UsuarioDTO> obtenerUsuariosRecientes() {
        return usuarioRepository.findTop5ByOrderByIdUsuarioDesc()
                .stream()
                .map(UsuarioMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDTO> findByRolNombre(String nombreRol) {
        return usuarioRepository.findByRol_nombreRol(nombreRol)
                .stream()
                .map(UsuarioMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDTO> findByRolNombreAndEstado(String nombreRol, String estado) {
        return usuarioRepository.findByRol_nombreRolAndEstado(nombreRol, estado)
                .stream()
                .map(UsuarioMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> listarPorRol(String nombreRol) {
        return usuarioRepository.findByRol_nombreRol(nombreRol);
    }

    // ----------- 🔥 NUEVO: guardar domiciliario / registro general -----------
    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // ----------- Correos masivos -----------
    @Override
    public List<String> obtenerCorreosActivos() {
        return usuarioRepository.findByEstado("ACTIVO")
                .stream()
                .map(Usuario::getCorreo)
                .collect(Collectors.toList());
    }



}
