
package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.repository.UsuarioRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AutenticacaoService {

    private static final int LIMITE_TENTATIVAS = 5;
    private static final int TEMPO_BLOQUEIO_MINUTOS = 15;

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public AutenticacaoResultado autenticar(String email, String senha) {

        Usuario usuario = usuarioRepository
                .findByEmailComBloqueio(email)
                .orElse(null);

        if (usuario == null || !Boolean.TRUE.equals(usuario.getAtivo())) {
            return AutenticacaoResultado.falha(
                    "E-mail ou senha inválidos."
            );
        }

        LocalDateTime agora = LocalDateTime.now();

        // Verifica se a conta está temporariamente bloqueada.
        if (usuario.getBloqueadoAte() != null) {

            if (agora.isBefore(usuario.getBloqueadoAte())) {

                long minutosRestantes =
                        java.time.Duration.between(
                                agora,
                                usuario.getBloqueadoAte()
                        ).toMinutes();

                if (java.time.Duration.between(
                        agora,
                        usuario.getBloqueadoAte()
                ).toSeconds() % 60 > 0) {
                    minutosRestantes++;
                }

                return AutenticacaoResultado.falha(
                        "Sua conta está temporariamente bloqueada. "
                        + "Tente novamente em aproximadamente "
                        + minutosRestantes + " minuto(s)."
                );
            }

            // O período de bloqueio terminou.
            usuario.setBloqueadoAte(null);
            usuario.setTentativasLogin(0);
        }

        // Valida a senha.
        boolean senhaValida =
                senha != null
                && usuario.getSenha() != null
                && passwordEncoder.matches(senha, usuario.getSenha());

        if (!senhaValida) {

            int tentativas = usuario.getTentativasLogin() == null
                    ? 0
                    : usuario.getTentativasLogin();

            tentativas++;

            usuario.setTentativasLogin(tentativas);

            if (tentativas >= LIMITE_TENTATIVAS) {

                usuario.setBloqueadoAte(
                        agora.plusMinutes(TEMPO_BLOQUEIO_MINUTOS)
                );

                usuarioRepository.save(usuario);

                return AutenticacaoResultado.falha(
                        "Você excedeu o limite de 5 tentativas. "
                        + "Sua conta foi bloqueada por 15 minutos."
                );
            }

            usuarioRepository.save(usuario);

            int restantes = LIMITE_TENTATIVAS - tentativas;

            return AutenticacaoResultado.falha(
                    "E-mail ou senha inválidos. Você ainda possui "
                    + restantes + " tentativa(s) antes do bloqueio."
            );
        }

        // Login realizado: zera o contador e remove o bloqueio.
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAte(null);

        usuarioRepository.save(usuario);

        return AutenticacaoResultado.sucesso(usuario);
    }

    public String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }
}
