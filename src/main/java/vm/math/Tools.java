/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vm.math;

import java.awt.geom.Point2D;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import vm.datatools.DataTypeConvertor;

/**
 *
 * @author Vlada
 */
public class Tools {

    public static float round(float input, float toValue, boolean floor) {
        float add = 0;
        if (!floor) {
            if (input > 0) {
                add = toValue / 2;
            } else {
                add = -toValue / 2;
            }
        }
        double scale = 1 / toValue;
        float f = (float) ((input + add) * scale);
        int m = (int) f;
        double ret = m / scale;
        float retFloat = (float) ret;
        return retFloat;
    }

    public static float[][] copyMatrix(float[][] matrix) {
        float[][] ret = new float[matrix.length][matrix[0].length];
        for (int i = 0; i < ret.length; i++) {
            System.arraycopy(matrix[i], 0, ret[i], 0, matrix[i].length);
        }
        return ret;
    }

    public static float maxValue(Collection<float[]> floats) {
        float ret = Float.MIN_VALUE;
        for (float[] fArray : floats) {
            for (float f : fArray) {
                if (f > ret) {
                    ret = f;
                }
            }
        }
        return ret;
    }

    /**
     * To each valueForX xi assing valueForX max(y0, ..., yi)
     *
     * @param plotXY
     * @return
     */
    public static SortedMap<Float, Float> createNonDecreasingFunction(SortedMap<Float, Float> plotXY) {
        float maxValue = -Float.MAX_VALUE;
        SortedMap<Float, Float> ret = new TreeMap<>();
        for (Map.Entry<Float, Float> entry : plotXY.entrySet()) {
            maxValue = Math.max(maxValue, entry.getValue());
            ret.put(entry.getKey(), maxValue);
        }
        return ret;
    }

