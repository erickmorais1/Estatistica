package br.edu.ufersa.Estatistica;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Executa computacionalmente os 6 exercícios da Série I do capítulo de
 * Amostragem, usando os dados e parâmetros fornecidos pelo enunciado.
 *
 * Os métodos recebem todos os valores como parâmetro — basta alterar
 * os valores nas chamadas abaixo para resolver qualquer variação do problema.
 *
 */
public class ValidacaoExercicios {

    public static void main(String[] args) {

        ExerciciosSerie1 ex = new ExerciciosSerie1();
        EstatisticaDescritiva est = new EstatisticaDescritiva();

        // ─── EXERCÍCIO 1 ────────────────────────────────────────────────────
        System.out.println("===================================================");
        System.out.println("  EXERCÍCIO 1 — População de rendas (em $1000)");
        System.out.println("===================================================");

        // população fornecida pelo enunciado (rendas em $1000)
        ArrayList<Double> populacao = new ArrayList<>(Arrays.asList(
            29.0,  6.0, 34.0, 12.0, 15.0, 31.0, 34.0, 20.0,  8.0, 30.0,
             8.0, 15.0, 24.0, 22.0, 35.0, 31.0, 25.0, 26.0, 20.0, 10.0,
            30.0,  4.0, 16.0, 21.0, 14.0, 21.0, 16.0, 18.0, 20.0, 12.0,
            31.0, 20.0, 12.0, 18.0, 12.0, 25.0, 26.0, 13.0, 10.0,  5.0,
            13.0, 19.0, 30.0, 17.0, 25.0, 29.0, 25.0, 28.0, 32.0, 15.0,
            10.0, 21.0, 18.0,  7.0, 16.0, 14.0, 11.0, 22.0, 21.0, 36.0,
            32.0, 17.0, 15.0, 13.0,  8.0, 12.0, 23.0, 25.0, 13.0, 21.0,
             5.0, 12.0, 32.0, 21.0, 10.0, 30.0, 30.0, 10.0, 14.0, 17.0,
            34.0, 22.0, 30.0, 48.0, 19.0, 12.0,  8.0,  7.0, 15.0, 20.0,
            26.0, 25.0, 22.0, 30.0, 33.0, 14.0, 17.0, 13.0, 10.0,  9.0
        ));

        System.out.printf("  N = %d elementos na população%n", populacao.size());

        // --- 1a: tamanho amostral ---
        // parâmetros do enunciado: d = $2000 = 2 (em $1000), σ = $7000 = 7, confiança 95,5% → z = 2,0
        double d1  = 2.0;   // margem de erro em $1000
        double sigma1 = 7.0; // desvio padrão em $1000
        double z1  = 2.0;   // z para 95,5% de confiança

        int n1 = ex.calcularTamanhoAmostralMedia(z1, sigma1, d1);
        System.out.println("\n  a) Tamanho amostral:");
        System.out.printf("     z = %.1f  |  σ = $%.0f mil  |  d = $%.0f mil%n", z1, sigma1, d1);
        System.out.printf("     n = ⌈(z·σ/d)²⌉ = ⌈(%.1f × %.1f / %.1f)²⌉ = %d%n", z1, sigma1, d1, n1);

        // garante que n não ultrapasse o tamanho da população
        int nEfetivo1 = Math.min(n1, populacao.size());
        System.out.printf("     n efetivo (limitado a N=%d) = %d%n", populacao.size(), nEfetivo1);

        // --- 1b: amostra aleatória simples ---
        ArrayList<Double> amostra1 = ex.amostraAleatoriaSimplesSeR(populacao, nEfetivo1);
        System.out.printf("%n  b) AAS sem reposição: %d elementos sorteados%n", amostra1.size());

        // --- 1c: agrupamento em classes ---
        double[][] classes = ex.agruparEmClasses(amostra1);
        System.out.println("\n  c) Distribuição de frequências:");
        System.out.println("     Classe                  | Frequência");
        System.out.println("     ------------------------+-----------");
        for (double[] classe : classes) {
            System.out.printf("     [%6.1f  ;  %6.1f )   |    %.0f%n",
                classe[0], classe[1], classe[2]);
        }

        // --- 1d: média amostral ---
        double mediaAmostral1 = est.media(amostra1);
        System.out.printf("%n  d) Média amostral x̄ = %.4f ($%.0f)%n",
            mediaAmostral1, mediaAmostral1 * 1000);

        // --- 1e: desvio padrão amostral ---
        double desvioPadrao1 = est.desvioPadrao(amostra1);
        System.out.printf("%n  e) Desvio padrão amostral s = %.4f ($%.0f)%n",
            desvioPadrao1, desvioPadrao1 * 1000);

        // --- 1f: verificação |µ - x̄| ≤ d ---
        double mediaPop1 = est.media(populacao);
        System.out.printf("%n  f) Média populacional µ = %.4f%n", mediaPop1);
        System.out.print("     ");
        ex.verificarMargemDeErro(mediaPop1, mediaAmostral1, d1);

        // ─── EXERCÍCIO 2 ────────────────────────────────────────────────────
        System.out.println("\n===================================================");
        System.out.println("  EXERCÍCIO 2 — Amostragem sistemática (lista)");
        System.out.println("===================================================");

        // simula uma lista com 200 nomes (substitua por lista real conforme necessário)
        ArrayList<String> listaNomes = gerarListaNomes(200);
        int nSistematico = 50;

        System.out.printf("  Lista com %d nomes  |  Amostra desejada: %d%n",
            listaNomes.size(), nSistematico);

        ArrayList<String> amostraSistematica = ex.amostragemsistematica(listaNomes, nSistematico);

        System.out.printf("  Primeiros 10 selecionados: %s%n",
            amostraSistematica.subList(0, Math.min(10, amostraSistematica.size())));
        System.out.printf("  Total selecionado: %d nomes%n", amostraSistematica.size());

        // ─── EXERCÍCIO 3 ────────────────────────────────────────────────────
        System.out.println("\n===================================================");
        System.out.println("  EXERCÍCIO 3 — Tamanho amostral para proporção");
        System.out.println("  (p desconhecido — caso mais conservador)");
        System.out.println("===================================================");

        // parâmetros: d = 0,05 (5%), confiança 95% → z = 1,96
        double d3 = 0.05;
        double z3 = 1.96;

        int n3 = ex.calcularTamanhoAmostralProporçãoDesconhecida(z3, d3);
        System.out.printf("  z = %.2f  |  d = %.2f  |  p = q = 0,5 (desconhecido)%n", z3, d3);
        System.out.printf("  n = ⌈z² · p · q / d²⌉ = ⌈%.2f² × 0,5 × 0,5 / %.2f²⌉ = %d%n",
            z3, d3, n3);

        // ─── EXERCÍCIO 4 ────────────────────────────────────────────────────
        System.out.println("\n===================================================");
        System.out.println("  EXERCÍCIO 4 — Proporção, população infinita");
        System.out.println("  p̂ = q̂ = 0,5 | d = 0,05 | 1-α = 95,5%");
        System.out.println("===================================================");

        // parâmetros do enunciado: p̂ = q̂ = 0,5, d = 0,05, 95,5% → z = 2,0
        double p4 = 0.5, q4 = 0.5, d4 = 0.05, z4 = 2.0;

        int n4 = ex.calcularTamanhoAmostralProporçãoInfinita(z4, p4, q4, d4);
        System.out.printf("  z = %.1f  |  p = %.1f  |  q = %.1f  |  d = %.2f%n", z4, p4, q4, d4);
        System.out.printf("  n = ⌈z² · p · q / d²⌉ = ⌈%.1f² × %.1f × %.1f / %.2f²⌉ = %d%n",
            z4, p4, q4, d4, n4);

        // ─── EXERCÍCIO 5 ────────────────────────────────────────────────────
        System.out.println("\n===================================================");
        System.out.println("  EXERCÍCIO 5 — Proporção, população finita N=200.000");
        System.out.println("  Comparação com Exercício 4");
        System.out.println("===================================================");

        // mesmos parâmetros do Ex. 4, com N = 200.000
        int N5 = 200000;
        int n5 = ex.calcularTamanhoAmostralProporçãoFinita(z4, p4, q4, d4, N5);

        System.out.printf("  N = %,d  |  z = %.1f  |  p = %.1f  |  q = %.1f  |  d = %.2f%n",
            N5, z4, p4, q4, d4);
        System.out.printf("  n₀ (infinita) = %d%n", n4);
        System.out.printf("  n  (finita)   = ⌈n₀·N / (n₀+N-1)⌉ = %d%n", n5);
        System.out.println();
        ex.compararTamanhosAmostrais(n4, n5);

        // ─── EXERCÍCIO 6 ────────────────────────────────────────────────────
        System.out.println("\n===================================================");
        System.out.println("  EXERCÍCIO 6 — Tamanho amostral para salário médio");
        System.out.println("===================================================");

        // parâmetros definidos pelo pesquisador (alterar conforme necessidade):
        // σ estimado = R$ 1.500, d = R$ 200, confiança 95% → z = 1,96
        double sigmaEstimado6 = 1500.0;
        double d6 = 200.0;
        double z6 = 1.96;

        int n6 = ex.calcularTamanhoAmostralMediaEstimada(z6, sigmaEstimado6, d6);
        System.out.printf("  σ estimado = R$ %.0f  |  d = R$ %.0f  |  z = %.2f%n",
            sigmaEstimado6, d6, z6);
        System.out.printf("  n = ⌈(z·σ/d)²⌉ = ⌈(%.2f × %.0f / %.0f)²⌉ = %d%n",
            z6, sigmaEstimado6, d6, n6);
        System.out.println("  (σ e d devem ser fornecidos em mesma unidade monetária)");

        System.out.println("\n===================================================");
        System.out.println("  Todos os exercícios executados com sucesso.");
        System.out.println("===================================================");
    }

    /**
     * Gera uma lista simulada de nomes para o Exercício 2.
     * Em uso real, substitua por uma lista lida de arquivo ou banco de dados.
     *
     * @param tamanho quantidade de nomes a gerar
     * @return ArrayList<String> com nomes no formato "Nome_001", "Nome_002", ...
     */
    private static ArrayList<String> gerarListaNomes(int tamanho) {
        ArrayList<String> lista = new ArrayList<>();
        for (int i = 1; i <= tamanho; i++) {
            lista.add(String.format("Nome_%03d", i));
        }
        return lista;
    }
}
