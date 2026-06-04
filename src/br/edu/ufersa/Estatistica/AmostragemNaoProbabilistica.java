package br.edu.ufersa.Estatistica;

import java.util.ArrayList;
import java.util.Random;

/**
 * Implementa os métodos de amostragem não probabilística (tópicos 7.3.5, 7.3.6 e 7.3.7).
 *
 * Métodos implementados:
 *   7.3.5 - Amostragem Acidental (ou por conveniência):
 *           Seleciona os primeiros n elementos disponíveis na lista, sem
 *           nenhum critério de aleatoriedade. Simula a coleta de dados
 *           "por conveniência" (ex: os primeiros que aparecem).
 *
 *   7.3.6 - Amostragem Intencional (ou por julgamento):
 *           Seleciona apenas os elementos que satisfazem um critério
 *           definido pelo pesquisador. Aqui o critério é um intervalo
 *           de valores [minimo, maximo].
 *
 *   7.3.7 - Amostragem por Quotas:
 *           Divide a população em grupos (quotas) e coleta um número
 *           proporcional de elementos de cada grupo. Os grupos são
 *           definidos por faixas de valores (ex: faixas etárias).
 *
 */
public class AmostragemNaoProbabilistica {

    private Random random;

    public AmostragemNaoProbabilistica() {
        this.random = new Random(42L);
    }

    public AmostragemNaoProbabilistica(long seed) {
        this.random = new Random(seed);
    }

    // -------------------------------------------------------------------------
    // 7.3.5 — AMOSTRAGEM ACIDENTAL (por conveniência)
    // -------------------------------------------------------------------------

    /**
     * Seleciona os primeiros n elementos da lista, sem aleatoriedade.
     *
     * Simula a situação em que o pesquisador coleta dados dos indivíduos
     * mais fáceis de acessar (ex: primeiros da fila, primeiros cadastrados).
     *
     * @param populacao lista com todos os elementos da população
     * @param n         tamanho desejado da amostra
     * @return ArrayList<Double> com os primeiros n elementos da população
     * @throws IllegalArgumentException se populacao for nula/vazia ou n inválido
     */
    public ArrayList<Double> acidental(ArrayList<Double> populacao, int n) {
        validarEntradas(populacao, n);

        ArrayList<Double> amostra = new ArrayList<>();

        // pega sequencialmente os primeiros n elementos — sem sorteio
        for (int i = 0; i < n; i++) {
            amostra.add(populacao.get(i));
        }

        return amostra;
    }

    // -------------------------------------------------------------------------
    // 7.3.6 — AMOSTRAGEM INTENCIONAL (por julgamento)
    // -------------------------------------------------------------------------

    /**
     * Seleciona todos os elementos que satisfazem o critério definido pelo
     * pesquisador: valor dentro do intervalo fechado [minimo, maximo].
     *
     * Simula a seleção intencional baseada em julgamento técnico,
     * por exemplo: "quero apenas indivíduos com idade entre 30 e 50 anos".
     *
     * @param populacao lista com todos os elementos da população
     * @param minimo    limite inferior do critério de seleção (inclusivo)
     * @param maximo    limite superior do critério de seleção (inclusivo)
     * @return ArrayList<Double> com todos os elementos que atendem ao critério
     * @throws IllegalArgumentException se populacao for nula/vazia ou minimo > maximo
     */
    public ArrayList<Double> intencional(ArrayList<Double> populacao, double minimo, double maximo) {
        validarPopulacao(populacao);

        if (minimo > maximo) {
            throw new IllegalArgumentException("O valor mínimo não pode ser maior que o máximo");
        }

        ArrayList<Double> amostra = new ArrayList<>();

        for (int i = 0; i < populacao.size(); i++) {
            double elemento = populacao.get(i);

            // seleciona elemento se estiver dentro do critério definido
            if (elemento >= minimo && elemento <= maximo) {
                amostra.add(elemento);
            }
        }

        if (amostra.isEmpty()) {
            System.out.println("  Aviso: nenhum elemento satisfez o critério [" + minimo + ", " + maximo + "]");
        }

        return amostra;
    }

