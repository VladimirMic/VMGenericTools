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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

/**
 *
 * @author Vlada
 */
public class Tools {

    public static float round(float distance, float toValue, boolean floor) {
        float add = 0;
        if (!floor) {
            add = toValue / 2;
        }
        int m = (int) ((distance + add) / toValue);
        float cur = (float) (m * toValue);
        return cur;
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
     * To each value xi assing value max(y0, ..., yi)
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
        SortedSet<Map.Entry<Integer, Float>> ret = new TreeSet<>(new vm.datatools.Tools.MapByValueComparator());
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

}
