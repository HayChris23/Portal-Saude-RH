package br.com.portal.saudereh;

import br.com.portal.saudereh.model.Solicitacao;
import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.service.AutenticacaoService;
import br.com.portal.saudereh.service.SolicitacaoService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {

    private final SolicitacaoService solicitacaoService;
    private final AutenticacaoService autenticacaoService;

    public HomeController(
            SolicitacaoService solicitacaoService,
            AutenticacaoService autenticacaoService) {

        this.solicitacaoService = solicitacaoService;
        this.autenticacaoService = autenticacaoService;
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

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        return "portal";
    }

    @GetMapping("/nova-solicitacao")
    public String novaSolicitacao(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        return "nova-solicitacao";
    }

    @PostMapping("/solicitacao/salvar")
    public String salvarSolicitacao(
            @RequestParam String categoria,
            @RequestParam String descricao,
            @RequestParam(required = false) MultipartFile arquivo,
            Model model,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Solicitacao solicitacao = solicitacaoService.criarSolicitacao(
                categoria,
                descricao
        );

        if (arquivo != null && !arquivo.isEmpty()) {
            System.out.println(
                    "Arquivo recebido: " + arquivo.getOriginalFilename()
            );
        }

        model.addAttribute("solicitacao", solicitacao);

        return "sucesso";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}