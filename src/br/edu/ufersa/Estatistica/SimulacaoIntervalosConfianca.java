package br.edu.ufersa.Estatistica;

import java.util.ArrayList;

/**
 * Fase 2 — Simula 100 intervalos de confiança e mede a taxa de cobertura.
 *
 * Fórmula do IC (σ conhecido):
 *   IC = [ x̄ - z*(σ/√n)  ;  x̄ + z*(σ/√n) ]
 *
 * A taxa de cobertura deve convergir para o nível de confiança (≈95%).
 */

public class SimulacaoIntervalosConfianca {
    private EstatisticaDescritiva est;

    public SimulacaoIntervalosConfianca() {
        this.est = new EstatisticaDescritiva();
    
}

// ── Resultado de um único IC ──────────────────────────────────────────────

    public static class ResultadoIC {
        public final int    simulacao;
        public final double mediaAmostral;
        public final double limiteInferior;
        public final double limiteSuperior;
        public final double amplitude;
        public final boolean contemMedia;

        public ResultadoIC(int sim, double xBar, double li, double ls, double mu) {
            this.simulacao      = sim;
            this.mediaAmostral  = xBar;
            this.limiteInferior = li;
            this.limiteSuperior = ls;
            this.amplitude      = ls - li;
            this.contemMedia    = (mu >= li && mu <= ls);
        }
    }

    // ── Simulação principal ───────────────────────────────────────────────────

    /**
     * Gera numSimulacoes amostras AAS e calcula o IC para cada uma.
     *
     * @param populacao     população completa (N=1000)
     * @param n             tamanho de cada amostra
     * @param sigma         desvio padrão populacional conhecido (σ=5)
     * @param z             valor crítico z (1.96 para 95%)
     * @param numSimulacoes número de ICs a gerar (100 pelo enunciado)
     */
    public ArrayList<ResultadoIC> simular(
            ArrayList<Double> populacao, int n, double sigma,
            double z, int numSimulacoes) {

        if (populacao == null || populacao.isEmpty())
            throw new IllegalArgumentException("População não pode ser nula ou vazia");
        if (n <= 0 || n > populacao.size())
            throw new IllegalArgumentException("n deve ser positivo e não maior que N");
        if (sigma <= 0)
            throw new IllegalArgumentException("σ deve ser positivo");
        if (numSimulacoes <= 0)
            throw new IllegalArgumentException("numSimulacoes deve ser positivo");

        double mu     = est.media(populacao);
        double margem = z * (sigma / Math.sqrt(n));   // z * (σ/√n)

        ArrayList<ResultadoIC> resultados = new ArrayList<>();

        for (int i = 0; i < numSimulacoes; i++) {
            // semente diferente a cada iteração → amostras independentes
            ExerciciosSerie1 ex = new ExerciciosSerie1(i * 137L + 29L);
            ArrayList<Double> amostra = ex.amostraAleatoriaSimplesSeR(populacao, n);

            double xBar = est.media(amostra);
            resultados.add(new ResultadoIC(i + 1, xBar, xBar - margem, xBar + margem, mu));
        }

        return resultados;
    }

    // ── Métricas ──────────────────────────────────────────────────────────────

    /** Proporção de ICs que contêm µ (deve ser ≈ 0,95). */
    public double calcularTaxaCobertura(ArrayList<ResultadoIC> resultados) {
        if (resultados == null || resultados.isEmpty()) return 0;
        int contem = 0;
        for (ResultadoIC r : resultados) if (r.contemMedia) contem++;
        return (double) contem / resultados.size();
    }

    /** Imprime o resumo da simulação no console. */
    public void exibirResumo(ArrayList<ResultadoIC> resultados,
                              double mu, int n, double z, double sigma) {
        double taxa      = calcularTaxaCobertura(resultados);
        int    contem    = (int)(taxa * resultados.size());
        double amplitude = 2 * z * sigma / Math.sqrt(n);

        System.out.println("  ── Resultados da Simulação ──");
        System.out.printf("    Total simulado         : %d ICs%n",       resultados.size());
        System.out.printf("    ICs que contêm µ       : %d%n",            contem);
        System.out.printf("    ICs que NÃO contêm µ   : %d%n",            resultados.size() - contem);
        System.out.printf("    Taxa de cobertura      : %.1f%%  (esperado ≈ 95,0%%)%n", taxa * 100);
        System.out.printf("    Amplitude de cada IC   : %.4f%n",          amplitude);
        System.out.printf("    µ real (média popul.)  : %.4f%n",          mu);
    }
}
