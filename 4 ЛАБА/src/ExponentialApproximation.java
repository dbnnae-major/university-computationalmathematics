import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ExponentialApproximation {

    public static List<Double> calculateCoefficients(List<Point> points, int n) {
        double SX  = FunctionCalculate.calculateSX(points, n);
        double SXX = FunctionCalculate.calculateSXX(points, n);

        double SYln  = 0;
        double SXYln = 0;
        for (int i = 0; i < n; i++) {
            double x = points.get(i).x;
            double y = points.get(i).y;
//            if (y <= 0) {
//                throw new IllegalArgumentException("y должен быть > 0 для экспоненты");
//            }
            double Y = Math.log(y);
            SYln   += Y;
            SXYln  += x * Y;
        }

        double delta  = n * SXX - SX * SX;
        double deltaA = SYln * SXX - SX * SXYln;
        double deltaB = n * SXYln  - SX * SYln;

        double A = deltaA / delta;
        double B = deltaB / delta;


        double a = Math.exp(A);
        double b = B;

        List<Double> coeffs = new ArrayList<>(2);
        coeffs.add(a);
        coeffs.add(b);
        return coeffs;
    }

    public static String printFunction(List<Double> coefficients) {
        double a = coefficients.get(0);
        double b = coefficients.get(1);
        if (!Double.isFinite(a) || !Double.isFinite(b)) {
            return "Error";
        }
        BigDecimal bdA = BigDecimal.valueOf(a).setScale(3, RoundingMode.HALF_UP);
        BigDecimal bdB = BigDecimal.valueOf(b).setScale(3, RoundingMode.HALF_UP);
        String sA = bdA.toPlainString();
        String sB = bdB.toPlainString();

        return "φ(x) = " + sA + "·e^(" + sB + "·x)";
    }

    public static List<Point> calculatePoints(List<Point> points, int n, List<Double> coefficients) {
        double a = coefficients.get(0);
        double b = coefficients.get(1);
        List<Point> phi_points = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double x = points.get(i).x;
            double y = a * Math.exp(b * x);
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
}
