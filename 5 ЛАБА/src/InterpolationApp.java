import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.DoubleFunction;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.Color;

public class InterpolationApp {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        // === 1–3. Ввод данных ===
        double[] xs = null, ys = null;
        DoubleFunction<Double> trueFunc = null;
        System.out.println("Выберите способ ввода данных:");
        System.out.println(" 1) с клавиатуры");
        System.out.println(" 2) из файла");
        System.out.println(" 3) по функции (sin, x^2, exp)");
        System.out.print("Ваш выбор (1–3): ");
        int mode = sc.nextInt();
        int n;

        switch (mode) {
            case 1:
                System.out.print("Введите число узлов n ≥ 2: ");
                n = sc.nextInt();
                validateN(n);
                xs = new double[n];
                ys = new double[n];
                for (int i = 0; i < n; i++) {
                    System.out.printf("x[%d]= ", i);
                    xs[i] = sc.nextDouble();
                    System.out.printf("y[%d]= ", i);
                    ys[i] = sc.nextDouble();
                }
                break;
            case 2:
                String[] files = {"input1.txt", "input2.txt", "input3.txt"};
                System.out.println("Выберите файл:");
                for (int i = 0; i < files.length; i++)
                    System.out.printf(" %d) %s%n", i + 1, files[i]);
                System.out.print("Ваш выбор (1–3): ");
                int fi = sc.nextInt() - 1;
                if (fi < 0 || fi >= files.length) {
                    System.err.println("Неверный номер файла");
                    sc.close();
                    return;
                }
                try {
                    double[][] data = readFromFile(files[fi]);
                    xs = data[0];
                    ys = data[1];
                    n = xs.length;
                    validateN(n);
                } catch (IOException ex) {
                    System.err.println("Ошибка чтения файла: " + ex.getMessage());
                    sc.close();
                    return;
                }
                break;
            case 3:
                System.out.println("1) sin(x)\n2) x^2\n3) exp(x)");
                System.out.print("Выбор (1–3): ");
                int fmode = sc.nextInt();
                switch (fmode) {
                    case 1:
                        trueFunc = Math::sin;
                        break;
                    case 2:
                        trueFunc = x -> x * x;
                        break;
                    case 3:
                        trueFunc = Math::exp;
                        break;
                    default:
                        System.err.println("Неверный номер функции");
                        sc.close();
                        return;
                }
                System.out.print("Введите a, b: ");
                double a = sc.nextDouble(), b = sc.nextDouble();
                System.out.print("Введите число точек n ≥ 2: ");
                n = sc.nextInt();
                validateN(n);
                xs = new double[n];
                ys = new double[n];
                for (int i = 0; i < n; i++) {
                    xs[i] = a + i * (b - a) / (n - 1);
                    ys[i] = trueFunc.apply(xs[i]);
                }
                break;
            default:
                System.err.println("Неверный режим");
                sc.close();
                return;
        }
        // проверка дублей x
        for (int i = 0; i < xs.length; i++)
            for (int j = i + 1; j < xs.length; j++)
                if (xs[i] == xs[j])
                    throw new IllegalArgumentException("Дублирующийся x[" + i + "]=" + xs[i]);

        // === 2. Таблица конечных разностей ===
        System.out.println("\nИсходные данные:");
        for (int i = 0; i < xs.length; i++)
            System.out.printf("%3d: x=%.5f, y=%.5f%n", i, xs[i], ys[i]);

        // Разделенный
        double[][] f = buildDividedDifferences(xs, ys);
        printDividedDifferencesTable(xs, f);


        double[][] delta = buildFiniteDifferences(xs, ys);
        System.out.println("\nТаблица конечных разностей:");
        System.out.print(" i   x_i       y_i    ");
        for (int k = 1; k < xs.length; k++)
            System.out.printf("Δ^%-2d y   ", k);
        System.out.println();
        for (int i = 0; i < xs.length; i++) {
            System.out.printf("%2d %8.5f %8.5f ", i, xs[i], ys[i]);
            for (int k = 1; k + i < xs.length; k++)
                System.out.printf("%8.5f ", delta[i][k]);
            System.out.println();
        }

