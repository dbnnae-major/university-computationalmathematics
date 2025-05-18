import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

public class Main {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ввода данных с консоли, Ввод данных с файла - 2");
        if (scanner.next().equals("2")) {
            try (Scanner fileScanner = new Scanner(new File("src/input.txt"))) {
                fileScanner.useLocale(Locale.US);
                int n = fileScanner.nextInt();

                if (n < 8 || n > 12) {
                    System.out.println("Ошибка: n должно быть в диапазоне от 8 до 12, а найдено: " + n);
                    return;
                }

                List<Point> points = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    double x = fileScanner.nextDouble();
                    double y = fileScanner.nextDouble();
                    points.add(new Point(x, y));
                }

                // ЛИНЕЙНАЯ АППРОКСИМАЦИЯ
                double d1 = linearApproximation(scanner, points, n);

                // ПОЛИНОМИАЛЬНАЯ ФУНКЦИЯ 2-Й СТЕПЕНИ
                double d2 = QuadraticApproximation(scanner, points, n);

                // ПОЛИНОМИАЛЬНАЯ ФУНКЦИЯ 3-Й СТЕПЕНИ
                double d3 = CubicApproximation(scanner, points, n);

                // ЭКСПОНЕНЦИАЛЬНАЯ ФУНКЦИЯ
                double d4 = ExponentialApproximation(scanner, points, n);

                // СТЕПЕННАЯ ФУНКЦИЯ
                double d5 = PowerApproximation(scanner, points, n);

                // ЛОГАРИФМИЧЕСКАЯ ФУНКЦИЯ
                double d6 = LogarithmicApproximation(scanner, points, n);

                double[] ds = { d1, d2, d3, d4, d5, d6 };

                int BestIdx = chooseBestApproximation(ds);

                Map<String, List<Point>> approximations = new LinkedHashMap<>();
                approximations.put("Линейная",    LinearApproximation.calculatePoints(points, n, LinearApproximation.calculateCoefficients(points, n)));
                approximations.put("Полином2",     QuadraticApproximation.calculatePoints(points, n, QuadraticApproximation.calculateCoefficients(points, n)));
                approximations.put("Полином3",     CubicApproximation.calculatePoints(points, n, CubicApproximation.calculateCoefficients(points, n)));
                approximations.put("Экспоненциальная", ExponentialApproximation.calculatePoints(points, n, ExponentialApproximation.calculateCoefficients(points, n)));
                approximations.put("Степенная",    PowerApproximation.calculatePoints(points, n, PowerApproximation.calculateCoefficients(points, n)));
                approximations.put("Логарифмическая", LogarithmicApproximation.calculatePoints(points, n, LogarithmicApproximation.calculateCoefficients(points, n)));

                System.out.println("Выберите какие графики построить:");
                String[] keys = approximations.keySet().toArray(new String[0]);
                for (int i = 0; i < keys.length; i++) {
                    System.out.printf("%d — %s%n", i+1, keys[i]);
                }
                System.out.println("Можно ввести несколько через пробел, например: 1 3 5");

                scanner.nextLine();
                String line = scanner.nextLine().trim();
                String[] parts = line.split("\\s+");
                Set<String> selected = new LinkedHashSet<>();
                for (String p : parts) {
                    try {
                        int idx = Integer.parseInt(p) - 1;
                        if (idx >= 0 && idx < keys.length) {
                            selected.add(keys[idx]);
                        }
                    } catch (Exception ignored) {}
                }

                GraphPlotter.plot(points, approximations, selected);

            } catch (FileNotFoundException e) {
                System.out.println("Неверно заполненный файл.");

            }
        } else {
            int n = Input.readIntInRange(scanner, "Введите n (количество точек от 8 до 12):", 8, 12);
            List<Point> points = new ArrayList<>(n);
            points = Input.readPoints(scanner, points, n);

            // ЛИНЕЙНАЯ АППРОКСИМАЦИЯ
            double d1 = linearApproximation(scanner, points, n);

            // ПОЛИНОМИАЛЬНАЯ ФУНКЦИЯ 2-Й СТЕПЕНИ
            double d2 = QuadraticApproximation(scanner, points, n);

            // ПОЛИНОМИАЛЬНАЯ ФУНКЦИЯ 3-Й СТЕПЕНИ
            double d3 = CubicApproximation(scanner, points, n);

            // ЭКСПОНЕНЦИАЛЬНАЯ ФУНКЦИЯ
            double d4 = ExponentialApproximation(scanner, points, n);

            // СТЕПЕННАЯ ФУНКЦИЯ
            double d5 = PowerApproximation(scanner, points, n);

            // ЛОГАРИФМИЧЕСКАЯ ФУНКЦИЯ
            double d6 = LogarithmicApproximation(scanner, points, n);

            double[] ds = { d1, d2, d3, d4, d5, d6 };

            int BestIdx = chooseBestApproximation(ds);

            Map<String, List<Point>> approximations = new LinkedHashMap<>();
            approximations.put("Линейная",    LinearApproximation.calculatePoints(points, n, LinearApproximation.calculateCoefficients(points, n)));
            approximations.put("Полином2",     QuadraticApproximation.calculatePoints(points, n, QuadraticApproximation.calculateCoefficients(points, n)));
            approximations.put("Полином3",     CubicApproximation.calculatePoints(points, n, CubicApproximation.calculateCoefficients(points, n)));
            approximations.put("Экспоненциальная", ExponentialApproximation.calculatePoints(points, n, ExponentialApproximation.calculateCoefficients(points, n)));
            approximations.put("Степенная",    PowerApproximation.calculatePoints(points, n, PowerApproximation.calculateCoefficients(points, n)));
            approximations.put("Логарифмическая", LogarithmicApproximation.calculatePoints(points, n, LogarithmicApproximation.calculateCoefficients(points, n)));

            System.out.println("Выберите какие графики построить:");
            String[] keys = approximations.keySet().toArray(new String[0]);
            for (int i = 0; i < keys.length; i++) {
                System.out.printf("%d — %s%n", i+1, keys[i]);
            }
            System.out.println("Можно ввести несколько через пробел, например: 1 3 5");

            scanner.nextLine();
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            Set<String> selected = new LinkedHashSet<>();
            for (String p : parts) {
                try {
                    int idx = Integer.parseInt(p) - 1;
                    if (idx >= 0 && idx < keys.length) {
                        selected.add(keys[idx]);
                    }
                } catch (Exception ignored) {}
            }

            GraphPlotter.plot(points, approximations, selected);

        }
    }

    public static int chooseBestApproximation(double[] ds) {
        String[] names = {
                "Линейная аппроксимация",
                "Полином 2-й степени",
                "Полином 3-й степени",
                "Экспоненциальная функция",
                "Степенная функция",
                "Логарифмическая функция"
        };

        int bestIdx = 0;
        double bestD = ds[0];
        for (int i = 1; i < ds.length; i++) {
            if (ds[i] < bestD) {
                bestD = ds[i];
                bestIdx = i;
            }
        }

        System.out.printf("Лучшее приближение: %s (d = %.3f)%n",
                names[bestIdx], bestD);

        return bestIdx;
    }

    public static double LogarithmicApproximation(Scanner scanner, List<Point> points, int n) {
        List<Double> log_coeffs = LogarithmicApproximation.calculateCoefficients(points, n);
        if (LogarithmicApproximation.printFunction(log_coeffs).equals("Error")) {
            System.out.println("Невозможно аппроксимировать: недостаточный разброс значений x / y < 0");
        } else {
            List<Point> log_phi_points = LogarithmicApproximation.calculatePoints(points, n, log_coeffs);
            List<Double> log_delts = LogarithmicApproximation.calculateDelta(points, n, log_phi_points);
            System.out.println("Логарифмическая функция: " + LogarithmicApproximation.printFunction(log_coeffs));
            System.out.printf("Среднеквадратичное отклонение: %.3f \n", log_delts.get(n));
            System.out.printf("Мера отклонения: %.3f \n", log_delts.get(n + 1));
            double log_R = log_delts.get(n + 2);
            System.out.printf("Коэффициент детерминации: %.3f%n", log_R);

            String quality;
            if (log_R >= 0.95) {
                quality = "Высокая точность аппроксимации (модель хорошо описывает явление)";
            } else if (log_R >= 0.75) {
                quality = "Удовлетворительная аппроксимация (модель адекватно описывает явление)";
            } else if (log_R >= 0.5) {
                quality = "Слабая аппроксимация (модель слабо описывает явление)";
            } else {
                quality = "Точность аппроксимации недостаточна — модель требует изменения";
            }

            System.out.println(quality);
            System.out.println("Вывести таблицу? 1 - да; 2 - нет");
            if (scanner.next().equals("1")) {
                System.out.println("x \t\t y \t\t p \t\t e");
                for (int i = 0; i < n; i++) {
                    double x = points.get(i).x;
                    double y = points.get(i).y;
                    double phi = log_phi_points.get(i).y;
                    double delta = log_delts.get(i);

                    System.out.printf("%.3f\t%.3f\t%.3f\t%.3f%n", x, y, phi, delta);
                }
            }
            return log_delts.get(n);
        }
        return 100000000;
    }

    public static double PowerApproximation(Scanner scanner, List<Point> points, int n) {
        List<Double> power_coeffs = PowerApproximation.calculateCoefficients(points, n);
        if (PowerApproximation.printFunction(power_coeffs).equals("Error")) {
            System.out.println("Невозможно аппроксимировать: недостаточный разброс значений x / y < 0");
        } else {
            List<Point> power_phi_points = PowerApproximation.calculatePoints(points, n, power_coeffs);
            List<Double> power_delts = PowerApproximation.calculateDelta(points, n, power_phi_points);
            System.out.println("Степенная функция: " + PowerApproximation.printFunction(power_coeffs));
            System.out.printf("Среднеквадратичное отклонение: %.3f \n", power_delts.get(n));
            System.out.printf("Мера отклонения: %.3f \n", power_delts.get(n + 1));
            double power_R = power_delts.get(n + 2);
            System.out.printf("Коэффициент детерминации: %.3f%n", power_R);

            String quality;
            if (power_R >= 0.95) {
                quality = "Высокая точность аппроксимации (модель хорошо описывает явление)";
            } else if (power_R >= 0.75) {
                quality = "Удовлетворительная аппроксимация (модель адекватно описывает явление)";
            } else if (power_R >= 0.5) {
                quality = "Слабая аппроксимация (модель слабо описывает явление)";
            } else {
                quality = "Точность аппроксимации недостаточна — модель требует изменения";
            }

            System.out.println(quality);
            System.out.println("Вывести таблицу? 1 - да; 2 - нет");
            if (scanner.next().equals("1")) {
                System.out.println("x \t\t y \t\t p \t\t e");
                for (int i = 0; i < n; i++) {
                    double x = points.get(i).x;
                    double y = points.get(i).y;
                    double phi = power_phi_points.get(i).y;
                    double delta = power_delts.get(i);

                    System.out.printf("%.3f\t%.3f\t%.3f\t%.3f%n", x, y, phi, delta);
                }
            }
            return power_delts.get(n);
        }
        return 100000000;
    }

    public static double ExponentialApproximation(Scanner scanner, List<Point> points, int n) {
        List<Double> expon_coeffs = ExponentialApproximation.calculateCoefficients(points, n);
        if (ExponentialApproximation.printFunction(expon_coeffs).equals("Error")) {
            System.out.println("Невозможно аппроксимировать: недостаточный разброс значений x / y < 0");
        } else {
            List<Point> expon_phi_points = ExponentialApproximation.calculatePoints(points, n, expon_coeffs);
            List<Double> expon_delts = ExponentialApproximation.calculateDelta(points, n, expon_phi_points);
            System.out.println("Экспоненциальная функция: " + ExponentialApproximation.printFunction(expon_coeffs));
            System.out.printf("Среднеквадратичное отклонение: %.3f \n", expon_delts.get(n));
            System.out.printf("Мера отклонения: %.3f \n", expon_delts.get(n + 1));
            double expon_R = expon_delts.get(n + 2);
            System.out.printf("Коэффициент детерминации: %.3f%n", expon_R);

            String quality;
            if (expon_R >= 0.95) {
                quality = "Высокая точность аппроксимации (модель хорошо описывает явление)";
            } else if (expon_R >= 0.75) {
                quality = "Удовлетворительная аппроксимация (модель адекватно описывает явление)";
            } else if (expon_R >= 0.5) {
                quality = "Слабая аппроксимация (модель слабо описывает явление)";
            } else {
                quality = "Точность аппроксимации недостаточна — модель требует изменения";
            }

            System.out.println(quality);

            System.out.println("Вывести таблицу? 1 - да; 2 - нет");
            if (scanner.next().equals("1")) {
                System.out.println("x \t\t y \t\t p \t\t e");
                for (int i = 0; i < n; i++) {
                    double x = points.get(i).x;
                    double y = points.get(i).y;
                    double phi = expon_phi_points.get(i).y;
                    double delta = expon_delts.get(i);

                    System.out.printf("%.3f\t%.3f\t%.3f\t%.3f%n", x, y, phi, delta);
                }
            }
            return expon_delts.get(n);
        }
        return 100000000;
    }

    public static double CubicApproximation(Scanner scanner, List<Point> points, int n) {
        List<Double> cubic_coeffs = CubicApproximation.calculateCoefficients(points, n);
        if (CubicApproximation.printFunction(cubic_coeffs).equals("Error")) {
            System.out.println("Невозможно аппроксимировать: недостаточный разброс значений x");
        } else {
            List<Point> cubic_phi_points = CubicApproximation.calculatePoints(points, n, cubic_coeffs);
            List<Double> cubic_delts = CubicApproximation.calculateDelta(points, n, cubic_phi_points);
            System.out.println("Полиномиальная функция 3-й степени: " + CubicApproximation.printFunction(cubic_coeffs));
            System.out.printf("Среднеквадратичное отклонение: %.3f \n", cubic_delts.get(n));
            System.out.printf("Мера отклонения: %.3f \n", cubic_delts.get(n + 1));
            double cubic_R = cubic_delts.get(n + 2);
            System.out.printf("Коэффициент детерминации: %.3f%n", cubic_R);

            String quality;
            if (cubic_R >= 0.95) {
                quality = "Высокая точность аппроксимации (модель хорошо описывает явление)";
            } else if (cubic_R >= 0.75) {
                quality = "Удовлетворительная аппроксимация (модель адекватно описывает явление)";
            } else if (cubic_R >= 0.5) {
                quality = "Слабая аппроксимация (модель слабо описывает явление)";
            } else {
                quality = "Точность аппроксимации недостаточна — модель требует изменения";
            }

            System.out.println(quality);

            System.out.println("Вывести таблицу? 1 - да; 2 - нет");
            if (scanner.next().equals("1")) {
                System.out.println("x \t\t y \t\t p \t\t e");
                for (int i = 0; i < n; i++) {
                    double x = points.get(i).x;
                    double y = points.get(i).y;
                    double phi = cubic_phi_points.get(i).y;
                    double delta = cubic_delts.get(i);

                    System.out.printf("%.3f\t%.3f\t%.3f\t%.3f%n", x, y, phi, delta);
                }
            }
            return cubic_delts.get(n);
        }
        return 100000000;
    }

    public static double QuadraticApproximation(Scanner scanner, List<Point> points, int n) {
        List<Double> quadratic_coeffs = QuadraticApproximation.calculateCoefficients(points, n);
        if (QuadraticApproximation.printFunction(quadratic_coeffs).equals("Error")) {
            System.out.println("Невозможно аппроксимировать: недостаточный разброс значений x");
        } else {
            List<Point> quadratic_phi_points = QuadraticApproximation.calculatePoints(points, n, quadratic_coeffs);
            List<Double> quadratic_delts = QuadraticApproximation.calculateDelta(points, n, quadratic_phi_points);
            System.out.println("Полиномиальная функция 2-й степени: " + QuadraticApproximation.printFunction(quadratic_coeffs));
            System.out.printf("Среднеквадратичное отклонение: %.3f \n", quadratic_delts.get(n));
            System.out.printf("Мера отклонения: %.3f \n", quadratic_delts.get(n + 1));
            double quadratic_R = quadratic_delts.get(n + 2);
            System.out.printf("Коэффициент детерминации: %.3f%n", quadratic_R);

            String quality;
            if (quadratic_R >= 0.95) {
                quality = "Высокая точность аппроксимации (модель хорошо описывает явление)";
            } else if (quadratic_R >= 0.75) {
                quality = "Удовлетворительная аппроксимация (модель адекватно описывает явление)";
            } else if (quadratic_R >= 0.5) {
                quality = "Слабая аппроксимация (модель слабо описывает явление)";
            } else {
                quality = "Точность аппроксимации недостаточна — модель требует изменения";
            }

            System.out.println(quality);

            System.out.println("Вывести таблицу? 1 - да; 2 - нет");
            if (scanner.next().equals("1")) {
                System.out.println("x \t\t y \t\t p \t\t e");
                for (int i = 0; i < n; i++) {
                    double x = points.get(i).x;
                    double y = points.get(i).y;
                    double phi = quadratic_phi_points.get(i).y;
                    double delta = quadratic_delts.get(i);

                    System.out.printf("%.3f\t%.3f\t%.3f\t%.3f%n", x, y, phi, delta);
                }
            }
            return quadratic_delts.get(n);
        }
        return 100000000;
    }

    public static double linearApproximation(Scanner scanner, List<Point> points, int n) {
        List<Double> linear_coeffs = LinearApproximation.calculateCoefficients(points, n);
        if (LinearApproximation.printFunction(linear_coeffs).equals("Error")) {
            System.out.println("Невозможно аппроксимировать: недостаточный разброс значений x");
        } else {
            List<Point> linear_phi_points = LinearApproximation.calculatePoints(points, n, linear_coeffs);
            List<Double> linear_delts = LinearApproximation.calculateDelta(points, n, linear_phi_points);
            System.out.println("Линейная аппроксимация: " + LinearApproximation.printFunction(linear_coeffs));
            System.out.printf("Среднеквадратичное отклонение: %.3f \n", linear_delts.get(n));
            System.out.printf("Мера отклонения: %.3f \n", linear_delts.get(n + 1));
            System.out.printf("Коэффициент корреляции: %.3f \n", linear_delts.get(n + 2));

            double linear_R = linear_delts.get(n + 3);
            System.out.printf("Коэффициент детерминации: %.3f%n", linear_R);

            String quality;
            if (linear_R >= 0.95) {
                quality = "Высокая точность аппроксимации (модель хорошо описывает явление)";
            } else if (linear_R >= 0.75) {
                quality = "Удовлетворительная аппроксимация (модель адекватно описывает явление)";
            } else if (linear_R >= 0.5) {
                quality = "Слабая аппроксимация (модель слабо описывает явление)";
            } else {
                quality = "Точность аппроксимации недостаточна — модель требует изменения";
            }

            System.out.println(quality);

            System.out.println("Вывести таблицу? 1 - да; 2 - нет");
            if (scanner.next().equals("1")) {
                System.out.println("x \t\t y \t\t p \t\t e");
                for (int i = 0; i < n; i++) {
                    double x = points.get(i).x;
                    double y = points.get(i).y;
                    double phi = linear_phi_points.get(i).y;
                    double delta = linear_delts.get(i);

                    System.out.printf("%.3f\t%.3f\t%.3f\t%.3f%n", x, y, phi, delta);
                }
            }
            return linear_delts.get(n);
        }
        return 100000000;
    }
}

