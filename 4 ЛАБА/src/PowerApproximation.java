import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PowerApproximation {
    public static List<Double> calculateCoefficients(List<Point> points, int n) {
        // Линеаризуем ln(y) = ln(a) + b·ln(x)
        double SLX  = 0;   // Σ ln(x_i)
        double SLLX = 0;   // Σ [ln(x_i)]^2
        double SLY  = 0;   // Σ ln(y_i)
        double SLXY = 0;   // Σ ln(x_i)·ln(y_i)

        for (int i = 0; i < n; i++) {
            double x = points.get(i).x;
            double y = points.get(i).y;
            double LX = Math.log(x);
            double LY = Math.log(y);
            SLX  += LX;
            SLLX += LX * LX;
            SLY  += LY;
            SLXY += LX * LY;
        }

        // Δ = n·ΣX² − (ΣX)²
        double delta  = n * SLLX - SLX * SLX;
        double deltaA = SLY * SLLX - SLX * SLXY; // для A = ln(a)
        double deltaB = n   * SLXY  - SLX * SLY;  // для B = b

        double A = deltaA / delta;  // ln(a)
        double B = deltaB / delta;  // b

        double a = Math.exp(A);
        double b = B;

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
        String sB = bdB.toPlainString();

        return "φ(x) = " + sA + "·x^" + sB;
    }

    public static List<Point> calculatePoints(List<Point> points, int n, List<Double> coefficients) {
        List<Point> phi_points = new ArrayList<>(n);
        double a = coefficients.get(0);
        double b = coefficients.get(1);

        for (int i = 0; i < n; i++) {
            double x = points.get(i).x;
            double y = a * Math.pow(x, b);
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
            e_2 += Math.pow(e, 2);
        }

        double phi_mid = phi_sum / n;
        double s1 = 0;
        double s2 = 0;

        for (int i = 0; i < n; i++) {
            double y1 = points.get(i).y;
            double y2 = phi_points.get(i).y;
            s1 += Math.pow(y1 - y2, 2);
            s2 += Math.pow(y1 - phi_mid, 2);
        }

        double R = 1 - s1 / s2;
        d = Math.sqrt(e_2 / n);

        delts.add(d);
        delts.add(e_2);
        delts.add(R);

        return delts;
    }
}
