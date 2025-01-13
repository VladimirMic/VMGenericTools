/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vm.mathtools;

import java.awt.geom.Point2D;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
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
        double tValueD = DataTypeConvertor.floatToPreciseDouble(toValue);
        double inputD = DataTypeConvertor.floatToPreciseDouble(input);
        if (toValue < 1) {
            return roundInternal(inputD, tValueD, floor, true);
        } else {
            return roundInternal(inputD, tValueD, floor, false);
        }
    }

    public static double roundDouble(double input, float toValue, boolean floor) {
        float ret = round((float) input, toValue, floor);
        return vm.datatools.DataTypeConvertor.floatToPreciseDouble(ret);
    }

    public static long roundLong(long input, long toValue, boolean floor) {
        if (!floor) {
            input += toValue / 2;
        }
        long part = input / toValue;
        return part * toValue;
    }

    private static float roundInternal(double input, double toValue, boolean floor, boolean multiplication) {
        double add = 0;
        if (!floor) {
            if (input > 0) {
                add = toValue / 2;
            } else {
                add = -toValue / 2;
            }
        }
        double scale = multiplication ? toValue : 1 / toValue;
        double f = multiplication ? ((input + add) / scale) : ((input + add) * scale);
        int m = (int) f;
        double ret = multiplication ? m * scale : m / scale;
        return DataTypeConvertor.doubleToPreciseFloat(ret);
    }

    public static float[][] copyMatrix(float[][] matrix) {
        float[][] ret = new float[matrix.length][matrix[0].length];
        for (int i = 0; i < ret.length; i++) {
            System.arraycopy(matrix[i], 0, ret[i], 0, matrix[i].length);
        }
        return ret;
    }

    public static int[] maxValueWithIdxAndCount(int[] array) {
        int ret = -Integer.MAX_VALUE;
        int retIdx = -1;
        int count = 0;
        for (int idx = 0; idx < array.length; idx++) {
            int i = array[idx];
            if (i > ret) {
                ret = i;
                retIdx = idx;
                count = 1;
            } else if (i == ret) {
                count++;
            }
        }
        return new int[]{ret, retIdx, count};

    }

    public static int maxValue(int[] array) {
        int ret = -Integer.MAX_VALUE;
        for (int i : array) {
            ret = Math.max(ret, i);
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

    public static double getMin(Collection<Float> values) {
        Float[] array = values.toArray(Float[]::new);
        double[] primitive = vm.datatools.DataTypeConvertor.floatsToDoubles(array);
        return new Min().evaluate(primitive);
    }

    public static double getMax(float[] values) {
        return new Max().evaluate(DataTypeConvertor.floatsToDoubles(values));
    }

    public static double getMax(double[] values) {
        return new Max().evaluate(values);
    }

    public static double getMax(Collection<Float> values) {
        Float[] array = values.toArray(Float[]::new);
        double[] primitive = vm.datatools.DataTypeConvertor.floatsToDoubles(array);
        return new Max().evaluate(primitive);
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

    private static float gcd(Float a, Float b) {
        if (a == null || b == null) {
            return Float.NaN;
        }
        b = Math.abs(b);
        a = Math.abs(a);
        Double ad = DataTypeConvertor.floatToPreciseDouble(a);
        Double bd = DataTypeConvertor.floatToPreciseDouble(b);
        while (bd > 0) {
            double temp = bd;
            bd = ad % bd;
            ad = temp;
            a = null;
            a = DataTypeConvertor.doubleToPreciseFloat(ad);
            b = null;
            b = DataTypeConvertor.doubleToPreciseFloat(bd);
            ad = null;
            ad = DataTypeConvertor.floatToPreciseDouble(a);
            bd = null;
            bd = DataTypeConvertor.floatToPreciseDouble(b);
        }
        return DataTypeConvertor.doubleToPreciseFloat(ad);
    }

    public static float gcd(Float... input) {
        Float result = input[0];
        for (int i = 1; i < input.length; i++) {
            result = gcd(result, input[i]);
        }
        return result;
    }

    public static float lcm(Float ad, Float bd) {
        return ad * (bd / gcd(ad, bd));
    }

    public static float lcm(Long a, Long b) {
        Float af = a.floatValue();
        Float bf = b.floatValue();
        return af * (bf / gcd(af, bf));
    }

    public static float lcm(Float... input) {
        Float result = input[0];
        for (int i = 1; i < input.length; i++) {
            result = lcm(result, input[i]);
        }
        return result;
    }

    private static float ifSmallerThanOneStepForHistogram(float f) {
        if (f >= 1 || f <= -1) {
            return f;
        }
        int sig = f < 0 ? -1 : 1;
        f = f * sig;
        float bigN = f;
        int exp = 0;
        while (bigN < 1) {
            bigN *= 10;
            exp++;
        }
        int num = (int) bigN;
        float ret = (float) ((float) num * Math.pow(10, -exp)) * sig;
        if (ret == 0.1f && f >= 0.12f) {
            return 0.15f;
        }
        return ret;
    }

    public static SortedMap<Float, Float> createHistogramOfValues(float[] values) {
        return createHistogramOfValues(values, false, false);
    }

    public static SortedMap<Float, Float> createHistogramOfValues(float[] values, boolean absoluteValues, boolean printLog) {
        float max = (float) Tools.getMax(values);
        float min = (float) Tools.getMin(values);
        float basicInterval = computeBasicXIntervalForHistogram(min, max);
        return createHistogramOfValues(values, basicInterval, absoluteValues, printLog);
    }

    /**
     *
     * @param values
     * @param step on the x axis
     * @param absoluteValues if false, then histogram. Otherwise absolute values
     * @param printLogMinMax
     * @return
     */
    public static SortedMap<Float, Float> createHistogramOfValues(float[] values, float step, boolean absoluteValues, boolean printLogMinMax) {
        SortedMap<Float, Float> ret = new TreeMap<>();
        for (Float value : values) {
            Float key = Tools.round(value, step, false);
            if (!ret.containsKey(key)) {
                ret.put(key, 1.0F);
            } else {
                float count = ret.get(key);
                ret.put(key, count + 1.0F);
            }
        }
        Float lastKey = ret.lastKey();
        double stepd = DataTypeConvertor.floatToPreciseDouble(step);
        while (lastKey >= ret.firstKey()) {
            double lastKeyD = DataTypeConvertor.floatToPreciseDouble(lastKey);
            if (!ret.containsKey(lastKey)) {
                Float nextKey = ret.tailMap(lastKey).firstKey();
                float dif = Math.abs(nextKey - lastKey);
                dif = Tools.ifSmallerThanOneStepForHistogram(dif);
                if (dif != 0) {
                    ret.put(lastKey, 0.0F);
                }
            } else if (!absoluteValues) {
                ret.put(lastKey, ret.get(lastKey) / values.length);
            }
            lastKeyD -= stepd;
            lastKey = (float) lastKeyD;
        }
        if (printLogMinMax) {
            Float firstKey = ret.firstKey();
            lastKey = ret.lastKey();
            System.err.println(firstKey + ";" + ret.get(firstKey) + ";" + lastKey + ";" + ret.get(lastKey));
        }
        return ret;
    }

    public static SortedMap<Float, Float> createHistogramOfValues(List<Float> values, float step, boolean absoluteValues, boolean printLog) {
        float[] floatToPrimitiveArray = DataTypeConvertor.floatToPrimitiveArray(values);
        return createHistogramOfValues(floatToPrimitiveArray, step, absoluteValues, printLog);
    }

    public static SortedMap<Float, Float> createHistogramOfValues(List<Float> values, boolean absoluteValues, boolean printLog) {
        float[] floatToPrimitiveArray = DataTypeConvertor.floatToPrimitiveArray(values);
        return createHistogramOfValues(floatToPrimitiveArray, absoluteValues, printLog);
    }

    public static SortedMap<Float, Float> createHistogramOfValues(List<Float> values) {
        float[] floatToPrimitiveArray = DataTypeConvertor.floatToPrimitiveArray(values);
        return createHistogramOfValues(floatToPrimitiveArray);
    }

    public static float computeBasicXIntervalForHistogram(float[] values) {
        float max = (float) Tools.getMax(values);
        float min = (float) Tools.getMin(values);
        return computeBasicXIntervalForHistogram(min, max);
    }

    public static float computeBasicXIntervalForHistogram(Collection<Float> values) {
        float max = (float) Tools.getMax(values);
        float min = (float) Tools.getMin(values);
        return computeBasicXIntervalForHistogram(min, max);
    }

    /**
     * Return the width of the interval that defines between 80 and 160 points
     *
     * @param min
     * @param max
     * @return
     */
    public static float computeBasicXIntervalForHistogram(float min, float max) {
        int exp = 0;
        float diff = max - min;
        if (diff == 0) {
            return 0.1f;
        }
        while (diff < 1) {
            diff *= 10;
            exp++;
        }
        float maxCopy = max - min;
        int untilZero = (int) maxCopy;
        float prev;
        int counter = 0;
        if (untilZero < 0) {
            while (untilZero != maxCopy) {
                untilZero = (int) (maxCopy * 10);
                maxCopy *= 10;
                counter--;
            }
        }
        prev = (int) diff;
        untilZero = (int) (prev / 10);
        while (untilZero != 0) {
            prev = untilZero;
            untilZero = (int) (maxCopy / 10);
            maxCopy /= 10;
            counter++;
        }
        counter -= 3;
        float ret = (float) (prev * Math.pow(10, counter));
        ret = (float) (ret / Math.pow(10, exp));
        while (80 * ret > max - min) {
            ret /= 1.25;
        }
        while (160 * ret < max - min) {
            ret *= 1.25;
        }
        ret = Tools.ifSmallerThanOneStepForHistogram(ret);
        Logger.getLogger(Tools.class.getName()).log(Level.INFO, "Step for the plot with min and max values on x axis {0}, {1} is decided to be {2}", new Object[]{min, max, ret});
        return ret;
    }

    public static float getStepOfAlreadyMadeHistogram(SortedMap<Float, Float> histogram) {
        if (histogram == null || histogram.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (histogram.size() == 1) {
            return 0.1f;
        }
        Iterator<Float> it = histogram.keySet().iterator();
        Float prev = it.next();
        Float curr = it.next();
        double prevD = DataTypeConvertor.floatToPreciseDouble(prev);
        double currD = DataTypeConvertor.floatToPreciseDouble(curr);
        double retD = currD - prevD;
        float ret = Tools.ifSmallerThanOneStepForHistogram((float) retD);
        ret = Tools.ifSmallerThanOneStepForHistogram(ret);
        Logger.getLogger(Tools.class.getName()).log(Level.INFO, "Histogram bar width identified as {0}. This has to be the same value as above.", ret);
        return ret;
    }

    public static String formatFirstZeros(int decimal, int digits) {
        String ret = "";
        String decimalS = Integer.toString(decimal);
        while (ret.length() + decimalS.length() < digits) {
            ret += "0";
        }
        return ret + decimalS;
    }

    public static Float getTangent(Float y1, Float y0, Float x1, Float x0) {
        if (y1 == null || y0 == null || x1 == null || x0 == null) {
            return null;
        }
        if (y1 == Float.NaN || y0 == Float.NaN || x1 == Float.NaN || x0 == Float.NaN) {
            return Float.NaN;
        }
        return (y1 - y0) / (x1 - x0);
    }

    public static Float getTangent(Float y1, Float y0, Long x1, Long x0) {
        if (y1 == null || y0 == null || x1 == null || x0 == null) {
            return null;
        }
        if (y1 == Float.NaN || y0 == Float.NaN) {
            return Float.NaN;
        }
        return (y1 - y0) / (x1 - x0);
    }

    public static int compareNumbers(Number n1, Number n2) {
        if (n1 == null && n2 != null) {
            return 1;
        }
        if (n2 == null && n1 != null) {
            return -1;
        }
        if (n1 == null && n2 == null) {
            return 0;
        }
        return Float.compare(n1.floatValue(), n2.floatValue());
    }

    public static Double computeDistanceFromGPS(Float lat1, Float lon1, Float lat2, Float lon2, boolean inMeters) {
        if (lat1 == null || lat2 == null | lon1 == null || lon2 == null) {
            return null;
        }
        float R = inMeters ? 6371000 : 6371; // metres
        double lat1Rad = Tools.degToRad(lat1);
        double lat2Rad = Tools.degToRad(lat2);
        double lon1Rad = Tools.degToRad(lon1);
        double lon2Rad = Tools.degToRad(lon2);
        double deltaLat = (lat2Rad - lat1Rad);
        double deltaLon = (lon2Rad - lon1Rad);
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // in metres
        return d;
    }

}
