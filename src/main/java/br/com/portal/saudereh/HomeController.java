
package br.com.portal.saudereh;

import br.com.portal.saudereh.model.Categoria;
import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.service.AutenticacaoService;
import br.com.portal.saudereh.service.CategoriaService;
import br.com.portal.saudereh.service.SolicitacaoService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {

    private static final String COLABORADOR = "COLABORADOR";
    private static final String ADM_PLANO_SAUDE = "ADM_PLANO_SAUDE";
    private static final String RH_GESTOR = "RH_GESTOR";

    private final SolicitacaoService solicitacaoService;
    private final AutenticacaoService autenticacaoService;
    private final CategoriaService categoriaService;

    public HomeController(
            SolicitacaoService solicitacaoService,
            AutenticacaoService autenticacaoService,
            CategoriaService categoriaService) {

        this.solicitacaoService = solicitacaoService;
        this.autenticacaoService = autenticacaoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String realizarLogin(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session,
            Model model) {

        Usuario usuario = autenticacaoService.autenticar(email, senha);

        if (usuario == null) {
            model.addAttribute("erro", "E-mail ou senha inválidos.");
            return "login";
        }

        session.setAttribute("usuario", usuario);

        return "redirect:/portal";
    }

    @GetMapping("/portal")
    public String portal(HttpSession session, Model model) {
        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "portal";
    }

    @GetMapping("/nova-solicitacao")
    public String novaSolicitacao(
            HttpSession session,
            Model model) {

        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "nova-solicitacao";
    }

    @PostMapping("/solicitacao/salvar")
    public String salvarSolicitacao(
            @RequestParam String categoria,
            @RequestParam String descricao,
            @RequestParam(required = false) MultipartFile arquivo,
            Model model,
            HttpSession session) {

        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Solicitacao solicitacao =
                    solicitacaoService.criarSolicitacao(
                            usuario,
                            categoria,
                            descricao
                    );

            if (arquivo != null && !arquivo.isEmpty()) {
                System.out.println(
                        "Arquivo recebido: "
                                + arquivo.getOriginalFilename()
                );
            }

            model.addAttribute("usuario", usuario);
            model.addAttribute("solicitacao", solicitacao);

            return "sucesso";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "nova-solicitacao";
        }
    }

    // Consulta e administração de categorias: perfis administrativos.
    @GetMapping("/categorias")
    public String listarCategorias(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!podeAdministrar(usuario)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Você não tem permissão para administrar categorias."
            );
            return "redirect:/portal";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("categoria", new Categoria());

        return "categorias";
    }

    @PostMapping("/categorias/salvar")
    public String salvarCategoria(
            @ModelAttribute Categoria categoria,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!podeAdministrar(usuario)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Você não tem permissão para administrar categorias."
            );
            return "redirect:/portal";
        }

        try {
            categoriaService.salvar(categoria);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Categoria salva com sucesso!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/categorias";
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarCategoria(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!podeAdministrar(usuario)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Você não tem permissão para administrar categorias."
            );
            return "redirect:/portal";
        }

        try {
            model.addAttribute("usuario", usuario);
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("categoria", categoriaService.buscarPorId(id));
            return "categorias";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/categorias";
        }
    }

    @PostMapping("/categorias/desativar/{id}")
    public String desativarCategoria(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!podeAdministrar(usuario)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Você não tem permissão para administrar categorias."
            );
            return "redirect:/portal";
        }

        try {
            categoriaService.desativar(id);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Categoria desativada com sucesso!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/categorias";
    }

    // Colaboradores consultam somente suas solicitações.
    // Perfis administrativos podem consultar todas.
    @GetMapping("/solicitacoes")
    public String listarSolicitacoes(
            @RequestParam(required = false) String protocolo,
            @RequestParam(required = false) String status,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        boolean administrativo = podeAdministrar(usuario);
        List<Solicitacao> solicitacoes;

        if (protocolo != null && !protocolo.isBlank()) {
            try {
                Solicitacao solicitacao;

                if (administrativo) {
                    solicitacao =
                            solicitacaoService.buscarPorProtocolo(protocolo);
                } else {
                    solicitacao =
                            solicitacaoService.buscarPorProtocoloDoUsuario(
                                    protocolo,
                                    usuario.getId()
                            );
                }

                solicitacoes = List.of(solicitacao);

            } catch (IllegalArgumentException e) {
                solicitacoes = List.of();
                model.addAttribute("erro", e.getMessage());
            }

        } else if (administrativo) {
            solicitacoes = solicitacaoService.listarPorStatus(status);

        } else {
            solicitacoes =
                    solicitacaoService.listarSolicitacoesDoUsuarioPorStatus(
                            usuario.getId(),
                            status
                    );
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("solicitacoes", solicitacoes);
        model.addAttribute("protocolo", protocolo);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("perfilAdministrativo", administrativo);

        return "solicitacoes";
    }

    // Somente perfis administrativos podem atualizar status.
    @PostMapping("/solicitacoes/{id}/status")
    public String atualizarStatusSolicitacao(
            @PathVariable Long id,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioLogado(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!podeAdministrar(usuario)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Você não tem permissão para atualizar solicitações."
            );
            return "redirect:/solicitacoes";
        }

        try {
            solicitacaoService.atualizarStatus(id, status, usuario);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Status da solicitação atualizado com sucesso!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/solicitacoes";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private Usuario usuarioLogado(HttpSession session) {
        return (Usuario) session.getAttribute("usuario");
    }

    private boolean podeAdministrar(Usuario usuario) {
        if (usuario == null || usuario.getPerfil() == null) {
            return false;
        }

        String perfil = usuario.getPerfil().trim().toUpperCase();

        return ADM_PLANO_SAUDE.equals(perfil)
                || RH_GESTOR.equals(perfil);
    }
}
