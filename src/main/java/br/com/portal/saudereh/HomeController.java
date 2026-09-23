package br.com.portal.saudereh;

import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.service.SolicitacaoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {

    private final SolicitacaoService solicitacaoService;

    public HomeController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/portal")
    public String portal() {
        return "portal";
    }

    @GetMapping("/nova-solicitacao")
    public String novaSolicitacao() {
        return "nova-solicitacao";
    }

    @PostMapping("/solicitacao/salvar")
    public String salvarSolicitacao(
            @RequestParam String categoria,
            @RequestParam String descricao,
            @RequestParam(required = false) MultipartFile arquivo,
            Model model) {

        Solicitacao solicitacao = solicitacaoService.criarSolicitacao(
                categoria,
                descricao
        );

        if (arquivo != null && !arquivo.isEmpty()) {
            System.out.println("Arquivo recebido: " + arquivo.getOriginalFilename());
        }

        model.addAttribute("solicitacao", solicitacao);

        return "sucesso";
    }
}