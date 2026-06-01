package br.edu.ufersa.Estatistica;
import java.util.ArrayList;
import java.util.Collections;

public class EstatisticaDescritiva {
    public double media(ArrayList<Double> dados) {
        if (dados != null) {
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
            } else {
                //-999 é o noso valor padrão para indicar erro
                return -999;
            }
        }else{
            return -999;
        }
    }
    public double mediana(ArrayList<Double> dados) {
        if (dados != null) {
            //ordena a lista de forma crescente
            Collections.sort(dados);

            //caso a quantidade de dados seja par
            if (dados.size() % 2 == 0) {
                int indice1 = (dados.size() / 2) - 1; //pega o valor que está no ìncide
                int indice2 = (dados.size() / 2);//pega o valor que está no indice

                //os números nos índices são somados e divididos por 2
                double mediana = (dados.get(indice1) + dados.get(indice2)) / 2;
                return mediana;
            } else {
                //caso a quantidade de dados seja ímpar
                //o valor que está no índice *dados.size()/2* será a mediana
                double mediana = dados.get(dados.size() / 2);
                return mediana;
            }
        }else return -999;
    }

}
