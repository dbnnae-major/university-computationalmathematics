import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class LinearApproximation {
    public double a;

    public static List<Double> calculateCoefficients(List<Point> points, int n) {
        double SX = FunctionCalculate.calculateSX(points, n);
        double SXX = FunctionCalculate.calculateSXX(points, n);
        double SXY = FunctionCalculate.calculateSXY(points, n);
        double SY = FunctionCalculate.calculateSY(points, n);

        double delta = SXX * n - Math.pow(SX, 2);
        double delta1 = SXY * n - SX * SY;
        double delta2 = SXX * SY - SX * SXY;
        double a = delta1 / delta;
        double b = delta2 / delta;

        List<Double> coefficients = new ArrayList<>(2);

        coefficients.add(a);
        coefficients.add(b);

        return coefficients;
    }

    public static String printFunction(List<Double> coefficients) {
        double a = coefficients.get(0);
        double b = coefficients.get(1);

        if (!Double.isFinite(a) || !Double.isFinite(b)) {
            return "Error";
        }

        BigDecimal bdA = BigDecimal.valueOf(a)
                .setScale(3, RoundingMode.HALF_UP);
        BigDecimal bdB = BigDecimal.valueOf(b)
                .setScale(3, RoundingMode.HALF_UP);

        String sA = bdA.toPlainString();
        String sB = bdB.abs().toPlainString();
        String sign = bdB.signum() >= 0 ? " + " : " - ";

        return "φ = " + sA + "·x" + sign + sB;
    }

    public static List<Point> calculatePoints(List<Point> points, int n, List<Double> coefficients) {
        List<Point> phi_points = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            double x = points.get(i).x;
            double y = f(coefficients, x);
            phi_points.add(new Point(x, y));
        }

        return phi_points;
    }

    public static List<Double> calculateDelta(List<Point> points, int n, List<Point> phi_points) {
        double e = 0;
        double e_2 = 0;
        double d = 0;

        double SX = FunctionCalculate.calculateSX(points, n);
        double SY = FunctionCalculate.calculateSY(points, n);

        double x_mid = SX/n;
        double y_mid = SY/n;
        double numerator = 0;
        double x_denominator = 0;
        double y_denominator = 0;
        double denominator = 0;
        double phi_sum = 0;

        for (int i = 0; i < n; i++) {
            numerator += (points.get(i).x - x_mid) * (points.get(i).y - y_mid);
            x_denominator += Math.pow((points.get(i).x - x_mid), 2);
            y_denominator += Math.pow((points.get(i).y - y_mid), 2);
        }
        denominator = Math.sqrt(x_denominator*y_denominator);
        double r = numerator/denominator;

        List<Double> delts = new ArrayList<>(n+4);

        for (int i = 0; i < n; i++) {
            double y1 = points.get(i).y;
            double y2 = phi_points.get(i).y;
            phi_sum += y2;
            e = y2 - y1;
            delts.add(e);
            e_2 += Math.pow(y2-y1, 2);
        }

        //коэффициент детерминации
        double phi_mid = phi_sum/n;
        double s1 = 0;
        double s2 = 0;

        for (int i = 0; i < n; i++) {
            double y1 = points.get(i).y;
            double y2 = phi_points.get(i).y;
            s1 += Math.pow(y1 - y2, 2);
            s2 += Math.pow(y1 - phi_mid, 2);

        }
        double R = 1 - s1/s2;

        d = Math.sqrt(e_2/n);
        delts.add(d);
        delts.add(e_2);
        delts.add(r);
        delts.add(R);

        return delts;
    }

    public static double f(List<Double> coefficients, double x) {
        double a = coefficients.get(0);
        double b = coefficients.get(1);

        return a * x + b;
    }
}