        // === 3. Интерполяция и обработка ошибок ===
        System.out.print("\nВведите X для интерполяции: ");
        double X = sc.nextDouble();

//        boolean isUniform = true;
//        double h = xs[1] - xs[0];
//        for (int i = 2; i < xs.length; i++) {
//            if (Math.abs((xs[i] - xs[i-1]) - h) > 1e-9) {
//                isUniform = false;
//                break;
//            }
//        }
//
//        double result;
//        String methodName;
//        String usedDiffs;  // сюда мы соберём распечатку разностей
//
//        if (!isUniform) {
//            // сетка неравномерная → Newton-divided
//            methodName = "Newton (разделённые разности)";
//            result = newtonDivided(X, xs, ys, true);
//        } else {
//            // равномерная сетка → Newton-finite
//            methodName = "Newton (конечные разности)";
//            result = newtonFinite(X, xs, ys, true);
//        }
//
//        System.out.printf("%s: %.7f%n", methodName, result);

//        String lagOut, divOut, finOut;
//        try {
//            lagOut = String.format("%.7f", lagrange(X, xs, ys, true));
//        } catch (Exception ex) {
//            lagOut = "Ошибка: " + ex.getMessage();
//        }
//        try {
//            divOut = String.format("%.7f", newtonDivided(X, xs, ys, true));
//        } catch (Exception ex) {
//            divOut = "Ошибка: " + ex.getMessage();
//        }
//        try {
//            finOut = String.format("%.7f", newtonFinite(X, xs, ys, true));
//        } catch (Exception ex) {
//            finOut = "Ошибка: " + ex.getMessage();
//        }
//
//        System.out.printf("Lagrange:        %s%n", lagOut);
//        System.out.printf("Newton-divided:  %s%n", divOut);
//        System.out.printf("Newton-finite:   %s%n", finOut);

        // Lagrange и разделённый Ньютона для сравнения
        double lag = lagrange(X, xs, ys, true);

        double h = xs[1] - xs[0];
        double tFwd  = Math.abs((X - xs[0])    / h);
        double tBack = Math.abs((X - xs[xs.length-1]) / h);

        boolean useBackward = tBack < tFwd;
        double result;
        String methodName;

        if (useBackward) {
            methodName = "Newton–назад";
            result = newtonFiniteBackward(X, xs, ys, true);
        } else {
            methodName = "Newton–вперёд";
            result = newtonFinite (X, xs, ys, true);
        }

        double div = newtonDivided(X, xs, ys, true);

        System.out.printf("%s:         %.7f%n", methodName, result);
        System.out.printf("Newton–раздел.: %.7f%n", div);
        System.out.printf("Lagrange:       %.7f%n", lag);

        // === 4. Построение графиков доступных методов ===
        plotAllSeparate(xs, ys, trueFunc);


