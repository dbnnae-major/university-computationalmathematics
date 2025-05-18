import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class QuadraticApproximation {
    public static List<Double> calculateCoefficients(List<Point> points, int n)  {
        double SX = FunctionCalculate.calculateSX(points, n);
        double SXX = FunctionCalculate.calculateSXX(points, n);
        double SXXX = FunctionCalculate.calculateSXXX(points, n);
        double SXXXX = FunctionCalculate.calculateSXXXX(points, n);
        double SXY = FunctionCalculate.calculateSXY(points, n);
        double SXXY = FunctionCalculate.calculateSXXY(points, n);
        double SY = FunctionCalculate.calculateSY(points, n);

        double D  = det3(n,   SX,   SXX,
                SX,  SXX,  SXXX,
                SXX, SXXX, SXXXX);

        double D0 = det3(SY,  SX,   SXX,
                SXY, SXX,  SXXX,
                SXXY,SXXX, SXXXX);

        double D1 = det3(n,   SY,   SXX,
                SX,  SXY,  SXXX,
                SXX, SXXY, SXXXX);

        double D2 = det3(n,   SX,   SY,
                SX,  SXX,  SXY,
                SXX, SXXX, SXXY);

        double a0 = D0 / D;
        double a1 = D1 / D;
        double a2 = D2 / D;

        List<Double> coefficients = new ArrayList<>(3);

        coefficients.add(a0);
        coefficients.add(a1);
        coefficients.add(a2);

        return coefficients;
    }

    private static double det3(
            double a1, double a2, double a3,
            double b1, double b2, double b3,
            double c1, double c2, double c3) {
        return a1 * (b2 * c3 - b3 * c2)
                - a2 * (b1 * c3 - b3 * c1)
                + a3 * (b1 * c2 - b2 * c1);
    }

    public static String printFunction(List<Double> coefficients) {
        double a0 = coefficients.get(0);
        double a1 = coefficients.get(1);
        double a2 = coefficients.get(2);

        if (!Double.isFinite(a0) || !Double.isFinite(a1) || !Double.isFinite(a2)) {
            return "Error";
        }

        BigDecimal bd0 = BigDecimal.valueOf(a0).setScale(3, RoundingMode.HALF_UP);
        BigDecimal bd1 = BigDecimal.valueOf(a1).setScale(3, RoundingMode.HALF_UP);
        BigDecimal bd2 = BigDecimal.valueOf(a2).setScale(3, RoundingMode.HALF_UP);

        String s0 = bd0.toPlainString();
        String s1 = bd1.abs().toPlainString();
        String s2 = bd2.abs().toPlainString();

        String sign1 = bd1.signum() >= 0 ? " + " : " - ";
        String sign2 = bd2.signum() >= 0 ? " + " : " - ";

        return "φ(x) = "
                + s0
                + sign1 + s1 + "·x"
                + sign2 + s2 + "·x²";
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
        double phi_sum = 0;
        List<Double> delts = new ArrayList<>(n+3);

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
        delts.add(R);

        return delts;
    }

    public static double f(List<Double> coefficients, double x) {
        double a0 = coefficients.get(0);
        double a1 = coefficients.get(1);
        double a2 = coefficients.get(2);


        return a0 + a1 * x + a2 * x * x;
    }
}
