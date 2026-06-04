package br.edu.ufersa.Estatistica;

import java.util.ArrayList;
import java.util.Random;

/**
 * Gera a população fictícia de teste definida na Seção 4 do enunciado:
 * N = 1.000 indivíduos com idades e variância populacional conhecida σ² = 25.
 *
 * A população é gerada com distribuição Normal de média 35 e desvio padrão 5
 * (σ² = 25), com idades truncadas no intervalo [18, 80] para realismo.
 * A semente (seed) é fixa para garantir reprodutibilidade nos testes.
 */
public class PopulacaoTeste {

    // tamanho populacional definido pelo enunciado
    public static final int N = 1000;

    // desvio padrão populacional σ = 5, logo σ² = 25
    public static final double SIGMA = 5.0;
    public static final double SIGMA_QUADRADO = 25.0;

    // semente fixa para reprodutibilidade
    private static final long SEED = 42L;

    public static ArrayList<Double> gerar() {
        Random random = new Random(SEED);
        ArrayList<Double> populacao = new ArrayList<>();

        // média etária de 35 anos, desvio padrão 5 (σ² = 25)
        double mediaIdade = 35.0;

        while (populacao.size() < N) {
            // geração por Box-Muller para distribuição Normal
            double idade = mediaIdade + SIGMA * random.nextGaussian();

            // trunca para faixa realista de idades [18, 80]
            if (idade >= 18 && idade <= 80) {
                populacao.add(Math.round(idade * 10.0) / 10.0);
            }
        }

        return populacao;
    }

    /**
     * Retorna as faixas etárias padrão usadas pela amostragem por quotas.
     * Cada faixa é representada por { limiteInferior, limiteSuperior }.
     *
     * Faixas:
     *   Jovem:        18 – 29
     *   Adulto:       30 – 44
     *   Meia-idade:   45 – 59
     *   Idoso:        60 – 80
     */
    public static double[][] faixasEtarias() {
        return new double[][]{
            {18, 29},
            {30, 44},
            {45, 59},
            {60, 80}
        };
    }

    /**
     * Retorna os rótulos descritivos das faixas etárias, na mesma ordem
     * que faixasEtarias().
     */
    public static String[] rotulosFaixas() {
        return new String[]{"Jovem (18-29)", "Adulto (30-44)", "Meia-idade (45-59)", "Idoso (60-80)"};
    }
}
