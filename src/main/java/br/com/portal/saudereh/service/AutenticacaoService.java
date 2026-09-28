package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario autenticar(String email, String senha) {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElse(null);

        if (usuario == null || !Boolean.TRUE.equals(usuario.getAtivo())) {
            return null;
        }

        boolean senhaValida = passwordEncoder.matches(senha, usuario.getSenha());

        if (!senhaValida) {
        return null;
        }   

        return usuario;
        }

    public String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }
}