import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphPlotter {
    public static void plot(List<Point> points,
                            Map<String, List<Point>> approximations,
                            Set<String> selected) {
        // 1) Собираем наборы данных и находим экстремумы
        XYSeriesCollection dataset = new XYSeriesCollection();

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

        // 1.1) Исходные точки
        XYSeries raw = new XYSeries("Исходные точки");
        for (Point p : points) {
            raw.add(p.x, p.y);
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }
        dataset.addSeries(raw);

        // 1.2) Аппроксимации
        for (String name : selected) {
            XYSeries series = new XYSeries(name);
            for (Point p : approximations.get(name)) {
                series.add(p.x, p.y);
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            dataset.addSeries(series);
        }

        // 2) Создаём график
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Аппроксимации",
                "x", "y",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // 3) Настраиваем renderer
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        // 3.1) Исходные точки — только маркеры
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);

        // 3.2) Аппроксимации — только линии
        for (int i = 1; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesLinesVisible(i, true);
            renderer.setSeriesShapesVisible(i, false);
        }

        // 4) Даем запас в 2 единицы вокруг экстремумов
        double xLower = minX - 2, xUpper = maxX + 2;
        double yLower = minY - 2, yUpper = maxY + 2;

        plot.getDomainAxis().setRange(xLower, xUpper);
        plot.getRangeAxis().setRange(yLower, yUpper);

        // 5) Включаем оси и сетку
        plot.getDomainAxis().setAxisLineVisible(true);
        plot.getRangeAxis().setAxisLineVisible(true);
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(true);
        plot.setDomainZeroBaselinePaint(Color.BLACK);
        plot.setRangeZeroBaselinePaint(Color.BLACK);
        plot.setDomainZeroBaselineStroke(new BasicStroke(1.5f));
        plot.setRangeZeroBaselineStroke(new BasicStroke(1.5f));
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);

        // 6) Отображаем в окне
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Графики аппроксимаций");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 600));
            frame.add(chartPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
