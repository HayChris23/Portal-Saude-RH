package br.com.portal.saudereh.service;

import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InicializacaoService implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AutenticacaoService autenticacaoService;

    public InicializacaoService(
            UsuarioRepository usuarioRepository,
            AutenticacaoService autenticacaoService) {

        this.usuarioRepository = usuarioRepository;
        this.autenticacaoService = autenticacaoService;
    }

    @Override
    public void run(String... args) {

        if (usuarioRepository.findByEmail("hayane@findes.org.br").isEmpty()) {

            Usuario usuario = new Usuario();

            usuario.setNome("Hayane");
            usuario.setMatricula("HAYANE");
            usuario.setEmail("hayane@findes.org.br");
            usuario.setSenha(
                    autenticacaoService.criptografarSenha("123456")
            );
            usuario.setPerfil("COLABORADOR");
            usuario.setAtivo(true);

            usuarioRepository.save(usuario);

            System.out.println("======================================");
            System.out.println("USUARIO DE TESTE CRIADO COM SUCESSO");
            System.out.println("E-mail: hayane@findes.org.br");
            System.out.println("======================================");
        }
    }
}