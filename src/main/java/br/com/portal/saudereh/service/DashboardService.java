package br.com.portal.saudereh.service;

import br.com.portal.saudereh.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
