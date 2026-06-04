package br.edu.ufersa.Estatistica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Resolve computacionalmente os Exercícios da Série I do capítulo de Amostragem.
 *
 * Cada método corresponde a uma operação exigida pelos exercícios, recebendo
 * todos os dados necessários como parâmetros de entrada — sem valores fixos
 * embutidos no código — tornando o módulo reutilizável para qualquer conjunto
 * de dados.
 *
 * Exercícios cobertos:
 *   Ex. 1 — Tamanho amostral para média, AAS, agrupamento em classes,
 *            média, desvio padrão e verificação |µ - x̄| ≤ d
 *   Ex. 2 — Amostragem sistemática de uma lista
 *   Ex. 3 — Tamanho amostral para proporção com p desconhecido
 *   Ex. 4 — Tamanho amostral para proporção, população infinita
 *   Ex. 5 — Tamanho amostral para proporção, população finita; comparação com Ex. 4
 *   Ex. 6 — Tamanho amostral para média com σ estimado pelo usuário
 */
public class ExerciciosSerie1 {

    private EstatisticaDescritiva estatistica;
    private Random random;

    public ExerciciosSerie1() {
        this.estatistica = new EstatisticaDescritiva();
        this.random = new Random(42L);
    }

    public ExerciciosSerie1(long seed) {
        this.estatistica = new EstatisticaDescritiva();
        this.random = new Random(seed);
    }

    // -------------------------------------------------------------------------
    // EXERCÍCIO 1
    // -------------------------------------------------------------------------

    /**
     * Ex. 1a — Calcula o tamanho amostral n para estimar a média populacional
     * com margem de erro d e desvio padrão σ conhecidos.
     *
     * Fórmula: n = (z · σ / d)²
     *
     * @param z     valor crítico Z para o nível de confiança (ex: 1.96 para 95%)
     * @param sigma desvio padrão populacional σ
     * @param d     margem de erro tolerável
     * @return tamanho amostral mínimo n (arredondado para cima)
     * @throws IllegalArgumentException se sigma ou d forem não positivos
     */
    public int calcularTamanhoAmostralMedia(double z, double sigma, double d) {
        if (sigma <= 0) {
            throw new IllegalArgumentException("O desvio padrão σ deve ser positivo");
        }
        if (d <= 0) {
            throw new IllegalArgumentException("A margem de erro d deve ser positiva");
        }

        // n = (z · σ / d)² arredondado para cima
        double nExato = Math.pow((z * sigma) / d, 2);
        return (int) Math.ceil(nExato);
    }

    /**
     * Ex. 1b — Retira uma Amostra Aleatória Simples (AAS) sem reposição
     * de tamanho n da população fornecida.
     *
     * Cada elemento tem a mesma probabilidade de ser selecionado.
     * O sorteio é realizado embaralhando os índices da população.
     *
     * @param populacao lista com todos os elementos da população
     * @param n         tamanho da amostra a retirar
     * @return ArrayList<Double> com os n elementos sorteados
     * @throws IllegalArgumentException se populacao for nula/vazia ou n inválido
     */
    public ArrayList<Double> amostraAleatoriaSimplesSeR(ArrayList<Double> populacao, int n) {
        validarEntradas(populacao, n);

        // copia os índices e embaralha para sorteio sem reposição
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < populacao.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);