    public static float interpolatePoints(List<Point2D.Float> points, float x0) {
        Point2D.Float segmentStart = null;
        Point2D.Float segmentEnd = null;
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i - 1).x <= x0 && points.get(i).x >= x0) {
                segmentStart = points.get(i - 1);
                segmentEnd = points.get(i);
                break;
            }
        }
        if (segmentEnd == null) {
            if (points.get(0).x >= x0) {
                return points.get(0).y;
            }
            return points.get(points.size() - 1).y;
        }
        float directive = makeDirective(segmentStart.y, segmentEnd.y, segmentStart.x, segmentEnd.x);
        return segmentStart.y + directive * (x0 - segmentStart.x);
    }

    private static float makeDirective(float y1, float y2, float x1, float x2) {
        return (y1 - y2) / (x1 - x2);
    }

    public static double pearsonCorrelationCoefficient(double[] a1, double[] a2) {
        if (a1.length != a2.length) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, "The lengths of arrays are not the same: {0}, {1}", new Object[]{a1.length, a2.length});
        }
        PearsonsCorrelation evaluator = new PearsonsCorrelation();
        return evaluator.correlation(a1, a2);
    }

    public static double[][] pearsonCorrelationMatrixOfColumns(double[][] data) {
        PearsonsCorrelation evaluator = new PearsonsCorrelation();
        return evaluator.computeCorrelationMatrix(data).getData();
    }

    public static double getMean(double[] values) {
        return new Mean().evaluate(values);
    }

    public static double getVariance(double[] values) {
        return new Variance().evaluate(values);
    }

    public static double getMedian(double[] values) {
        return new Median().evaluate(values);
    }

    public static double getMean(int[] values) {
        return new Mean().evaluate(DataTypeConvertor.intsToDoubles(values));
    }

    public static double getVariance(int[] values) {
        return new Variance().evaluate(DataTypeConvertor.intsToDoubles(values));
    }

    public static double getMedian(int[] values) {
        return new Median().evaluate(DataTypeConvertor.intsToDoubles(values));
    }

    public static double getMean(float[] values) {
        return new Mean().evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getVariance(float[] values) {
        return new Variance().evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getStandardDeviation(float[] values) {
        return new StandardDeviation().evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getStandardDeviation(double[] values) {
        return new StandardDeviation().evaluate(values);
    }

    public static double getMin(float[] values) {
        return new Min().evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getMin(double[] values) {
        return new Min().evaluate(values);
    }

    public static double getMax(float[] values) {
        return new Max().evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getMax(double[] values) {
        return new Max().evaluate(values);
    }

    public static double getQuartile1(float[] values) {
        return new Percentile(25).evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getQuartile1(double[] values) {
        return new Percentile(25).evaluate(values);
    }

    public static double getQuartile3(float[] values) {
        return new Percentile(75).evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getQuartile3(double[] values) {
        return new Percentile(75).evaluate(values);
    }

    public static double getMedian(float[] values) {
        return new Median().evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getIDim(double[] valuesD, boolean print) {
        double mean = getMean(valuesD);
        double variance = getVariance(valuesD);
        double ret = mean * mean / 2d / variance;
        if (print) {
            System.out.print(mean + ";" + variance + ";" + ret);
        }
        return ret;
    }

    public static float[] subtractVectors(float[] origVector, float[] toBeSubtracted) {
        float[] ret = new float[Math.min(origVector.length, toBeSubtracted.length)];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = origVector[i] - toBeSubtracted[i];
        }
        return ret;
    }

    public static double[][] subtractColumnsMeansFromMatrix(float[][] matrix, float[] meansOverColumns) {
        double[][] ret = new double[matrix.length][meansOverColumns.length];
        for (int i = 0; i < matrix.length; i++) {
            float[] row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                ret[i][j] = row[j] - meansOverColumns[j];
            }
        }
        return ret;
    }

    public static SortedSet<Map.Entry<Integer, Float>> evaluateSumsPerRow(float[][] matrix, boolean sortedList) {
        SortedSet<Map.Entry<Integer, Float>> ret = new TreeSet<>(new vm.datatools.Tools.MapByFloatValueComparator());
        for (Integer i = 0; i < matrix.length; i++) {
            float[] row = matrix[i];
            Float sum = 0.0F;
            for (int j = 0; j < row.length; j++) {
                sum += row[j];
            }
            ret.add(new AbstractMap.SimpleEntry<>(i, sum));
        }
        return ret;
    }

    public static float[][] transposeMatrix(float[][] matrix) {
        float[][] ret = new float[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                ret[j][i] = matrix[i][j];
            }
        }
        return ret;
    }

    public static float[] evaluateAnglesOfTriangle(float[] dists, boolean inDegrees) {
        float a = dists[0];
        float b = dists[1];
        float c = dists[2];
        return evaluateAnglesOfTriangle(a, b, c, inDegrees);
    }

    public static float[] evaluateAnglesOfTriangle(float a, float b, float c, boolean inDegrees) {
        float a2 = a * a;
        float b2 = b * b;
        float c2 = c * c;
        float alpha = (float) Math.acos((b2 + c2 - a2) / (2 * b * c));
        float beta = (float) Math.acos((a2 + c2 - b2) / (2 * a * c));
        float gamma = (float) Math.acos((a2 + b2 - c2) / (2 * a * b));
        if (inDegrees) {
            alpha = (float) (alpha * 180 / Math.PI);
            beta = (float) (beta * 180 / Math.PI);
            gamma = (float) (gamma * 180 / Math.PI);
        }
        return new float[]{alpha, beta, gamma};
    }

    public static double degToRad(double angleInDegrees) {
        return (angleInDegrees / 180) * Math.PI;
    }

    public static float[] degsToRad(float[] angleInDegrees) {
        float[] ret = new float[angleInDegrees.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (float) degToRad(angleInDegrees[i]);
        }
        return ret;
    }

    public static float[] radsToDeg(float[] angleInRads) {
        float[] ret = new float[angleInRads.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (float) radToDeg(angleInRads[i]);
        }
        return ret;
    }

    public static double radToDeg(double angleInRads) {
        return (angleInRads / Math.PI) * 180;
    }

    /**
     *
     * @param value valueForX to round
     * @param granularity granularity of rounding
     * @return closest lower or equal number to @valueForX which is equal the
     * some integer multiplied by a @granularity
     */
    public static float floorToGranularity(double value, double granularity) {
        int m = (int) (value / granularity);
        float cur = (float) (m * granularity);
        return cur;
    }

    public static double getLengthOfVector(float[] vector) {
        float sum = 0;
        for (int i = 0; i < vector.length; i++) {
            sum += vector[i] * vector[i];
        }
        return Math.sqrt(sum);
    }

    private static double gcd(Number a, Number b) {
        double ad = a.doubleValue();
        double bd = b.doubleValue();
        while (bd > 0) {
            double temp = bd;
            bd = ad % bd;
            ad = temp;
        }
        return ad;
    }

    public static double gcd(Number... input) {
        double result = input[0].doubleValue();
        for (int i = 1; i < input.length; i++) {
            result = gcd(result, input[i]);
        }
        return result;
    }

    public static double lcm(Number a, Number b) {
        double ad = a.doubleValue();
        double bd = b.doubleValue();
        return ad * (bd / gcd(ad, bd));
    }

    public static double lcm(Number... input) {
        double result = input[0].doubleValue();
        for (int i = 1; i < input.length; i++) {
            result = lcm(result, input[i]);
        }
        return result;
    }

    public static float ifSmallerThanOneRoundToFirstNonzeroFloatingNumber(float f) {
        if (f >= 1) {
            return f;
        }
        float bigN = f;
        int exp = 0;
        while (bigN < 1) {
            bigN *= 10;
            exp++;
        }
        int num = (int) bigN;
        return (float) ((float) num * Math.pow(10, -exp));
    }

}
