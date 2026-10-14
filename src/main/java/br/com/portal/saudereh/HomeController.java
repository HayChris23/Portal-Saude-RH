
package br.com.portal.saudereh;

import br.com.portal.saudereh.model.Categoria;
import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.service.AutenticacaoService;
import br.com.portal.saudereh.service.CategoriaService;
import br.com.portal.saudereh.service.SolicitacaoService;
import br.com.portal.saudereh.service.DashboardService;
import br.com.portal.saudereh.service.HistoricoSolicitacaoService;

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
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

import java.util.List;

@Controller
public class HomeController {

    private static final String COLABORADOR = "COLABORADOR";
    private static final String ADM_PLANO_SAUDE = "ADM_PLANO_SAUDE";
    private static final String RH_GESTOR = "RH_GESTOR";

    private final SolicitacaoService solicitacaoService;
    private final AutenticacaoService autenticacaoService;
    private final CategoriaService categoriaService;
    private final DashboardService dashboardService;
    private final HistoricoSolicitacaoService historicoSolicitacaoService;

    public HomeController(
            SolicitacaoService solicitacaoService,
            AutenticacaoService autenticacaoService,
            CategoriaService categoriaService,
            DashboardService dashboardService,
            HistoricoSolicitacaoService historicoSolicitacaoService) {

        this.solicitacaoService = solicitacaoService;
        this.autenticacaoService = autenticacaoService;
        this.categoriaService = categoriaService;
        this.dashboardService = dashboardService;
        this.historicoSolicitacaoService = historicoSolicitacaoService;
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

    // Pesquisa solicitações usando filtros opcionais.
    // Colaboradores consultam somente suas solicitações.
    // Perfis administrativos podem consultar todas.
    @GetMapping("/solicitacoes")
public String listarSolicitacoes(
        @RequestParam(required = false) String protocolo,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Long categoriaId,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataInicial,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataFinal,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes) {

    Usuario usuario = usuarioLogado(session);

    if (usuario == null) {
        return "redirect:/login";
    }

    boolean administrativo = podeAdministrar(usuario);

    // O colaborador só poderá pesquisar as próprias solicitações.
    Long usuarioIdPesquisa = administrativo ? null : usuario.getId();

    List<Solicitacao> solicitacoes;

    try {
        solicitacoes = solicitacaoService.pesquisarAvancado(
                protocolo,
                status,
                categoriaId,
                dataInicial,
                dataFinal,
                usuarioIdPesquisa
        );
        System.out.println("TESTE 1: Pesquisa de solicitações concluída.");

    } catch (IllegalArgumentException e) {
        solicitacoes = List.of();
        model.addAttribute("erro", e.getMessage());
    }

    // Disponibiliza somente categorias ativas no filtro.
    List<Categoria> categoriasAtivas = categoriaService.listarTodas()
            .stream()
            .filter(c -> Boolean.TRUE.equals(c.getAtivo()))
            .toList();
    System.out.println("TESTE 2: Categorias carregadas.");

    model.addAttribute("usuario", usuario);
    model.addAttribute("solicitacoes", solicitacoes);
    model.addAttribute("categorias", categoriasAtivas);
    model.addAttribute("protocolo", protocolo);
    model.addAttribute("statusSelecionado", status);
    model.addAttribute("categoriaSelecionada", categoriaId);
    model.addAttribute("dataInicial", dataInicial);
    model.addAttribute("dataFinal", dataFinal);
    model.addAttribute("perfilAdministrativo", administrativo);
System.out.println("TESTE 3: Preparando a página solicitacoes.");
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


    @GetMapping("/solicitacoes/{id}/historico")
    public String consultarHistorico(
        @PathVariable Long id,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes) {

    Usuario usuario = usuarioLogado(session);

    if (usuario == null) {
        return "redirect:/login";
    }

    try {
        Solicitacao solicitacao = solicitacaoService.buscarPorId(id);

        boolean administrativo = podeAdministrar(usuario);

        boolean donoDaSolicitacao =
                solicitacao.getUsuario() != null
                && solicitacao.getUsuario().getId() != null
                && solicitacao.getUsuario().getId().equals(usuario.getId());

        if (!administrativo && !donoDaSolicitacao) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Você não tem permissão para consultar este histórico."
            );
            return "redirect:/solicitacoes";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("solicitacao", solicitacao);
        model.addAttribute(
                "historicos",
                historicoSolicitacaoService.listarPorSolicitacao(id)
        );

        return "historico-solicitacao";

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("erro", e.getMessage());
        return "redirect:/solicitacoes";
    }
}
        
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");

    if (usuario == null) {
        return "redirect:/login";
    }

    model.addAttribute("totalSolicitacoes",
        dashboardService.getTotalSolicitacoes());

    model.addAttribute("solicitacoesAbertas",
        dashboardService.getSolicitacoesAbertas());

    model.addAttribute("solicitacoesEmAnalise",
        dashboardService.getSolicitacoesEmAnalise()
    );

    model.addAttribute("solicitacoesConcluidas",
        dashboardService.getSolicitacoesConcluidas()
    );

    model.addAttribute("solicitacoesCanceladas",
        dashboardService.getSolicitacoesCanceladas()
    );

    model.addAttribute("solicitacoesPorStatus",
        dashboardService.getSolicitacoesPorStatus()
    );

    model.addAttribute("solicitacoesPorCategoria",
        dashboardService.getSolicitacoesPorCategoria()
    );

    model.addAttribute(
    "solicitacoesPendentes",
    dashboardService.getSolicitacoesPendentes()
    );

System.out.println("========== DASHBOARD ==========");

for (Object[] item : dashboardService.getSolicitacoesPorStatus()) {
    System.out.println(
        "Status: [" + item[0] + "] | Quantidade: [" + item[1] + "]"
    );
}

System.out.println("Total: " + dashboardService.getTotalSolicitacoes());
System.out.println("Abertas: " + dashboardService.getSolicitacoesAbertas());
System.out.println("Em análise: " + dashboardService.getSolicitacoesEmAnalise());
System.out.println("Concluídas: " + dashboardService.getSolicitacoesConcluidas());
System.out.println("Canceladas: " + dashboardService.getSolicitacoesCanceladas());

System.out.println("===============================");

return "dashboard";
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
