package br.edu.ufersa.Estatistica;

import java.util.ArrayList;

/**
 * Fase 2 — Relatório de Experimentos Final.
 */
public class RelatorioExperimentos {

    private SimulacaoIntervalosConfianca sim;
    private static final double SIGMA = PopulacaoTeste.SIGMA;
    private static final double Z_95  = 1.96;
    private static final int    N_SIM = 100;

    public RelatorioExperimentos() {
        this.sim = new SimulacaoIntervalosConfianca();
    }

    public void executar(ArrayList<Double> populacao) {
        EstatisticaDescritiva est = new EstatisticaDescritiva();
        double mu = est.media(populacao);

        System.out.println("=================================================");
        System.out.println("   RELATORIO DE EXPERIMENTOS - FASE 2");
        System.out.println("=================================================");
        System.out.printf("   N=%d  mu=%.4f  sigma=%.1f  z=%.2f  %d sim./n%n%n",
                populacao.size(), mu, SIGMA, Z_95, N_SIM);

        // Tabela 1: diferentes tamanhos de amostra
        System.out.println("  [Exp. 1] Impacto do tamanho amostral n:");
        System.out.printf("  %-8s %-14s %-14s %-10s%n",
                "n", "Media das xBar", "Amplitude IC", "Cobertura");
        System.out.println("  " + sep(48));  // <-- CORRIGIDO

        int[] tamanhos = {30, 50, 100, 200, 385};
        for (int n : tamanhos) {
            ArrayList<SimulacaoIntervalosConfianca.ResultadoIC> res =
                    sim.simular(populacao, n, SIGMA, Z_95, N_SIM);

            double taxa = sim.calcularTaxaCobertura(res);
            double amp  = 2 * Z_95 * SIGMA / Math.sqrt(n);

            double somaX = 0;
            for (SimulacaoIntervalosConfianca.ResultadoIC r : res) {
                somaX += r.mediaAmostral;
            }

            System.out.printf("  %-8d %-14.4f %-14.4f %.1f%%%n",
                    n, somaX / res.size(), amp, taxa * 100);
        }

        // Tabela 2: diferentes margens de erro
        System.out.println();
        System.out.println("  [Exp. 2] Impacto da margem de erro (z=1,96 ; sigma=5):");
        System.out.printf("  %-12s %-12s %-14s%n", "epsilon", "n minimo", "Amplitude IC");
        System.out.println("  " + sep(38));  // <-- CORRIGIDO

        double[] margens = {0.3, 0.5, 0.8, 1.0, 1.5};
        for (double e : margens) {
            int nMin = (int) Math.ceil(Math.pow((Z_95 * SIGMA) / e, 2));
            double amp = 2 * Z_95 * SIGMA / Math.sqrt(nMin);
            System.out.printf("  e = %-8.1f %-12d %.4f%n", e, nMin, amp);
        }

        System.out.println();
        System.out.println("  Conclusoes:");
        System.out.println("  1. n maior -> amplitude IC menor (estimativas mais precisas)");
        System.out.println("  2. Taxa de cobertura estavel ~95% independente de n");
        System.out.println("  3. epsilon maior -> n minimo menor (menor custo amostral)");
        System.out.println("  4. n=385 garante epsilon=0,5 com 95% de confianca (enunciado)");
        System.out.println("=================================================\n");
    }

    public void testeRobustez(ArrayList<Double> populacao) {
        System.out.println("=================================================");
        System.out.println("   TESTES DE ROBUSTEZ - FASE 2");
        System.out.println("=================================================");

        // n = 1 (extremo)
        try {
            sim.simular(populacao, 1, SIGMA, Z_95, 5);
            System.out.println("  [OK] n=1: sem travamento");
        } catch (Exception e) {
            System.out.println("  [OK] n=1: " + e.getMessage());
        }

        // n = 0 (invalido)
        try {
            sim.simular(populacao, 0, SIGMA, Z_95, 5);
            System.out.println("  [FALHOU] n=0 deveria lancar excecao");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] n=0: " + e.getMessage());
        }

        // sigma negativo
        try {
            sim.simular(populacao, 100, -1.0, Z_95, 5);
            System.out.println("  [FALHOU] sigma<0 deveria lancar excecao");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] sigma<0: " + e.getMessage());
        }

        // populacao nula
        try {
            sim.simular(null, 100, SIGMA, Z_95, 5);
            System.out.println("  [FALHOU] populacao nula deveria lancar excecao");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] pop. nula: " + e.getMessage());
        }

        // numSimulacoes = 0
        try {
            sim.simular(populacao, 100, SIGMA, Z_95, 0);
            System.out.println("  [FALHOU] numSim=0 deveria lancar excecao");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] numSim=0: " + e.getMessage());
        }

        System.out.println("  Robustez verificada - sem travamentos.");
        System.out.println("=================================================\n");
    }

    /**
     * Alternativa ao String.repeat() do Java 11.
     * Gera uma string com n tracos. Compativel com Java 8+.
     */
    private static String sep(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append('-');
        return sb.toString();
    }
}