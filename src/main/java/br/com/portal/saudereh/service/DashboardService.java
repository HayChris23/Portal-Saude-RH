package br.com.portal.saudereh.service;

import br.com.portal.saudereh.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public long getTotalSolicitacoes() {
        return dashboardRepository.contarSolicitacoesAtivas();
    }

    public long getSolicitacoesAbertas() {
        return dashboardRepository.contarPorStatusEspecifico("Aberta");
    }

    public long getSolicitacoesEmAnalise() {
        return dashboardRepository.contarPorStatusEspecifico("Em análise");
    }

    public long getSolicitacoesConcluidas() {
        return dashboardRepository.contarPorStatusEspecifico("Concluída");
    }

    public long getSolicitacoesCanceladas() {
        return dashboardRepository.contarPorStatusEspecifico("Cancelada");
    }

    public long getSolicitacoesPendentes() {
        return dashboardRepository.contarPorStatusEspecifico("Pendente");
    }

    public List<Object[]> getSolicitacoesPorStatus() {
        return dashboardRepository.contarPorStatus();
    }

    public List<Object[]> getSolicitacoesPorCategoria() {
        return dashboardRepository.contarPorCategoria();
    }

    /**
     * Calcula o tempo médio entre a abertura e a primeira conclusão
     * registrada de cada solicitação.
     *
     * Retorna o valor em horas.
     */
    public double getTempoMedioAtendimentoHoras() {
        List<Object[]> dados = dashboardRepository.buscarDadosConclusao();

        long totalMinutos = 0;
        long quantidade = 0;

        for (Object[] linha : dados) {
            LocalDateTime dataAbertura = (LocalDateTime) linha[1];
            LocalDateTime dataConclusao = (LocalDateTime) linha[2];

            if (dataAbertura == null || dataConclusao == null) {
                continue;
            }

            long minutos = Duration.between(
                    dataAbertura,
                    dataConclusao
            ).toMinutes();

            if (minutos >= 0) {
                totalMinutos += minutos;
                quantidade++;
            }
        }

        if (quantidade == 0) {
            return 0.0;
        }

        return totalMinutos / (double) quantidade / 60.0;
    }

    /**
     * Retorna a quantidade de primeiras conclusões por mês.
     * Cada solicitação é considerada apenas uma vez.
     *
     * Cada item contém: [mês, quantidade].
     */

    public List<Object[]> getConclusoesPorMes() {

    List<Object[]> dados = dashboardRepository.buscarDadosConclusao();

    YearMonth mesAtual = YearMonth.now();
    YearMonth primeiroMes = mesAtual.minusMonths(5);

    Map<YearMonth, Long> conclusoesPorMes = new LinkedHashMap<>();

    // Inicializa os seis meses com zero.
    for (int i = 0; i < 6; i++) {
        YearMonth mes = primeiroMes.plusMonths(i);
        conclusoesPorMes.put(mes, 0L);
    }

    // Conta as primeiras conclusões registradas em cada mês.
    for (Object[] linha : dados) {

        LocalDateTime dataConclusao = (LocalDateTime) linha[2];

        if (dataConclusao == null) {
            continue;
        }

        YearMonth mes = YearMonth.from(dataConclusao);

        if (conclusoesPorMes.containsKey(mes)) {
            conclusoesPorMes.merge(mes, 1L, Long::sum);
        }
    }

    DateTimeFormatter formatador =
            DateTimeFormatter.ofPattern("MM/yyyy");

    List<Object[]> resultado = new ArrayList<>();

    conclusoesPorMes.forEach((mes, quantidade) ->
            resultado.add(new Object[]{
                    mes.format(formatador),
                    quantidade
            })
    );

    return resultado;
}

    public void diagnosticarConclusoes() {
        List<Object[]> dados =
                dashboardRepository.buscarConclusoesParaDiagnostico();

        System.out.println("===== DIAGNÓSTICO DE CONCLUSÕES =====");

        for (Object[] linha : dados) {
            System.out.println(
                    "Solicitação ID: " + linha[0]
                    + " | Status no histórico: " + linha[1]
                    + " | Data da conclusão: " + linha[2]
                    + " | Status atual: " + linha[3]
            );
        }

        System.out.println("Total de registros: " + dados.size());
        System.out.println("=====================================");
    }

}