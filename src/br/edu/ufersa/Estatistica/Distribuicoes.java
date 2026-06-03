package br.edu.ufersa.Estatistica;

/**
 * Calcula os valores críticos das distribuições Z (Normal Padrão) e t de Student.
 * Todos os algoritmos são implementados numericamente, sem bibliotecas externas.
 */
public class Distribuicoes {

    // -------------------------------------------------------------------------
    // DISTRIBUIÇÃO NORMAL PADRÃO (Z)
    // -------------------------------------------------------------------------

    /**
     * Retorna o valor crítico Z para o nível de confiança informado.
     * Ex: zCritico(0.95) → ≈ 1.96
     */
    public double zCritico(double confianca) {
        if (confianca <= 0 || confianca >= 1) {
            throw new IllegalArgumentException("Nível de confiança deve estar entre 0 e 1 (exclusive)");
        }

        // a probabilidade acumulada até o quantil superior é 1 - alfa/2
        double alfa = 1.0 - confianca;
        double p = 1.0 - (alfa / 2.0);

        return inversaNormalPadrao(p);
    }

    /**
     * Calcula P(Z <= x) pela relação com a função erro: 0.5 * (1 + erf(x / √2))
     */
    public double cdfNormalPadrao(double x) {
        return 0.5 * (1.0 + erf(x / Math.sqrt(2.0)));
    }

    /**
     * Aproxima erf(x) via série de Taylor.
     * erf(x) = (2/√π) * Σ (-1)^n * x^(2n+1) / (n! * (2n+1))
     */
    private double erf(double x) {
        double soma = 0.0;
        double termo = x;
        double xQuad = x * x;

        for (int n = 0; n < 200; n++) {
            soma += termo;
            termo *= -xQuad / (n + 1.0);
            termo *= (2.0 * n + 1.0) / (2.0 * n + 3.0);

            if (Math.abs(termo) < 1e-15) {
                break;
            }
        }

        return (2.0 / Math.sqrt(Math.PI)) * soma;
    }

    /**
     * Inverte a CDF da Normal Padrão por busca binária no intervalo [-10, 10].
     */
    private double inversaNormalPadrao(double p) {
        double inferior = -10.0;
        double superior = 10.0;
        double meio = 0.0;

        for (int i = 0; i < 1000; i++) {
            meio = (inferior + superior) / 2.0;
            double cdf = cdfNormalPadrao(meio);

            if (Math.abs(cdf - p) < 1e-10) {
                break;
            }

            if (cdf < p) {
                inferior = meio;
            } else {
                superior = meio;
            }
        }

        return meio;
    }

    // -------------------------------------------------------------------------
    // DISTRIBUIÇÃO t DE STUDENT
    // -------------------------------------------------------------------------

    /**
     * Retorna o valor crítico t de Student para o nível de confiança e graus
     * de liberdade informados.
     * Ex: tCritico(0.95, 9) → ≈ 2.262 (para amostra n=10)
     */
    public double tCritico(double confianca, int grausLiberdade) {
        if (confianca <= 0 || confianca >= 1) {
            throw new IllegalArgumentException("Nível de confiança deve estar entre 0 e 1 (exclusive)");
        }

        if (grausLiberdade < 1) {
            throw new IllegalArgumentException("Graus de liberdade deve ser pelo menos 1");
        }

        double alfa = 1.0 - confianca;
        double p = 1.0 - (alfa / 2.0);

        return inversaTStudent(p, grausLiberdade);
    }

    /**
     * Calcula P(T <= t) integrando numericamente a PDF pelo método do trapézio.
     * O limite inferior é truncado em -20 como aproximação de -infinito.
     */
    public double cdfTStudent(double t, int grausLiberdade) {
        double limiteInferior = -20.0;
        int passos = 10000;
        double h = (t - limiteInferior) / passos;

        double soma = 0.0;
        for (int i = 0; i <= passos; i++) {
            double x = limiteInferior + i * h;
            double fx = pdfTStudent(x, grausLiberdade);

            // regra do trapézio: extremos com peso 1, demais com peso 2
            if (i == 0 || i == passos) {
                soma += fx;
            } else {
                soma += 2.0 * fx;
            }
        }

        return (h / 2.0) * soma;
    }

    /**
     * Densidade da t de Student:
     * f(x) = Γ((gl+1)/2) / (√(gl·π) · Γ(gl/2)) · (1 + x²/gl)^(-(gl+1)/2)
     */
    private double pdfTStudent(double x, int grausLiberdade) {
        double gl = (double) grausLiberdade;
        double coeficiente = gamma((gl + 1.0) / 2.0) / (Math.sqrt(gl * Math.PI) * gamma(gl / 2.0));
        double base = 1.0 + (x * x) / gl;
        double expoente = -(gl + 1.0) / 2.0;

        return coeficiente * Math.pow(base, expoente);
    }

    /**
     * Aproxima a função Gamma de Euler pelos coeficientes de Lanczos (g=7, n=9).
     * Válida para qualquer x > 0; usa reflexão para x < 0.5.
     */
    private double gamma(double x) {
        double[] coef = {
            0.99999999999980993,
            676.5203681218851,
            -1259.1392167224028,
            771.32342877765313,
            -176.61502916214059,
            12.507343278686905,
            -0.13857109526572012,
            9.9843695780195716e-6,
            1.5056327351493116e-7
        };

        if (x < 0.5) {
            // fórmula de reflexão: Γ(x) = π / (sin(πx) · Γ(1-x))
            return Math.PI / (Math.sin(Math.PI * x) * gamma(1.0 - x));
        }

        x -= 1.0;
        double a = coef[0];
        double t = x + 7.5;

        for (int i = 1; i < 9; i++) {
            a += coef[i] / (x + i);
        }

        return Math.sqrt(2.0 * Math.PI) * Math.pow(t, x + 0.5) * Math.exp(-t) * a;
    }

    /**
     * Inverte a CDF da t de Student por busca binária no intervalo [-50, 50].
     */
    private double inversaTStudent(double p, int grausLiberdade) {
        double inferior = -50.0;
        double superior = 50.0;
        double meio = 0.0;

        for (int i = 0; i < 1000; i++) {
            meio = (inferior + superior) / 2.0;
            double cdf = cdfTStudent(meio, grausLiberdade);

            if (Math.abs(cdf - p) < 1e-8) {
                break;
            }

            if (cdf < p) {
                inferior = meio;
            } else {
                superior = meio;
            }
        }

        return meio;
    }
}
