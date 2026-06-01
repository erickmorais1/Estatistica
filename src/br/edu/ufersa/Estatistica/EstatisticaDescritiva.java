package br.edu.ufersa.Estatistica;
import java.util.ArrayList;

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
                //-999 é o valor padrão para indicar erro
                return -999;
            }
        }else{
            return -999;
        }
    };
}