        plotAllTogether(xs, ys, trueFunc);
        sc.close();
    }

    private static void validateN(int n) {
        if (n < 2) throw new IllegalArgumentException("Нужно ≥2 точек, задано: " + n);
    }

    private static double[][] readFromFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String[] xsStr = br.readLine().trim().split("\\s+");
        String[] ysStr = br.readLine().trim().split("\\s+");
        br.close();
        if (xsStr.length != ysStr.length)
            throw new IOException("В файле разное число X и Y");
        int n = xsStr.length;
        double[] xs = new double[n], ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = Double.parseDouble(xsStr[i]);
            ys[i] = Double.parseDouble(ysStr[i]);
        }
        return new double[][]{xs, ys};
    }

    private static double[][] buildFiniteDifferences(double[] xs, double[] ys) {
        int n = xs.length;
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) d[i][0] = ys[i];
        for (int k = 1; k < n; k++)
            for (int i = 0; i + k < n; i++)
                d[i][k] = d[i + 1][k - 1] - d[i][k - 1];
        return d;
    }

    public static double newtonFinite(double x, double[] xs, double[] ys, boolean verbose) {
        int n = xs.length;
        double h = xs[1] - xs[0];
        for (int i = 2; i < n; i++)
            if (Math.abs((xs[i] - xs[i - 1]) - h) > 1e-9)
                throw new IllegalArgumentException("Шаг нерегулярен");

        double[][] d = buildFiniteDifferences(xs, ys);
        double t = (x - xs[0]) / h, res = d[0][0], tk = 1;

        if (verbose) {
            System.out.printf("\nНьютон-вперед (конечные):");
            System.out.printf("t = %.5f%n", t);
            System.out.println("Используемые конечные разности:");
            System.out.printf("Δ^0 y[0] = %.7f%n", d[0][0]);
        }

        for (int k = 1; k < n; k++) {
            tk *= (t - (k - 1));
            double term = tk / factorial(k) * d[0][k];
            res += term;
            if (verbose) {
                System.out.printf("Δ^%-2d y[0] = %.7f, множитель = %.5f, факториал = %d%n",
                        k, d[0][k], tk, factorial(k));
            }
        }


        if (verbose) {
            System.out.println("\nФормула Ньютона-вперед с конечными разностями:");
            System.out.println(buildNewtonFiniteFormula(n, ys[0]));
        }

        return res;
    }

    public static double newtonFiniteBackward(double x,
                                              double[] xs, double[] ys, boolean verbose) {
        int n = xs.length;
        double h = xs[1] - xs[0];
        // проверка равномерности шага
        for (int i = 2; i < n; i++) {
            if (Math.abs((xs[i] - xs[i - 1]) - h) > 1e-9)
                throw new IllegalArgumentException("Шаг нерегулярен");
        }

        double[][] d = buildFiniteDifferences(xs, ys);
        double t = (x - xs[n-1]) / h;
        double res = d[n-1][0];
        double tk = 1;

        if (verbose) {
            System.out.printf("\nНьютон-назад (конечные):%n");
            System.out.printf("t = %.5f%n", t);
            System.out.println("Используемые конечные разности:");
            System.out.printf("Δ^0 y[%d] = %.7f%n", n-1, d[n-1][0]);
        }
        for (int k = 1; k < n; k++) {
            tk *= (t + (k - 1));
            double diff = d[n-1-k][k];
            double term = tk / factorial(k) * diff;
            res += term;
            if (verbose) {
                System.out.printf("Δ^%d y[%d] = %.7f, множитель = %.5f, факториал = %d%n",
                        k, n-1-k, diff, tk, factorial(k));
            }
        }

        if (verbose) {
            System.out.println("\nФормула Ньютона-назад с конечными разностями:");
            System.out.println(buildNewtonFiniteBackwardFormula(n, ys[n-1]));
        }

        return res;
    }

    private static String buildNewtonFiniteBackwardFormula(int n, double yn) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("P(x) ≈ %.5f", yn));
        String tProd = "";
        // начинаем с Δ^1 y[n-2] и множителя (t)(t+1)
        for (int k = 1; k < n; k++) {
            // для k-го члена пишем t*(t+1)*...*(t+k-1)
            if (k == 1) {
                tProd = "t";
            } else {
                tProd += String.format("*(t+%d)", k-1);
            }
            sb.append(String.format(" + %s / %d! * Δ^%dy[%d]",
                    tProd, k, k, n-1-k));
        }
        return sb.toString();
    }

//    public static double newtonFinite(double x, double[] xs, double[] ys, boolean verbose) {
//        int n = xs.length;
//        double h = xs[1] - xs[0];
//        for (int i = 2; i < n; i++)
//            if (Math.abs((xs[i] - xs[i - 1]) - h) > 1e-9)
//                throw new IllegalArgumentException("Шаг нерегулярен");
//
//        // Строим таблицу конечных разностей
//        double[][] d = buildFiniteDifferences(xs, ys);
//
//        // Находим ближайший индекс i, такой что xs[i] <= x <= xs[i+1]
//        int i = 0;
//        while (i + 1 < n && x > xs[i + 1]) i++;
//        if (i >= n) i = n - 1;
//
//        double t = (x - xs[i]) / h;
//        double res = d[i][0], tk = 1;
//
//        if (verbose) {
//            System.out.printf("\nНьютон (конечные, с i=%d)\n", i);
//            System.out.printf("t = %.5f%n", t);
//            System.out.println("Используемые конечные разности:");
//            System.out.printf("Δ^0 y[%d] = %.7f%n", i, d[i][0]);
//        }
//
//        for (int k = 1; k < n - i; k++) {
//            tk *= (t - (k - 1));
//            double term = tk / factorial(k) * d[i][k];
//            res += term;
//            if (verbose) {
//                System.out.printf("Δ^%-2d y[%d] = %.7f, множитель = %.5f, факториал = %d%n",
//                        k, i, d[i][k], tk, factorial(k));
//            }
//        }
//
//        if (verbose) {
//            System.out.println("\nФормула Ньютона с конечными разностями:");
//            System.out.println(buildNewtonFiniteFormulaCentered(n - i, ys[i], i));
//        }
//
//        return res;
//    }

