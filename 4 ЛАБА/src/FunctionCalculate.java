import java.util.List;

public class FunctionCalculate {

    public static double calculateSX(List<Point> points, int n) {
        double SX = 0;
        for (int i = 0; i < n; i++) {
            SX += points.get(i).x;
        }
        return SX;
    }

    public static double calculateSXX(List<Point> points, int n) {
        double SXX = 0;
        for (int i = 0; i < n; i++) {
            SXX += Math.pow(points.get(i).x, 2);
        }
        return SXX;
    }

    public static double calculateSXXX(List<Point> points, int n) {
        double SXXX = 0;
        for (int i = 0; i < n; i++) {
            SXXX += Math.pow(points.get(i).x, 3);
        }
        return SXXX;
    }

    public static double calculateSXXXX(List<Point> points, int n) {
        double SXXXX = 0;
        for (int i = 0; i < n; i++) {
            SXXXX += Math.pow(points.get(i).x, 4);
        }
        return SXXXX;
    }

    public static double calculateSXXXXX(List<Point> points, int n) {
        double SXXXXX = 0;
        for (int i = 0; i < n; i++) {
            SXXXXX += Math.pow(points.get(i).x, 5);
        }
        return SXXXXX;
    }
    public static double calculateSXXXXXX(List<Point> points, int n) {
        double SXXXXXX = 0;
        for (int i = 0; i < n; i++) {
            SXXXXXX += Math.pow(points.get(i).x, 6);
        }
        return SXXXXXX;
    }

    public static double calculateSY(List<Point> points, int n) {
        double SY = 0;
        for (int i = 0; i < n; i++) {
            SY += points.get(i).y;
        }
        return SY;
    }

    public static double calculateSXY(List<Point> points, int n) {
        double SXY = 0;
        for (int i = 0; i < n; i++) {
            SXY += points.get(i).x * points.get(i).y;
        }
        return SXY;
    }

    public static double calculateSXXY(List<Point> points, int n) {
        double SXXY = 0;
        for (int i = 0; i < n; i++) {
            SXXY += Math.pow(points.get(i).x, 2) * points.get(i).y;
        }
        return SXXY;
    }

    public static double calculateSXXXY(List<Point> points, int n) {
        double SXXXY = 0;
        for (int i = 0; i < n; i++) {
            SXXXY += Math.pow(points.get(i).x, 3) * points.get(i).y;
        }
        return SXXXY;
    }
}
