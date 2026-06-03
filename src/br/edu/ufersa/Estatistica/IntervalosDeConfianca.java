package br.edu.ufersa.Estatistica;

import java.util.ArrayList;

/**
 * Calcula Intervalos de Confiança (IC) para a média populacional µ.
 *
 * Tópico 8.2 → IC com variância conhecida: usa distribuição Z
 * Tópico 8.3 → IC com variância desconhecida: usa t de Student com gl = n-1
 *
 * Todos os métodos retornam double[2] = { limiteInferior, limiteSuperior }.
 */
public class IntervalosDeConfianca {

    private Distribuicoes distribuicoes;
    private EstatisticaDescritiva estatistica;

    public IntervalosDeConfianca() {
        this.distribuicoes = new Distribuicoes();
        this.estatistica = new EstatisticaDescritiva();
    }

    // -------------------------------------------------------------------------
    // TÓPICO 8.2 — IC PARA µ COM VARIÂNCIA CONHECIDA (usa Z)
    // -------------------------------------------------------------------------

    /**
     * Constrói o IC para µ quando σ² é conhecida.
     * Fórmula: IC = [ x̄ - z·(σ/√n) ; x̄ + z·(σ/√n) ]
     *
     * @param dados     valores da amostra
     * @param sigma     desvio padrão POPULACIONAL σ (não σ²)
     * @param confianca nível de confiança em (0, 1), ex: 0.95
     * @return double[2] → { limiteInferior, limiteSuperior }
     */
    public double[] icMediaVarianciaConhecida(ArrayList<Double> dados, double sigma, double confianca) {
        validarDados(dados);

        if (sigma <= 0) {
            throw new IllegalArgumentException("Desvio padrão populacional (sigma) deve ser positivo");
        }

        if (confianca <= 0 || confianca >= 1) {
            throw new IllegalArgumentException("Nível de confiança deve estar entre 0 e 1 (exclusive)");
        }

        int n = dados.size();
        double media = estatistica.media(dados);
        double erroPadrao = erroPadraoConhecido(sigma, n);
        double z = distribuicoes.zCritico(confianca);
        double margem = z * erroPadrao;

        return new double[]{media - margem, media + margem};
    }

    // -------------------------------------------------------------------------
    // TÓPICO 8.3 — IC PARA µ COM VARIÂNCIA DESCONHECIDA (usa t de Student)
    // -------------------------------------------------------------------------

    /**
     * Constrói o IC para µ quando σ² é desconhecida.
     * Substitui σ por s (desvio amostral) e Z pelo valor crítico t com gl = n-1.
     * Fórmula: IC = [ x̄ - t·(s/√n) ; x̄ + t·(s/√n) ]
     */
    public double[] icMediaVarianciaDesconhecida(ArrayList<Double> dados, double confianca) {
        validarDados(dados);

        if (dados.size() < 2) {
            throw new IllegalArgumentException("São necessários pelo menos 2 elementos para estimar a variância");
        }

        if (confianca <= 0 || confianca >= 1) {
            throw new IllegalArgumentException("Nível de confiança deve estar entre 0 e 1 (exclusive)");
        }

        int n = dados.size();
        double media = estatistica.media(dados);
        double desvioPadrao = estatistica.desvioPadrao(dados); // s amostral

        int grausLiberdade = n - 1;
        double erroPadrao = erroPadraoDesconhecido(desvioPadrao, n);
        double t = distribuicoes.tCritico(confianca, grausLiberdade);
        double margem = t * erroPadrao;

        return new double[]{media - margem, media + margem};
    }

    // -------------------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // -------------------------------------------------------------------------

    /**
     * Erro padrão quando σ é conhecido: EP = σ / √n
     */
    public double erroPadraoConhecido(double sigma, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Tamanho da amostra deve ser positivo");
        }
        return sigma / Math.sqrt(n);
    }

    /**
     * Erro padrão quando σ é estimado pelo desvio amostral s: EP = s / √n
     */
    public double erroPadraoDesconhecido(double desvioPadrao, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Tamanho da amostra deve ser positivo");
        }
        return desvioPadrao / Math.sqrt(n);
    }

    /**
     * Verifica se a lista de dados é válida para cálculo.
     */
    private void validarDados(ArrayList<Double> dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Lista de dados não pode ser nula");
        }
        if (dados.isEmpty()) {
            throw new IllegalArgumentException("Lista de dados não pode estar vazia");
        }
    }

    /**
     * Imprime no console um resumo formatado do intervalo calculado.
     * Útil para depuração e validação dos resultados.
     */
    public void exibirResultado(double[] intervalo, double confianca, String tipoVariancia) {
        System.out.println("===========================================");
        System.out.println("  INTERVALO DE CONFIANÇA PARA A MÉDIA (µ)");
        System.out.println("===========================================");
        System.out.printf("  Variância: %s%n", tipoVariancia);
        System.out.printf("  Nível de confiança: %.0f%%%n", confianca * 100);
        System.out.printf("  Limite inferior: %.6f%n", intervalo[0]);
        System.out.printf("  Limite superior: %.6f%n", intervalo[1]);
        System.out.printf("  Amplitude do IC: %.6f%n", intervalo[1] - intervalo[0]);
        System.out.println("===========================================");
    }
}