private static String buildNewtonFiniteFormulaCentered(int terms, double yi, int i) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("P(x) ≈ y[%d] = %.5f", i, yi));
    String tProd = "";
    for (int k = 1; k < terms; k++) {
        tProd += String.format("*(t-%d)", k - 1);
        sb.append(String.format(" + %s / %d! * Δ^%dy[%d]", tProd, k, k, i));
    }
    return sb.toString();
}
    public static double newtonDivided(double x, double[] xs, double[] ys, boolean verbose) {
        int n = xs.length;
        double[][] f = new double[n][n];
        for (int i = 0; i < n; i++) f[i][0] = ys[i];
        for (int j = 1; j < n; j++)
            for (int i = 0; i + j < n; i++)
                f[i][j] = (f[i + 1][j - 1] - f[i][j - 1]) / (xs[i + j] - xs[i]);

        double res = f[0][0], prod = 1;
        if (verbose) {
            System.out.printf("\nНьютон (разделённые):");
            System.out.println("Используемые разделённые разности:");
            System.out.printf("f[x0] = %.7f%n", f[0][0]);
        }
        for (int j = 1; j < n; j++) {
            prod *= (x - xs[j - 1]);
            res += f[0][j] * prod;
            if (verbose) {
                System.out.printf("f[x0,..,x%d] = %.7f, множитель = %.5f%n",
                        j, f[0][j], prod);
            }
        }
        if (verbose) {
            System.out.println("\nФормула Ньютона с разделёнными разностями:");
            System.out.println(buildNewtonDividedFormula(xs));
        }
        return res;
    }

    public static double lagrange(double x, double[] xs, double[] ys, boolean verbose) {
        int n = xs.length;
        double res = 0;
        if (verbose) System.out.println("\nЛагранж:");
        for (int i = 0; i < n; i++) {
            double term = ys[i];
            if (verbose) System.out.printf("L_%d(x): ", i);
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    term *= (x - xs[j]) / (xs[i] - xs[j]);
                    if (verbose) System.out.printf("((x - %.5f)/(%.5f - %.5f)) ", xs[j], xs[i], xs[j]);
                }
            }
            res += term;
            if (verbose) {
                System.out.printf(" * %.5f +\n", ys[i]);
            }
        }
        if (verbose) {
            System.out.print("L(x) = ");
        }
        for (int i = 0; i < n; i++) {
            if (verbose) {
                if (i == n-1) {
                    System.out.printf(" L_%d(x) = ", i);
                }
                else {
                    System.out.printf("L_%d(x) + ", i);
                }
            }
        }
        if (verbose) System.out.printf("%.7f\n", res);
        return res;
    }

    private static String buildNewtonFiniteFormula(int n, double y0) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("P(x) ≈ %.5f", y0));
        String tProd = "";
        for (int k = 1; k < n; k++) {
            tProd += String.format("*(t-%d)", k - 1);
            sb.append(String.format(" + %s / %d! * Δ^%dy₀", tProd, k, k));
        }
        return sb.toString();
    }

    private static String buildNewtonDividedFormula(double[] xs) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("P(x) ≈ f[x0]"));
        String prod = "";
        for (int i = 1; i < xs.length; i++) {
            prod += String.format("*(x - %.5f)", xs[i - 1]);
            sb.append(String.format(" + %s * f[x0,..,x%d]", prod, i));
        }
        return sb.toString();
    }

    private static long factorial(int k) {
        long f = 1;
        for (int i = 2; i <= k; i++) f *= i;
        return f;
    }

    private static double[][] buildDividedDifferences(double[] xs, double[] ys) {
        int n = xs.length;
        double[][] f = new double[n][n];
        for (int i = 0; i < n; i++) {
            f[i][0] = ys[i];
        }
        for (int j = 1; j < n; j++) {
            for (int i = 0; i + j < n; i++) {
                f[i][j] = (f[i+1][j-1] - f[i][j-1]) / (xs[i+j] - xs[i]);
            }
        }
        return f;
    }
    private static void printDividedDifferencesTable(double[] xs, double[][] f) {
        int n = xs.length;
        System.out.println("\nТаблица разделённых разностей:");

        System.out.print(" i     x_i      f[x_i]   ");
        for (int j = 1; j < n; j++) {
            System.out.printf(" f[%d…%d]   ", 0, j);
        }
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.printf("%2d %10.5f %10.5f  ", i, xs[i], f[i][0]);
            for (int j = 1; j + i < n; j++) {
                System.out.printf("%10.5f  ", f[i][j]);
            }
            System.out.println();
        }
    }

    private static void plotAllSeparate(double[] xs, double[] ys,
                                        DoubleFunction<Double> trueFunc) {

        int m = 200;
        double min = xs[0], max = xs[xs.length - 1];
        double[] xg = new double[m], trueData = new double[m];
        double[] lagData = new double[m], divData = new double[m], finData = new double[m];

        // Попытаться заполнить данные, отлавливая исключения
        boolean okLag = true, okDiv = true, okFin = true;
        for (int i = 0; i < m; i++) {
            xg[i] = min + (max - min) * i / (m - 1);
            lagData[i] = lagrange(xg[i], xs, ys, false);
            try {
                divData[i] = newtonDivided(xg[i], xs, ys, false);
            } catch (Exception e) {
                okDiv = false;
            }
            try {
                finData[i] = newtonFinite(xg[i], xs, ys, false);
            } catch (Exception e) {
                okFin = false;
            }
            trueData[i] = (trueFunc != null ? trueFunc.apply(xg[i]) : Double.NaN);
        }

        // Общий метод для рисования одной серии
        class Drawer {
            void draw(String title, double[] data, boolean ok) {
                if (!ok) return;
                XYChart c = new XYChart(600, 400);
                c.setTitle(title);
                c.setXAxisTitle("X");
                c.setYAxisTitle("Y");
                if (trueFunc != null)
                    c.addSeries("Истинная f", xg, trueData)
                            .setLineStyle(SeriesLines.SOLID);
                c.addSeries(title, xg, data)
                        .setLineStyle(SeriesLines.SOLID);
                XYSeries nodes = c.addSeries("Узлы", xs, ys);
                nodes.setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
                nodes.setMarker(SeriesMarkers.CIRCLE);
                new SwingWrapper<>(c).displayChart();
            }
        }

        Drawer drawer = new Drawer();
        drawer.draw("Лагранж", lagData, okLag);
        drawer.draw("Ньютон-div", divData, okDiv);
        drawer.draw("Ньютон-fin", finData, okFin);
    }

    private static void plotAllTogether(double[] xs, double[] ys, DoubleFunction<Double> trueFunc) {
        int m = 200;
        double min = xs[0], max = xs[xs.length - 1];
        double[] xg = new double[m], trueData = new double[m];
        double[] lagData = new double[m], divData = new double[m], finData = new double[m];

        boolean okLag = true, okDiv = true, okFin = true;
        for (int i = 0; i < m; i++) {
            xg[i] = min + (max - min) * i / (m - 1);
            lagData[i] = lagrange(xg[i], xs, ys, false);
            try {
                divData[i] = newtonDivided(xg[i], xs, ys, false);
            } catch (Exception e) {
                okDiv = false;
            }
            try {
                finData[i] = newtonFinite(xg[i], xs, ys, false);
            } catch (Exception e) {
                okFin = false;
            }
            trueData[i] = (trueFunc != null ? trueFunc.apply(xg[i]) : Double.NaN);
        }

        XYChart chart = new XYChart(800, 600);
        chart.setTitle("Сравнение интерполяций");
        chart.setXAxisTitle("X");
        chart.setYAxisTitle("Y");

        if (trueFunc != null)
            chart.addSeries("Истинная f(x)", xg, trueData)
                    .setXYSeriesRenderStyle(XYSeriesRenderStyle.Line)
                    .setLineColor(Color.BLACK);

        if (okLag)
            chart.addSeries("Лагранж", xg, lagData)
                    .setXYSeriesRenderStyle(XYSeriesRenderStyle.Line)
                    .setLineColor(Color.RED);

        if (okDiv)
            chart.addSeries("Ньютон (разделённые)", xg, divData)
                    .setXYSeriesRenderStyle(XYSeriesRenderStyle.Line)
                    .setLineColor(Color.BLUE);

        if (okFin)
            chart.addSeries("Ньютон (конечные)", xg, finData)
                    .setXYSeriesRenderStyle(XYSeriesRenderStyle.Line)
                    .setLineColor(Color.GREEN);

        XYSeries nodes = chart.addSeries("Узлы", xs, ys);
        nodes.setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        nodes.setLineColor(Color.ORANGE);

        new SwingWrapper<>(chart).displayChart();
    }

}
