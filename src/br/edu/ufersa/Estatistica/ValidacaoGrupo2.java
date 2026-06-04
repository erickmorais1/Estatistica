package br.edu.ufersa.Estatistica;

import java.util.ArrayList;

/**
 * Executa os cenários de validação padronizados do enunciado (Seção 4) e
 * os testes de robustez para os algoritmos do Grupo 2.
 *
 * Parâmetros de entrada definidos pelo enunciado:
 *   - Margem de erro tolerável: e = 0,5
 *   - Nível de confiança: 95%
 *   - Variância populacional conhecida: σ² = 25 (σ = 5)
 *
 */
public class ValidacaoGrupo2 {

    // parâmetros do enunciado
    private static final double MARGEM_ERRO    = 0.5;
    private static final double CONFIANCA      = 0.95;
    private static final double Z_95           = 1.96;  // z para 95% de confiança
    private static final double SIGMA          = PopulacaoTeste.SIGMA;
    private static final double SIGMA_QUADRADO = PopulacaoTeste.SIGMA_QUADRADO;

    public static void main(String[] args) {

        // gera a população de teste (N=1000, σ²=25)
        ArrayList<Double> populacao = PopulacaoTeste.gerar();

        EstatisticaDescritiva est = new EstatisticaDescritiva();
        AmostragemConglomerados ac = new AmostragemConglomerados();
        AmostragemNaoProbabilistica anp = new AmostragemNaoProbabilistica();

        System.out.println("=================================================");
        System.out.println("         VALIDAÇÃO — GRUPO 2");
        System.out.println("         Amostragem: Tópicos 7.3.4 a 7.3.7");
        System.out.println("=================================================");
        System.out.printf("  N = %d indivíduos%n", populacao.size());
        System.out.printf("  σ² = %.1f  (σ = %.1f)%n", SIGMA_QUADRADO, SIGMA);
        System.out.printf("  Média populacional = %.4f%n", est.media(populacao));
        System.out.printf("  Desvio padrão amostral = %.4f%n", est.desvioPadrao(populacao));
        System.out.println();

        // dimensionamento do tamanho amostral pelo enunciado:
        // n = (z * σ / e)² = (1,96 * 5 / 0,5)² = (19,6)² = 384,16 → 385
        int n = calcularTamanhoAmostral(Z_95, SIGMA, MARGEM_ERRO);
        System.out.printf("  Tamanho amostral calculado (e=0,5 ; 95%%): n = %d%n", n);
        System.out.println();

        // ─── 7.3.4 CONGLOMERADOS ───────────────────────────────────────────
        System.out.println("-------------------------------------------------");
        System.out.println("  7.3.4 — Amostragem por Conglomerados");
        System.out.println("-------------------------------------------------");

        // divide em 20 conglomerados de 50 elementos cada, sorteia 8
        // 8 conglomerados × 50 elementos = 400 ≈ n calculado
        ArrayList<ArrayList<Double>> conglomerados =
            ac.dividirEmConglomerados(populacao, 20);
        ArrayList<Double> amostraConglomerados = ac.amostrar(conglomerados, 8);

        System.out.printf("  Conglomerados criados: 20 (≈50 elementos cada)%n");
        System.out.printf("  Conglomerados sorteados: 8%n");
        exibirEstatisticas("  Amostra por Conglomerados", amostraConglomerados, est);
        System.out.println();

        // ─── 7.3.5 ACIDENTAL ───────────────────────────────────────────────
        System.out.println("-------------------------------------------------");
        System.out.println("  7.3.5 — Amostragem Acidental");
        System.out.println("-------------------------------------------------");

        ArrayList<Double> amostraAcidental = anp.acidental(populacao, n);
        exibirEstatisticas("  Amostra Acidental", amostraAcidental, est);
        System.out.println();

        // ─── 7.3.6 INTENCIONAL ─────────────────────────────────────────────
        System.out.println("-------------------------------------------------");
        System.out.println("  7.3.6 — Amostragem Intencional");
        System.out.println("  Critério: idades entre 30 e 50 anos");
        System.out.println("-------------------------------------------------");

        ArrayList<Double> amostraIntencional = anp.intencional(populacao, 30.0, 50.0);
        exibirEstatisticas("  Amostra Intencional [30,50]", amostraIntencional, est);
        System.out.println();

        // ─── 7.3.7 POR QUOTAS ──────────────────────────────────────────────
        System.out.println("-------------------------------------------------");
        System.out.println("  7.3.7 — Amostragem por Quotas");
        System.out.println("  Critério: faixas etárias proporcionais");
        System.out.println("-------------------------------------------------");

        double[][] faixas = PopulacaoTeste.faixasEtarias();
        String[] rotulos  = PopulacaoTeste.rotulosFaixas();
        ArrayList<Double> amostraQuotas = anp.porQuotas(populacao, faixas, rotulos, n);

        exibirEstatisticas("  Amostra por Quotas", amostraQuotas, est);
        System.out.println();

        // ─── TESTES DE ROBUSTEZ ────────────────────────────────────────────
        System.out.println("-------------------------------------------------");
        System.out.println("  TESTES DE ROBUSTEZ — entradas inválidas");
        System.out.println("-------------------------------------------------");

        // população nula
        try {
            anp.acidental(null, 10);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] População nula: " + e.getMessage());
        }

        // n maior que a população
        try {
            anp.acidental(populacao, populacao.size() + 1);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] n > N: " + e.getMessage());
        }

        // critério intencional impossível (min > max)
        try {
            anp.intencional(populacao, 60.0, 20.0);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Critério inválido (min > max): " + e.getMessage());
        }

        // conglomerado único
        try {
            ac.dividirEmConglomerados(populacao, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Menos de 2 conglomerados: " + e.getMessage());
        }

        // sortear mais conglomerados do que existem
        try {
            ac.amostrar(conglomerados, conglomerados.size() + 1);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Sortear mais que o total: " + e.getMessage());
        }

        // n = 0
        try {
            anp.porQuotas(populacao, faixas, rotulos, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] n = 0: " + e.getMessage());
        }

        System.out.println();
        System.out.println("  Validação concluída sem travamentos.");
        System.out.println("=================================================");
    }

    /**
     * Calcula o tamanho amostral mínimo para estimar a média com margem
     * de erro e e nível de confiança definido pelo z crítico.
     *
     * Fórmula: n = (z * σ / e)²
     *
     * @param z     valor crítico Z para o nível de confiança desejado
     * @param sigma desvio padrão populacional σ
     * @param e     margem de erro tolerável
     * @return tamanho amostral mínimo (arredondado para cima)
     */
    public static int calcularTamanhoAmostral(double z, double sigma, double e) {
        double nExato = Math.pow((z * sigma) / e, 2);
        // arredonda para cima: qualquer fração exige mais uma unidade
        return (int) Math.ceil(nExato);
    }

    /**
     * Exibe no console as estatísticas descritivas da amostra produzida.
     */
    private static void exibirEstatisticas(
            String label, ArrayList<Double> amostra, EstatisticaDescritiva est) {

        System.out.printf("  %s:%n", label);
        System.out.printf("    n = %d%n", amostra.size());

        if (amostra.size() >= 1) {
            System.out.printf("    Média = %.4f%n", est.media(amostra));
        }
        if (amostra.size() >= 2) {
            System.out.printf("    Desvio padrão amostral = %.4f%n", est.desvioPadrao(amostra));
            System.out.printf("    Variância amostral = %.4f%n", est.varianciaAmostral(amostra));
        }
    }
}