    /**
     * Variação da amostragem intencional com limite de tamanho:
     * seleciona até n elementos que satisfazem o critério [minimo, maximo].
     *
     * Útil quando o critério retornaria muitos elementos e o pesquisador
     * deseja limitar o tamanho da amostra.
     *
     * @param populacao lista com todos os elementos da população
     * @param minimo    limite inferior do critério (inclusivo)
     * @param maximo    limite superior do critério (inclusivo)
     * @param n         tamanho máximo da amostra
     * @return ArrayList<Double> com até n elementos que atendem ao critério
     */
    public ArrayList<Double> intencionalComLimite(
            ArrayList<Double> populacao, double minimo, double maximo, int n) {

        ArrayList<Double> todos = intencional(populacao, minimo, maximo);

        if (todos.size() <= n) {
            return todos;
        }

        // retorna apenas os primeiros n elementos que satisfizeram o critério
        ArrayList<Double> amostra = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            amostra.add(todos.get(i));
        }
        return amostra;
    }

    // -------------------------------------------------------------------------
    // 7.3.7 — AMOSTRAGEM POR QUOTAS
    // -------------------------------------------------------------------------

    /**
     * Realiza a amostragem por quotas com base em faixas de valores.
     *
     * O processo segue três etapas:
     *   1. Divide a população em grupos (quotas) conforme as faixas definidas
     *   2. Calcula a proporção de cada grupo em relação à população total
     *   3. Coleta de cada grupo um número de elementos proporcional à sua
     *      representação na população, até totalizar n elementos
     *
     * As faixas são definidas por uma matriz de limites:
     *   faixas[i][0] = limite inferior da faixa i
     *   faixas[i][1] = limite superior da faixa i
     *
     * Exemplo para faixas etárias:
     *   double[][] faixas = { {18,29}, {30,44}, {45,59}, {60,80} };
     *
     * @param populacao lista com todos os elementos da população
     * @param faixas    matriz com os limites de cada faixa/grupo
     * @param rotulos   nomes descritivos de cada faixa (para exibição)
     * @param n         tamanho total desejado da amostra
     * @return ArrayList<Double> com a amostra proporcional coletada
     * @throws IllegalArgumentException se parâmetros forem inválidos
     */
    public ArrayList<Double> porQuotas(
            ArrayList<Double> populacao, double[][] faixas, String[] rotulos, int n) {

        validarEntradas(populacao, n);

        if (faixas == null || faixas.length == 0) {
            throw new IllegalArgumentException("As faixas não podem ser nulas ou vazias");
        }

        if (rotulos != null && rotulos.length != faixas.length) {
            throw new IllegalArgumentException("O número de rótulos deve ser igual ao número de faixas");
        }

        // ETAPA 1: separar população em grupos conforme as faixas
        ArrayList<ArrayList<Double>> grupos = new ArrayList<>();
        for (int i = 0; i < faixas.length; i++) {
            grupos.add(new ArrayList<>());
        }

        for (int i = 0; i < populacao.size(); i++) {
            double elemento = populacao.get(i);
            for (int j = 0; j < faixas.length; j++) {
                if (elemento >= faixas[j][0] && elemento <= faixas[j][1]) {
                    grupos.get(j).add(elemento);
                    break; // elemento pertence apenas a uma faixa
                }
            }
        }

        // ETAPA 2: calcular a quota (n proporcional) de cada grupo
        int[] quotas = calcularQuotas(grupos, populacao.size(), n);

        // ETAPA 3: coletar os elementos de cada grupo de forma acidental
        ArrayList<Double> amostra = new ArrayList<>();

        System.out.println("  Distribuição das quotas:");
        for (int i = 0; i < grupos.size(); i++) {
            String rotulo = (rotulos != null) ? rotulos[i] : "Grupo " + (i + 1);
            int disponiveis = grupos.get(i).size();
            int coletar = Math.min(quotas[i], disponiveis);

            System.out.printf("    %s: %d na população (%.1f%%) → quota = %d%n",
                rotulo,
                disponiveis,
                (disponiveis * 100.0) / populacao.size(),
                coletar
            );

            // coleta os primeiros 'coletar' elementos do grupo (amostragem acidental dentro da quota)
            for (int j = 0; j < coletar; j++) {
                amostra.add(grupos.get(i).get(j));
            }
        }

        System.out.println("  Total coletado: " + amostra.size() + " (solicitado: " + n + ")");

        return amostra;
    }

    /**
     * Calcula quantos elementos coletar de cada grupo para respeitar as
     * proporções populacionais e totalizar n elementos na amostra.
     *
     * Usa o método do quociente inteiro com ajuste no maior grupo para
     * garantir que a soma das quotas seja exatamente n.
     *
     * @param grupos    lista de grupos da população
     * @param totalPop  tamanho total da população
     * @param n         tamanho total desejado da amostra
     * @return array com a quota de cada grupo
     */
    private int[] calcularQuotas(ArrayList<ArrayList<Double>> grupos, int totalPop, int n) {
        int[] quotas = new int[grupos.size()];
        int somaParcial = 0;
        int maiorQuota = 0;
        int indiceMaior = 0;

        for (int i = 0; i < grupos.size(); i++) {
            // quota proporcional: (tamanho do grupo / total da população) * n
            double proporcao = (double) grupos.get(i).size() / totalPop;
            quotas[i] = (int) Math.floor(proporcao * n);
            somaParcial += quotas[i];

            // rastreia o grupo com maior quota para ajuste posterior
            if (quotas[i] > maiorQuota) {
                maiorQuota = quotas[i];
                indiceMaior = i;
            }
        }

        // ajusta o maior grupo para garantir que a soma seja exatamente n
        quotas[indiceMaior] += (n - somaParcial);

        return quotas;
    }

    // -------------------------------------------------------------------------
    // VALIDAÇÕES
    // -------------------------------------------------------------------------

    private void validarPopulacao(ArrayList<Double> populacao) {
        if (populacao == null) {
            throw new IllegalArgumentException("População não pode ser nula");
        }
        if (populacao.isEmpty()) {
            throw new IllegalArgumentException("População não pode estar vazia");
        }
    }

    private void validarEntradas(ArrayList<Double> populacao, int n) {
        validarPopulacao(populacao);

        if (n <= 0) {
            throw new IllegalArgumentException("Tamanho da amostra deve ser positivo");
        }

        if (n > populacao.size()) {
            throw new IllegalArgumentException(
                "Tamanho da amostra não pode ser maior que o tamanho da população");
        }
    }
}
