package br.edu.ufersa.Estatistica;

import java.util.ArrayList;
import java.util.Collections;

public class EstatisticaDescritiva {

    public double media(ArrayList<Double> dados) {
        //verifica se a lista não é vazia
        if (dados == null) {
            throw new IllegalArgumentException("Lista não pode ser nula");
        }

        if (dados.size() == 0) {
            throw new IllegalArgumentException("Lista não pode estar vazia");
        }

        //contador recebe o tamanho da lista de dados
        int contador = dados.size();
        double soma = 0;

        if (contador > 0) {
            for (int i = 0; i < contador; i++) {
                //soma todos os itens da lista de dados
                soma += dados.get(i);
            }
            //retorna a media(soma dos termos / quantidade de termos)
            return soma / contador;
        }

        //não será mais alcançado, mas mantido por coerência estrutural
        throw new IllegalArgumentException("Erro ao calcular média");
    }

    public double mediana(ArrayList<Double> dados) {
        //verifica se a lista não é vazia
        if (dados == null) {
            throw new IllegalArgumentException("Lista não pode ser nula");
        }

        if (dados.isEmpty()) {
            throw new IllegalArgumentException("Lista não pode estar vazia");
        }

        //ordena a lista de forma crescente
        ArrayList<Double> copia = new ArrayList<>(dados);
        Collections.sort(copia);

        int n = copia.size();

        //caso a quantidade de dados seja par
        if (n % 2 == 0) {
            int indice1 = (n / 2) - 1; //pega o valor que está no ìncide
            int indice2 = (n / 2);     //pega o valor que está no indice

            //os números nos índices são somados e divididos por 2
            double mediana = (copia.get(indice1) + copia.get(indice2)) / 2;
            return mediana;

        } else {
            //caso a quantidade de dados seja ímpar
            //o valor que está no índice *dados.size()/2* será a mediana
            double mediana = copia.get(n / 2);
            return mediana;
        }
    }

    public double varianciaAmostral(ArrayList<Double> dados) {
        //verifica se a lista não é vazia e tem tamanho maior ou igual a 2
        //pois, caso contrario, vai haver divisão por 0 em n-1
        if (dados == null) {
            throw new IllegalArgumentException("Lista não pode ser nula");
        }

        if (dados.size() < 2) {
            throw new IllegalArgumentException("É necessário pelo menos 2 elementos para variância");
        }

        //pega a media dos dados
        double media = media(dados);
        double variancia = 0;
        double temporario = 0;

        for (int i = 0; i < dados.size(); i++) {
            //temporario vai receber os valores elevados ao quadrado e ja somados
            temporario += (Math.pow(dados.get(i) - media, 2));
        }

        variancia = temporario / (dados.size() - 1);
        return variancia;
    }

    public double desvioPadrao(ArrayList<Double> dados) {
        //verifica se a lista de dados não está vazia
        if (dados == null) {
            throw new IllegalArgumentException("Lista não pode ser nula");
        }
        //lista de dados precisa de um tamanho mínimo
        if (dados.size() < 2) {
            throw new IllegalArgumentException("É necessário pelo menos 2 elementos para desvio padrão");
        }

        double desvioP = varianciaAmostral(dados);
        //calcula a raiz quadrada da variância
        return Math.sqrt(desvioP);
    }
}