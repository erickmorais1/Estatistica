package br.edu.ufersa.Estatistica;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Executa os cenários de teste padronizados do enunciado (Seção 4) e os
 * testes de robustez para os algoritmos do Grupo 3.
 *
 * Para rodar: execute o método main desta classe.
 */
public class ValidacaoGrupo3 {

    public static void main(String[] args) {

        // amostra definida no enunciado: X = [12.1, 11.5, 13.2, 12.8, 11.9, 12.4, 13.0, 12.2, 11.7, 12.3]
        ArrayList<Double> amostra = new ArrayList<>(Arrays.asList(
                12.1, 11.5, 13.2, 12.8, 11.9,
                12.4, 13.0, 12.2, 11.7, 12.3
        ));

        IntervalosDeConfianca ic = new IntervalosDeConfianca();
        EstatisticaDescritiva est = new EstatisticaDescritiva();

        System.out.println("===========================================");
        System.out.println("       VALIDAÇÃO — GRUPO 3");
        System.out.println("===========================================");
        System.out.printf("  n = %d observações%n", amostra.size());
        System.out.printf("  Média amostral (x̄)        = %.4f%n", est.media(amostra));
        System.out.printf("  Desvio padrão amostral (s) = %.4f%n", est.desvioPadrao(amostra));
        System.out.printf("  Variância amostral (s²)    = %.4f%n", est.varianciaAmostral(amostra));
        System.out.println();

        // -------------------------------------------------------------------
        // CENÁRIO A — σ = 0.5 conhecida, IC para µ a 95%
        // Referência teórica esperada: [≈11.9001 ; ≈12.5199]
        // -------------------------------------------------------------------
        System.out.println("--- CENÁRIO A — σ conhecida (σ = 0.5) ----");
        double[] resultadoA = ic.icMediaVarianciaConhecida(amostra, 0.5, 0.95);
        ic.exibirResultado(resultadoA, 0.95, "conhecida (σ = 0.5)");
        System.out.println("  Referência: [≈11.9001 ; ≈12.5199]");
        System.out.println();

        // -------------------------------------------------------------------
        // CENÁRIO B — variância desconhecida, IC para µ a 95% (t com gl=9)
        // Referência teórica esperada: [≈11.8205 ; ≈12.5995]
        // -------------------------------------------------------------------
        System.out.println("--- CENÁRIO B — variância desconhecida ----");
        double[] resultadoB = ic.icMediaVarianciaDesconhecida(amostra, 0.95);
        ic.exibirResultado(resultadoB, 0.95, "desconhecida (usa t de Student, gl=9)");
        System.out.println("  Referência: [≈11.8205 ; ≈12.5995]");
        System.out.println();

        // -------------------------------------------------------------------
        // TESTES DE ROBUSTEZ — entradas inválidas não devem travar o programa
        // -------------------------------------------------------------------
        System.out.println("--- TESTES DE ROBUSTEZ --------------------");

        // lista nula
        try {
            ic.icMediaVarianciaConhecida(null, 0.5, 0.95);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Lista nula: " + e.getMessage());
        }

        // sigma negativo
        try {
            ic.icMediaVarianciaConhecida(amostra, -1.0, 0.95);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Sigma negativo: " + e.getMessage());
        }

        // nível de confiança inválido
        try {
            ic.icMediaVarianciaDesconhecida(amostra, 1.5);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Confiança inválida: " + e.getMessage());
        }

        // amostra com n = 1 (impossível estimar variância)
        try {
            ArrayList<Double> amostraUnica = new ArrayList<>(Arrays.asList(12.1));
            ic.icMediaVarianciaDesconhecida(amostraUnica, 0.95);
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Amostra n=1: " + e.getMessage());
        }

        System.out.println();
        System.out.println("  Validação concluída sem travamentos.");
        System.out.println("===========================================");
    }
}