        // coleta os primeiros n índices sorteados
        ArrayList<Double> amostra = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            amostra.add(populacao.get(indices.get(i)));
        }

        return amostra;
    }

    /**
     * Ex. 1c — Agrupa os elementos de uma amostra em classes de frequência.
     *
     * O número de classes é calculado pela regra de Sturges: k = 1 + 3,322 · log₁₀(n).
     * A amplitude de cada classe é h = (máximo - mínimo) / k.
     *
     * @param amostra lista com os valores da amostra
     * @return matriz onde cada linha representa uma classe:
     *         [0] limite inferior, [1] limite superior, [2] frequência absoluta
     * @throws IllegalArgumentException se amostra for nula ou vazia
     */
    public double[][] agruparEmClasses(ArrayList<Double> amostra) {
        if (amostra == null || amostra.isEmpty()) {
            throw new IllegalArgumentException("Amostra não pode ser nula ou vazia");
        }

        // número de classes pela regra de Sturges
        int k = (int) Math.ceil(1 + 3.322 * Math.log10(amostra.size()));

        double minimo = Collections.min(amostra);
        double maximo = Collections.max(amostra);

        // amplitude de cada classe
        double h = (maximo - minimo) / k;

        // garante que o valor máximo caia dentro da última classe
        if (h == 0) h = 1;

        double[][] classes = new double[k][3];

        // define os limites de cada classe
        for (int i = 0; i < k; i++) {
            classes[i][0] = minimo + i * h;       // limite inferior
            classes[i][1] = minimo + (i + 1) * h; // limite superior
            classes[i][2] = 0;                     // frequência (será preenchida abaixo)
        }

        // conta a frequência de cada classe: intervalo fechado à esquerda [li, ls)
        // exceto a última classe que é fechada dos dois lados [li, ls]
        for (int j = 0; j < amostra.size(); j++) {
            double valor = amostra.get(j);
            for (int i = 0; i < k; i++) {
                boolean dentroDoIntervalo;
                if (i == k - 1) {
                    // última classe: fechada dos dois lados
                    dentroDoIntervalo = valor >= classes[i][0] && valor <= classes[i][1];
                } else {
                    // demais classes: fechado à esquerda, aberto à direita
                    dentroDoIntervalo = valor >= classes[i][0] && valor < classes[i][1];
                }
                if (dentroDoIntervalo) {
                    classes[i][2]++;
                    break;
                }
            }
        }

        return classes;
    }

    /**
     * Ex. 1f — Verifica se a diferença entre a média populacional µ e a média
     * amostral x̄ está dentro da margem de erro tolerável d.
     *
     * Condição verificada: |µ - x̄| ≤ d
     *
     * @param mediaPop      média populacional µ
     * @param mediaAmostral média amostral x̄
     * @param d             margem de erro tolerável
     * @return true se |µ - x̄| ≤ d, false caso contrário
     */
    public boolean verificarMargemDeErro(double mediaPop, double mediaAmostral, double d) {
        double diferenca = Math.abs(mediaPop - mediaAmostral);
        System.out.printf("  |µ - x̄| = |%.4f - %.4f| = %.4f  (d = %.4f)  →  %s%n",
            mediaPop, mediaAmostral, diferenca, d,
            diferenca <= d ? "DENTRO da margem ✓" : "FORA da margem ✗");
        return diferenca <= d;
    }

    // -------------------------------------------------------------------------
    // EXERCÍCIO 2
    // -------------------------------------------------------------------------

    /**
     * Ex. 2 — Realiza amostragem sistemática de tamanho n a partir de uma lista.
     *
     * Na amostragem sistemática, o intervalo de salto k = N/n é calculado
     * automaticamente. Um ponto de partida aleatório r é sorteado no intervalo
     * [0, k-1] e, a partir dele, seleciona-se cada k-ésimo elemento.
     *
     * Exemplo: lista com 500 nomes, n=50 → k=10. Se r=3, seleciona os
     * elementos nas posições 3, 13, 23, 33, ...
     *
     * @param lista lista de elementos (pode ser nomes, valores, etc.)
     * @param n     tamanho da amostra sistemática desejada
     * @return ArrayList<String> com os n elementos selecionados sistematicamente
     * @throws IllegalArgumentException se lista for nula/vazia ou n inválido
     */
    public ArrayList<String> amostragemsistematica(ArrayList<String> lista, int n) {
        if (lista == null || lista.isEmpty()) {
            throw new IllegalArgumentException("A lista não pode ser nula ou vazia");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("O tamanho da amostra deve ser positivo");
        }
        if (n > lista.size()) {
            throw new IllegalArgumentException(
                "Tamanho da amostra não pode ser maior que o tamanho da lista");
        }

        // intervalo de salto k = N / n
        int k = lista.size() / n;

        // ponto de partida aleatório entre 0 e k-1
        int r = random.nextInt(k);

        ArrayList<String> amostra = new ArrayList<>();
        int posicao = r;

        // seleciona um elemento a cada k posições
        while (amostra.size() < n && posicao < lista.size()) {
            amostra.add(lista.get(posicao));
            posicao += k;
        }

        System.out.printf("  Intervalo de salto k = %d  |  Ponto de partida r = %d%n", k, r);

        return amostra;
    }

    // -------------------------------------------------------------------------
    // EXERCÍCIO 3
    // -------------------------------------------------------------------------

    /**
     * Ex. 3 — Calcula o tamanho amostral para estimar uma proporção p
     * quando p é desconhecido.
     *
     * Quando p é desconhecido, adota-se p = q = 0,5, pois esse valor
     * maximiza o produto p·q e garante o maior tamanho amostral possível
     * (estimativa mais conservadora).
     *
     * Fórmula (população infinita): n = z² · p · q / d²
     *
     * @param z valor crítico Z para o nível de confiança
     * @param d margem de erro tolerável para a proporção
     * @return tamanho amostral mínimo n
     * @throws IllegalArgumentException se d for não positivo
     */
    public int calcularTamanhoAmostralProporçãoDesconhecida(double z, double d) {
        if (d <= 0) {
            throw new IllegalArgumentException("A margem de erro d deve ser positiva");
        }

        // p desconhecido → usa p = q = 0,5 (caso mais conservador)
        double p = 0.5;
        double q = 0.5;

        double nExato = (z * z * p * q) / (d * d);
        return (int) Math.ceil(nExato);
    }

    // -------------------------------------------------------------------------
    // EXERCÍCIO 4
    // -------------------------------------------------------------------------

    /**
     * Ex. 4 — Calcula o tamanho amostral para estimar uma proporção
     * com p̂ = q̂ = 0,5 e população infinita.
     *
     * Fórmula: n = z² · p · q / d²
     *
     * @param z valor crítico Z para o nível de confiança
     * @param p proporção estimada p̂ (ex: 0.5)
     * @param q complemento q̂ = 1 - p̂ (ex: 0.5)
     * @param d margem de erro tolerável
     * @return tamanho amostral mínimo n para população infinita
     * @throws IllegalArgumentException se p+q ≠ 1 ou d for não positivo
     */
    public int calcularTamanhoAmostralProporçãoInfinita(double z, double p, double q, double d) {
        validarProporçao(p, q, d);

        double nExato = (z * z * p * q) / (d * d);
        return (int) Math.ceil(nExato);
    }

    // -------------------------------------------------------------------------
    // EXERCÍCIO 5
    // -------------------------------------------------------------------------

    /**
     * Ex. 5 — Calcula o tamanho amostral para estimar uma proporção
     * com população finita de tamanho N, aplicando o fator de correção.
     *
     * Fórmula:
     *   n₀ = z² · p · q / d²          (tamanho para população infinita)
     *   n  = n₀ · N / (n₀ + N - 1)    (corrigido para população finita)
     *
     * O fator de correção reduz n quando a população é pequena em relação
     * a n₀, evitando amostras maiores que o necessário.
     *
     * @param z valor crítico Z
     * @param p proporção estimada p̂
     * @param q complemento q̂ = 1 - p̂
     * @param d margem de erro tolerável
     * @param N tamanho da população finita
     * @return tamanho amostral corrigido para população finita
     * @throws IllegalArgumentException se N < 1 ou demais parâmetros inválidos
     */
    public int calcularTamanhoAmostralProporçãoFinita(
            double z, double p, double q, double d, int N) {

        validarProporçao(p, q, d);

        if (N < 1) {
            throw new IllegalArgumentException("O tamanho populacional N deve ser pelo menos 1");
        }

        // n₀ para população infinita
        double n0 = (z * z * p * q) / (d * d);

        // fator de correção para população finita
        double nCorrigido = (n0 * N) / (n0 + N - 1);

        return (int) Math.ceil(nCorrigido);
    }

    /**
     * Ex. 5 (complemento) — Compara os tamanhos amostrais para população
     * infinita e finita, exibindo a diferença e o percentual de redução.
     *
     * @param nInfinito tamanho amostral para população infinita (resultado Ex. 4)
     * @param nFinito   tamanho amostral para população finita (resultado Ex. 5)
     */
    public void compararTamanhosAmostrais(int nInfinito, int nFinito) {
        int reducao = nInfinito - nFinito;
        double percentual = (reducao * 100.0) / nInfinito;

        System.out.println("  Comparação Ex. 4 vs Ex. 5:");
        System.out.printf("    n (população infinita) = %d%n", nInfinito);
        System.out.printf("    n (população finita)   = %d%n", nFinito);
        System.out.printf("    Redução: %d unidades (%.1f%%)%n", reducao, percentual);
        System.out.println("    → O fator de correção reduz o tamanho amostral necessário");
        System.out.println("      quando a população é finita e relativamente pequena.");
    }

    // -------------------------------------------------------------------------
    // EXERCÍCIO 6
    // -------------------------------------------------------------------------

    /**
     * Ex. 6 — Calcula o tamanho amostral para estimar a média de uma
     * variável contínua (ex: salário médio) com σ fornecido pelo usuário.
     *
     * Reutiliza a fórmula do Ex. 1a: n = (z · σ / d)²
     *
     * Quando σ não é conhecido previamente, o pesquisador pode estimá-lo
     * por meio de um estudo piloto, dados históricos ou experiência prévia.
     *
     * @param z            valor crítico Z para o nível de confiança
     * @param sigmaEstimado estimativa do desvio padrão populacional σ
     * @param d            margem de erro tolerável (em mesma unidade que σ)
     * @return tamanho amostral mínimo n
     * @throws IllegalArgumentException se sigmaEstimado ou d forem não positivos
     */
    public int calcularTamanhoAmostralMediaEstimada(double z, double sigmaEstimado, double d) {
        // reutiliza a mesma fórmula do Ex. 1a — modularidade aplicada
        return calcularTamanhoAmostralMedia(z, sigmaEstimado, d);
    }

    // -------------------------------------------------------------------------
    // VALIDAÇÕES INTERNAS
    // -------------------------------------------------------------------------

    private void validarEntradas(ArrayList<Double> populacao, int n) {
        if (populacao == null) {
            throw new IllegalArgumentException("População não pode ser nula");
        }
        if (populacao.isEmpty()) {
            throw new IllegalArgumentException("População não pode estar vazia");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("Tamanho da amostra deve ser positivo");
        }
        if (n > populacao.size()) {
            throw new IllegalArgumentException(
                "Tamanho da amostra não pode ser maior que o tamanho da população");
        }
    }

    private void validarProporçao(double p, double q, double d) {
        if (Math.abs(p + q - 1.0) > 1e-9) {
            throw new IllegalArgumentException("p + q deve ser igual a 1");
        }
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("p deve estar entre 0 e 1");
        }
        if (d <= 0) {
            throw new IllegalArgumentException("A margem de erro d deve ser positiva");
        }
    }
}
