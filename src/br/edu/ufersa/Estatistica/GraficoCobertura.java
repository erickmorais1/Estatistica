package br.edu.ufersa.Estatistica;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Fase 2 — Análise Gráfica Obrigatória.
 *
 * Exibe os 100 ICs simulados como linhas horizontais:
 *   Verde  → IC contém µ (correto)
 *   Vermelho → IC não contém µ (erro tipo I)
 *   Azul pontilhado → µ real
 *
 * A taxa de cobertura visível deve estar próxima de 95%.
 */



public class GraficoCobertura extends JPanel {
     private final ArrayList<SimulacaoIntervalosConfianca.ResultadoIC> resultados;
    private final double mediaPop;

    private static final int W  = 920, H  = 760;
    private static final int ML = 80,  MR = 30, MT = 70, MB = 85;

    private static final Color COR_CONTEM     = new Color(34, 139, 34);
    private static final Color COR_NAO_CONTEM = new Color(210, 30, 30);
    private static final Color COR_MEDIA      = new Color(0, 80, 210);

    public GraficoCobertura(
            ArrayList<SimulacaoIntervalosConfianca.ResultadoIC> resultados,
            double mediaPop) {
        this.resultados = resultados;
        this.mediaPop   = mediaPop;
        setPreferredSize(new Dimension(W, H));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int wu = W - ML - MR;
        int hu = H - MT - MB;
        int n  = resultados.size();

        // ── Limites do eixo X ─────────────────────────────────────────────
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        for (SimulacaoIntervalosConfianca.ResultadoIC r : resultados) {
            if (r.limiteInferior < minX) minX = r.limiteInferior;
            if (r.limiteSuperior > maxX) maxX = r.limiteSuperior;
        }
        double span = maxX - minX;
        minX -= span * 0.05;
        maxX += span * 0.05;
        span = maxX - minX;

        double sx = wu / span;
        double sy = (double) hu / n;

        // ── Título ────────────────────────────────────────────────────────
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.BLACK);
        g2.drawString("Simulação de 100 Intervalos de Confiança (95%) — Grupo 2", ML, 22);

        // legenda
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(COR_CONTEM);
        g2.fillRect(ML, 34, 14, 10);
        g2.setColor(Color.BLACK);
        g2.drawString("IC contém µ", ML + 18, 44);

        g2.setColor(COR_NAO_CONTEM);
        g2.fillRect(ML + 120, 34, 14, 10);
        g2.setColor(Color.BLACK);
        g2.drawString("IC não contém µ", ML + 138, 44);

        g2.setColor(COR_MEDIA);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{5f, 3f}, 0f));
        g2.drawLine(ML + 280, 39, ML + 302, 39);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(Color.BLACK);
        g2.drawString("µ real", ML + 306, 44);

        // ── Intervalos de confiança ───────────────────────────────────────
        for (int i = 0; i < n; i++) {
            SimulacaoIntervalosConfianca.ResultadoIC r = resultados.get(i);
            int y  = MT + (int)((i + 0.5) * sy);
            int x1 = ML + (int)((r.limiteInferior - minX) * sx);
            int x2 = ML + (int)((r.limiteSuperior - minX) * sx);

            g2.setColor(r.contemMedia ? COR_CONTEM : COR_NAO_CONTEM);
            g2.setStroke(new BasicStroke(r.contemMedia ? 1.2f : 1.8f));
            g2.drawLine(x1, y, x2, y);
            g2.drawLine(x1, y - 3, x1, y + 3);
            g2.drawLine(x2, y - 3, x2, y + 3);
        }

        // ── Linha da média populacional ───────────────────────────────────
        int xMu = ML + (int)((mediaPop - minX) * sx);
        g2.setColor(COR_MEDIA);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{6f, 4f}, 0f));
        g2.drawLine(xMu, MT, xMu, MT + hu);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString(String.format("µ=%.2f", mediaPop), xMu + 4, MT + 14);

        // ── Eixos ─────────────────────────────────────────────────────────
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.2f));
        int axY = MT + hu + 10;
        g2.drawLine(ML, axY, ML + wu, axY);  // eixo X
        g2.drawLine(ML, MT, ML, MT + hu);    // eixo Y

        // marcações eixo X
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= 8; i++) {
            double val = minX + (i * span / 8.0);
            int x = ML + (int)(i * wu / 8.0);
            g2.drawLine(x, axY, x, axY + 4);
            String label = String.format("%.1f", val);
            g2.drawString(label, x - (label.length() * 3), axY + 16);
        }
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.drawString("Média amostral (x\u0304)", ML + wu / 2 - 50, axY + 32);

        // marcações eixo Y (a cada 10 ICs)
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= 10; i++) {
            int idx = i * 10;
            int y = MT + (int)(idx * sy);
            g2.drawLine(ML - 4, y, ML, y);
            g2.drawString(String.valueOf(idx), ML - 28, y + 4);
        }

        // rótulo eixo Y (rotacionado)
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.rotate(-Math.PI / 2, ML - 52, MT + hu / 2);
        g2r.setFont(new Font("Arial", Font.PLAIN, 11));
        g2r.drawString("Nº do intervalo", ML - 52, MT + hu / 2);
        g2r.dispose();

        // ── Taxa de cobertura ─────────────────────────────────────────────
        int contem = 0;
        for (SimulacaoIntervalosConfianca.ResultadoIC r : resultados) if (r.contemMedia) contem++;
        double taxa = contem * 100.0 / n;

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.setColor(taxa >= 88 ? new Color(0, 110, 0) : Color.RED);
        g2.drawString(
            String.format("Taxa de cobertura: %d/%d = %.1f%%   (esperado ≈ 95%%)", contem, n, taxa),
            ML, H - 18
        );
    }

    /** Abre a janela do gráfico. */
    public static void exibir(
            ArrayList<SimulacaoIntervalosConfianca.ResultadoIC> resultados,
            double mediaPop, String titulo) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(titulo);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new GraficoCobertura(resultados, mediaPop));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

