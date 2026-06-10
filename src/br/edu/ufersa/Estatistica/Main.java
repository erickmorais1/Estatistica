package br.edu.ufersa.Estatistica;

import java.util.ArrayList;

/**
 * Ponto de entrada — Fase 2 completa.
 *
 * Executa em sequencia:
 *   1. Validacao da Fase 1
 *   2. Simulacao de 100 ICs e resumo
 *   3. Grafico de cobertura (janela Swing)
 *   4. Relatorio de experimentos
 *   5. Testes de robustez
 */
public class Main {

    private static final double SIGMA       = PopulacaoTeste.SIGMA;
    private static final double Z_95        = 1.96;
    private static final double MARGEM_ERRO = 0.5;
    private static final int    N_SIM       = 100;

    public static void main(String[] args) {

        // Gera populacao de teste (N=1000, sigma2=25)
        ArrayList<Double> populacao = PopulacaoTeste.gerar();
        EstatisticaDescritiva est = new EstatisticaDescritiva();
        double mu = est.media(populacao);

        System.out.println("=================================================");
        System.out.println("  FASE 2 - Simulacao, Validacao e Analise Grafica");
        System.out.println("  Grupo 2 - Amostragem (7.3.4 a 7.3.7)");
        System.out.println("=================================================");
        System.out.printf("  N=%d  |  mu=%.4f  |  sigma=%.1f  |  sigma2=%.1f%n%n",
                populacao.size(), mu, SIGMA, PopulacaoTeste.SIGMA_QUADRADO);

        // Tamanho amostral pelo enunciado
        int n = ValidacaoGrupo2.calcularTamanhoAmostral(Z_95, SIGMA, MARGEM_ERRO);
        System.out.printf("  n = ceil((z*sigma/e)^2) = ceil((%.2f*%.1f/%.1f)^2) = %d%n%n",
                Z_95, SIGMA, MARGEM_ERRO, n);

        // ── 1. Validacao da Fase 1 ─────────────────────────────────────────
        System.out.println(">>> [Fase 1] ValidacaoGrupo2...\n");
        ValidacaoGrupo2.main(new String[]{});

        // ── 2. Simulacao dos 100 ICs ───────────────────────────────────────
        System.out.println("\n=================================================");
        System.out.println("  SIMULACAO: 100 INTERVALOS DE CONFIANCA (95%)");
        System.out.println("=================================================");
        System.out.printf("  n=%d  |  sigma=%.1f  |  z=%.2f  |  e=%.1f%n%n",
                n, SIGMA, Z_95, MARGEM_ERRO);

        SimulacaoIntervalosConfianca simulacao = new SimulacaoIntervalosConfianca();
        ArrayList<SimulacaoIntervalosConfianca.ResultadoIC> resultados =
                simulacao.simular(populacao, n, SIGMA, Z_95, N_SIM);

        simulacao.exibirResumo(resultados, mu, n, Z_95, SIGMA);

        // Imprime primeiros 10 ICs
        System.out.println("\n  Primeiros 10 intervalos:");
        System.out.printf("  %-5s  %-12s  %-12s  %-12s  %s%n",
                "Sim.", "LI", "LS", "xBar", "Contem mu?");
        System.out.println("  " + sep(56));  // <-- CORRIGIDO
        for (int i = 0; i < 10; i++) {
            SimulacaoIntervalosConfianca.ResultadoIC r = resultados.get(i);
            System.out.printf("  %-5d  %-12.4f  %-12.4f  %-12.4f  %s%n",
                    r.simulacao, r.limiteInferior, r.limiteSuperior,
                    r.mediaAmostral, r.contemMedia ? "SIM" : "NAO");
        }
        System.out.println("  ...\n");

        // ── 3. Grafico de cobertura ────────────────────────────────────────
        System.out.println(">>> Abrindo grafico de cobertura...");
        GraficoCobertura.exibir(resultados, mu, "Analise Grafica - 100 ICs (Grupo 2)");

        // ── 4. Relatorio de experimentos ───────────────────────────────────
        RelatorioExperimentos relatorio = new RelatorioExperimentos();
        relatorio.executar(populacao);

        // ── 5. Testes de robustez ──────────────────────────────────────────
        relatorio.testeRobustez(populacao);

        System.out.println("=================================================");
        System.out.println("  Fase 2 concluida com sucesso.");
        System.out.println("=================================================");
    }

    /**
     * Alternativa ao String.repeat() do Java 11.
     * Compativel com Java 8+.
     */
    private static String sep(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append('-');
        return sb.toString();
    }
}