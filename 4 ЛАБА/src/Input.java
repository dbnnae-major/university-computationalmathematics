import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Input {
    public static int readIntInRange(Scanner scanner, String promt, int min, int max) {
        int value;
        while (true) {
            System.out.println(promt);
            try {
                value = scanner.nextInt();
                if (value < min || value > max) {
                    System.out.printf("Значение должно быть в диапозоне %d и %d.%n", min, max);
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Число не верного формата (целое число). Попробуйте еще раз");
                scanner.next();
            }
        }
        return value;
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: это не число. Попробуйте снова.");
                sc.next();
            }
        }
    }

    public static List<Point> readPoints(Scanner scanner ,List<Point> points, int n) {

        for (int i = 1; i <= n; i++) {
            System.out.printf("Точка %d (введите x y): \n", i);
            double x = readDouble(scanner, "x = ");
            double y = readDouble(scanner, "y = ");
            points.add(new Point(x, y));
        }
        return points;
    }
}
