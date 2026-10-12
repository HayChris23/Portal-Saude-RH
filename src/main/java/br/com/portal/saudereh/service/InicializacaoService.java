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

        criarUsuarioSeNaoExistir(
                "Hayane",
                "HAYANE",
                "hayane@findes.org.br",
                "123456",
                "COLABORADOR"
        );

        criarUsuarioSeNaoExistir(
                "Administrador do Plano",
                "TESTE_ADM_PLANO",
                "adm.plano.teste@portal.local",
                "Teste@123",
                "ADM_PLANO_SAUDE"
        );

        criarUsuarioSeNaoExistir(
                "Gestor de RH",
                "TESTE_RH_GESTOR",
                "rh.gestor.teste@portal.local",
                "Teste@123",
                "RH_GESTOR"
        );
    }

    private void criarUsuarioSeNaoExistir(
            String nome,
            String matricula,
            String email,
            String senha,
            String perfil) {

        if (usuarioRepository.findByEmail(email).isPresent()
                || usuarioRepository.findByMatricula(matricula).isPresent()) {
            System.out.println("Usuário já cadastrado: " + email);
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setMatricula(matricula);
        usuario.setEmail(email);
        usuario.setSenha(
                autenticacaoService.criptografarSenha(senha)
        );
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        System.out.println("Usuário de teste criado: " + email);
        System.out.println("Perfil: " + perfil);
    }
}
