package br.edu.ufersa.Estatistica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Implementa a amostragem por conglomerados (tópico 7.3.4).
 *
 * Na amostragem por conglomerados, a população é dividida em grupos
 * (conglomerados) mutuamente exclusivos e exaustivos. Em seguida, alguns
 * conglomerados são sorteados aleatoriamente e TODOS os elementos dos
 * conglomerados sorteados compõem a amostra.
 *
 * Diferença em relação à amostragem estratificada:
 *   - Estratificada: sorteia ELEMENTOS de cada estrato
 *   - Conglomerados: sorteia GRUPOS inteiros
 *
 */
public class AmostragemConglomerados {

    private Random random;

    public AmostragemConglomerados() {
        // semente fixa para reprodutibilidade
        this.random = new Random(42L);
    }

    /**
     * Construtor que permite definir uma semente personalizada.
     * Útil para testes com resultados diferentes a cada execução.
     *
     * @param seed semente para o gerador de números aleatórios
     */
    public AmostragemConglomerados(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Divide a população em conglomerados de tamanho aproximadamente igual.
     *
     * Os elementos são distribuídos sequencialmente: o conglomerado 1 recebe
     * os primeiros (N/qtdConglomerados) elementos, o conglomerado 2 os
     * seguintes, e assim por diante. O último conglomerado absorve os
     * elementos restantes caso N não seja divisível por qtdConglomerados.
     *
     * @param populacao        lista com todos os elementos da população
     * @param qtdConglomerados número de conglomerados a criar (deve ser >= 2)
     * @return ArrayList de ArrayLists, onde cada ArrayList interno é um conglomerado
     * @throws IllegalArgumentException se populacao for nula/vazia ou qtdConglomerados < 2
     */
    public ArrayList<ArrayList<Double>> dividirEmConglomerados(
            ArrayList<Double> populacao, int qtdConglomerados) {

        validarPopulacao(populacao);

        if (qtdConglomerados < 2) {
            throw new IllegalArgumentException("É necessário pelo menos 2 conglomerados");
        }

        if (qtdConglomerados > populacao.size()) {
            throw new IllegalArgumentException(
                "Número de conglomerados não pode ser maior que o tamanho da população");
        }

        ArrayList<ArrayList<Double>> conglomerados = new ArrayList<>();

        // tamanho base de cada conglomerado
        int tamBase = populacao.size() / qtdConglomerados;
        int inicio = 0;

        for (int i = 0; i < qtdConglomerados; i++) {
            ArrayList<Double> conglomerado = new ArrayList<>();

            // o último conglomerado pega todos os elementos restantes
            int fim = (i == qtdConglomerados - 1) ? populacao.size() : inicio + tamBase;

            for (int j = inicio; j < fim; j++) {
                conglomerado.add(populacao.get(j));
            }

            conglomerados.add(conglomerado);
            inicio = fim;
        }

        return conglomerados;
    }

    /**
     * Realiza a amostragem por conglomerados: sorteia aleatoriamente
     * qtdConglomeradosSorteados conglomerados e retorna todos os seus elementos.
     *
     * @param conglomerados              lista de conglomerados gerada por dividirEmConglomerados()
     * @param qtdConglomeradosSorteados  quantos conglomerados sortear (>= 1)
     * @return ArrayList<Double> com todos os elementos dos conglomerados sorteados
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    public ArrayList<Double> amostrar(
            ArrayList<ArrayList<Double>> conglomerados, int qtdConglomeradosSorteados) {

        if (conglomerados == null || conglomerados.isEmpty()) {
            throw new IllegalArgumentException("Lista de conglomerados não pode ser nula ou vazia");
        }

        if (qtdConglomeradosSorteados < 1) {
            throw new IllegalArgumentException("É necessário sortear pelo menos 1 conglomerado");
        }

        if (qtdConglomeradosSorteados > conglomerados.size()) {
            throw new IllegalArgumentException(
                "Quantidade a sortear não pode ser maior que o número de conglomerados disponíveis");
        }

        // cria lista de índices e embaralha para sorteio sem reposição
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < conglomerados.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);

        // coleta todos os elementos dos conglomerados sorteados
        ArrayList<Double> amostra = new ArrayList<>();
        ArrayList<Integer> indicesSorteados = new ArrayList<>();

        for (int i = 0; i < qtdConglomeradosSorteados; i++) {
            int idxSorteado = indices.get(i);
            indicesSorteados.add(idxSorteado);
            amostra.addAll(conglomerados.get(idxSorteado));
        }

        System.out.println("  Conglomerados sorteados (índices): " + indicesSorteados);
        System.out.println("  Total de elementos na amostra: " + amostra.size());

        return amostra;
    }

    /**
     * Método conveniente que executa divisão e sorteio em uma única chamada.
     *
     * @param populacao                  população completa
     * @param qtdConglomerados           em quantos grupos dividir
     * @param qtdConglomeradosSorteados  quantos grupos sortear
     * @return ArrayList<Double> com a amostra resultante
     */
    public ArrayList<Double> dividirEAmostrar(
            ArrayList<Double> populacao, int qtdConglomerados, int qtdConglomeradosSorteados) {

        ArrayList<ArrayList<Double>> conglomerados =
            dividirEmConglomerados(populacao, qtdConglomerados);

        return amostrar(conglomerados, qtdConglomeradosSorteados);
    }

    /**
     * Valida se a população é não nula e não vazia.
     */
    private void validarPopulacao(ArrayList<Double> populacao) {
        if (populacao == null) {
            throw new IllegalArgumentException("População não pode ser nula");
        }
        if (populacao.isEmpty()) {
            throw new IllegalArgumentException("População não pode estar vazia");
        }
    }
}
