package br.com.portal.saudereh;

import br.com.portal.saudereh.service.AssistenteIAService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AssistenteIAController {

    private final AssistenteIAService assistenteIAService;

    public AssistenteIAController(AssistenteIAService assistenteIAService) {
        this.assistenteIAService = assistenteIAService;
    }

    @GetMapping("/assistente")
    public String exibirAssistente() {
        return "assistente";
    }

    @PostMapping("/assistente/perguntar")
    public String perguntar(
            @RequestParam("pergunta") String pergunta,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            String resposta = assistenteIAService.responder(pergunta);
            model.addAttribute("pergunta", pergunta);
            model.addAttribute("resposta", resposta);

            return "assistente";

        } catch (IllegalArgumentException e) {
            model.addAttribute("pergunta", pergunta);
            model.addAttribute("erro", e.getMessage());

            return "assistente";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "O assistente está temporariamente indisponível. Tente novamente."
            );

            return "redirect:/assistente";
        }
    }
}