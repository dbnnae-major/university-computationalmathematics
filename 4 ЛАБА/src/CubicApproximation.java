import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CubicApproximation {
    public static List<Double> calculateCoefficients(List<Point> points, int n) {
        // 1) собираем суммы
        double SX    = FunctionCalculate.calculateSX(points, n);
        double SXX   = FunctionCalculate.calculateSXX(points, n);
        double SXXX  = FunctionCalculate.calculateSXXX(points, n);
        double SXXXX = FunctionCalculate.calculateSXXXX(points, n);
        double SXXXXX= FunctionCalculate.calculateSXXXXX(points, n);
        double SXXXXXX=FunctionCalculate.calculateSXXXXXX(points, n);

        double SY    = FunctionCalculate.calculateSY(points, n);
        double SXY   = FunctionCalculate.calculateSXY(points, n);
        double SXXY  = FunctionCalculate.calculateSXXY(points, n);
        double SXXXY = FunctionCalculate.calculateSXXXY(points, n);

        double D = det4(
                n,    SX,    SXX,    SXXX,
                SX,   SXX,   SXXX,   SXXXX,
                SXX,  SXXX,  SXXXX,  SXXXXX,
                SXXX, SXXXX, SXXXXX, SXXXXXX
        );

        double D0 = det4(
                SY,   SX,    SXX,    SXXX,
                SXY,  SXX,   SXXX,   SXXXX,
                SXXY, SXXX,  SXXXX,  SXXXXX,
                SXXXY,SXXXX, SXXXXX, SXXXXXX
        );
        double D1 = det4(
                n,    SY,    SXX,    SXXX,
                SX,   SXY,   SXXX,   SXXXX,
                SXX,  SXXY,  SXXXX,  SXXXXX,
                SXXX, SXXXY, SXXXXX, SXXXXXX
        );
        double D2 = det4(
                n,    SX,    SY,     SXXX,
                SX,   SXX,   SXY,    SXXXX,
                SXX,  SXXX,  SXXY,   SXXXXX,
                SXXX, SXXXX, SXXXY,  SXXXXXX
        );
        double D3 = det4(
                n,    SX,    SXX,    SY,
                SX,   SXX,   SXXX,   SXY,
                SXX,  SXXX,  SXXXX,  SXXY,
                SXXX, SXXXX, SXXXXX, SXXXY
        );


        double a0 = D0 / D;
        double a1 = D1 / D;
        double a2 = D2 / D;
        double a3 = D3 / D;

        List<Double> coefficient = new ArrayList<>(4);
        coefficient.add(a0);
        coefficient.add(a1);
        coefficient.add(a2);
        coefficient.add(a3);
        return coefficient;
    }

    private static double det4(
            double a1,double a2,double a3,double a4,
            double b1,double b2,double b3,double b4,
            double c1,double c2,double c3,double c4,
            double d1,double d2,double d3,double d4
    ) {
        return a1 * det3(b2,b3,b4, c2,c3,c4, d2,d3,d4)
                - a2 * det3(b1,b3,b4, c1,c3,c4, d1,d3,d4)
                + a3 * det3(b1,b2,b4, c1,c2,c4, d1,d2,d4)
                - a4 * det3(b1,b2,b3, c1,c2,c3, d1,d2,d3);
    }

    private static double det3(
            double a1,double a2,double a3,
            double b1,double b2,double b3,
            double c1,double c2,double c3
    ) {
        return a1*(b2*c3 - b3*c2)
                - a2*(b1*c3 - b3*c1)
                + a3*(b1*c2 - b2*c1);
    }

    public static List<Point> calculatePoints(List<Point> points, int n, List<Double> coefficients) {
        List<Point> phi_points = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double x = points.get(i).x;
            double y = coefficients.get(0)
                    + coefficients.get(1)*x
                    + coefficients.get(2)*x*x
                    + coefficients.get(3)*x*x*x;
            phi_points.add(new Point(x,y));
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

    public static String printFunction(List<Double> c) {
        double a0 = c.get(0), a1 = c.get(1), a2 = c.get(2), a3 = c.get(3);
        if (!Double.isFinite(a0) || !Double.isFinite(a1)
                || !Double.isFinite(a2) || !Double.isFinite(a3)) {
            return "Error";
        }
        BigDecimal bd0 = BigDecimal.valueOf(a0).setScale(3, RoundingMode.HALF_UP);
        BigDecimal bd1 = BigDecimal.valueOf(a1).setScale(3, RoundingMode.HALF_UP);
        BigDecimal bd2 = BigDecimal.valueOf(a2).setScale(3, RoundingMode.HALF_UP);
        BigDecimal bd3 = BigDecimal.valueOf(a3).setScale(3, RoundingMode.HALF_UP);

        String s0 = bd0.toPlainString();
        String s1 = bd1.abs().toPlainString();
        String s2 = bd2.abs().toPlainString();
        String s3 = bd3.abs().toPlainString();

        String sign1 = bd1.signum() >= 0 ? " + " : " - ";
        String sign2 = bd2.signum() >= 0 ? " + " : " - ";
        String sign3 = bd3.signum() >= 0 ? " + " : " - ";

        return "φ(x) = "
                + s0
                + sign1 + s1 + "·x"
                + sign2 + s2 + "·x²"
                + sign3 + s3 + "·x³";
    }
}
