package br.com.portal.saudereh;

import br.com.portal.saudereh.model.Usuario;
import br.com.portal.saudereh.service.AssistenteIAService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AssistenteIAController {

    private static final String HISTORICO = "historicoAssistente";
    private static final int LIMITE_MENSAGENS = 20;

    private final AssistenteIAService assistenteIAService;

    public AssistenteIAController(AssistenteIAService assistenteIAService) {
        this.assistenteIAService = assistenteIAService;
    }

    private boolean colaboradorAutenticado(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        return usuario != null
                && usuario.getPerfil() != null
                && "COLABORADOR".equalsIgnoreCase(usuario.getPerfil().trim());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> obterHistorico(HttpSession session) {

        Object historico = session.getAttribute(HISTORICO);

        if (historico == null) {
            List<Map<String, String>> novoHistorico = new ArrayList<>();
            session.setAttribute(HISTORICO, novoHistorico);
            return novoHistorico;
        }

        return (List<Map<String, String>>) historico;
    }

    private void adicionarMensagem(
            List<Map<String, String>> historico,
            String papel,
            String conteudo) {

        Map<String, String> mensagem = new LinkedHashMap<>();

        mensagem.put("role", papel);
        mensagem.put("content", conteudo);

        historico.add(mensagem);

        while (historico.size() > LIMITE_MENSAGENS) {
            historico.remove(0);
        }
    }

    @GetMapping("/assistente")
    public String exibirAssistente(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!colaboradorAutenticado(session)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Acesso permitido somente aos colaboradores."
            );
            return "redirect:/portal";
        }

        model.addAttribute("historico", obterHistorico(session));

        return "assistente";
    }

    @PostMapping("/assistente/perguntar")
    public String perguntar(
            @RequestParam("pergunta") String pergunta,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!colaboradorAutenticado(session)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Acesso permitido somente aos colaboradores."
            );
            return "redirect:/portal";
        }

        List<Map<String, String>> historico = obterHistorico(session);

        try {
            String resposta = assistenteIAService.responder(
                    pergunta,
                    historico
            );

            adicionarMensagem(historico, "user", pergunta);
            adicionarMensagem(historico, "assistant", resposta);

            session.setAttribute(HISTORICO, historico);

            model.addAttribute("historico", historico);

            return "assistente";

        } catch (IllegalArgumentException e) {

            model.addAttribute("historico", historico);
            model.addAttribute("pergunta", pergunta);
            model.addAttribute("erro", e.getMessage());

            return "assistente";

        } catch (Exception e) {

            model.addAttribute("historico", historico);
            model.addAttribute("pergunta", pergunta);
            model.addAttribute(
                    "erro",
                    "O assistente está temporariamente indisponível. Tente novamente."
            );

            return "assistente";
        }
    }

    @PostMapping("/assistente/limpar")
    public String limparConversa(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!colaboradorAutenticado(session)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Acesso permitido somente aos colaboradores."
            );
            return "redirect:/portal";
        }

        session.removeAttribute(HISTORICO);

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Conversa iniciada com sucesso!"
        );

        return "redirect:/assistente";
    }
}