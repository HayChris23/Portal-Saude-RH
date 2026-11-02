package br.com.portal.saudereh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class AssistenteIAService {

    private static final String API_URL =
            "https://api.openai.com/v1/responses";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AssistenteIAService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String responder(String pergunta) {

        String chave = System.getenv("OPENAI_API_KEY");

        if (chave == null || chave.isBlank()) {
            throw new IllegalStateException(
                    "A configuração do assistente de IA não está disponível.");
        }

        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException(
                    "Digite uma pergunta para o assistente.");
        }

        if (pergunta.length() > 2000) {
            throw new IllegalArgumentException(
                    "A pergunta deve ter no máximo 2.000 caracteres.");
        }

        try {
            String instrucao =
                    "Você é o assistente virtual do Portal Saúde RH. " +
                    "Responda em português brasileiro, com clareza e educação. " +
                    "Ajude com dúvidas gerais sobre solicitações de benefícios " +
                    "e sobre o funcionamento do portal. " +
                    "Não invente políticas internas, dados de funcionários, " +
                    "prazos ou informações sobre planos de saúde. " +
                    "Quando não souber a resposta, oriente o usuário a procurar o RH.";

            var corpo = objectMapper.createObjectNode();
            corpo.put("model", "gpt-4.1-mini");
            corpo.put("instructions", instrucao);
            corpo.put("input", pergunta);
            corpo.put("max_output_tokens", 300);

            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + chave)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(corpo)))
                    .build();

            HttpResponse<String> resposta = httpClient.send(
                    requisicao,
                    HttpResponse.BodyHandlers.ofString());

            if (resposta.statusCode() < 200
                    || resposta.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Não foi possível obter uma resposta da IA. " +
                        "Tente novamente mais tarde.");
            }

            JsonNode resultado =
                    objectMapper.readTree(resposta.body());

            for (JsonNode item : resultado.path("output")) {
                if ("message".equals(item.path("type").asText())) {
                    for (JsonNode conteudo : item.path("content")) {
                        if ("output_text".equals(
                                conteudo.path("type").asText())) {
                            String texto = conteudo.path("text").asText();
                            if (!texto.isBlank()) {
                                return texto;
                            }
                        }
                    }
                }
            }

            return "Não consegui formular uma resposta. Tente reformular a pergunta.";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "A solicitação foi interrompida. Tente novamente.");
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro de comunicação com o assistente. Tente novamente.");
        }
    }
}