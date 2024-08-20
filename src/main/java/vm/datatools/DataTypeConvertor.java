/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vm.datatools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vlada
 */
public class DataTypeConvertor {

    private static final Logger LOG = Logger.getLogger(DataTypeConvertor.class.getName());

    public static String doublesToString(double[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = Double.toString(array[0]);
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                ret += delimiter + array[i];
            }
        }
        return ret;
    }

    public static String bytesToString(byte[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = Byte.toString(array[0]);
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                ret += delimiter + array[i];
            }
        }
        return ret;
    }

    public static String objectsToString(Object[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = array[0].toString();
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                if (array[i] != null) {
                    ret += delimiter + array[i].toString();
                }
            }
        }
        return ret;
    }

    public static String shortsToString(short[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = Short.toString(array[0]);
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                ret += delimiter + array[i];
            }
        }
        return ret;
    }

    public static String floatsToString(float[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuffer buffer = new StringBuffer(array.length * 4);
        buffer.append(Float.toString(array[0]));
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                buffer.append(delimiter);
                buffer.append(Float.toString(array[i]));
            }
        }
        return buffer.toString();
    }

    public static String intsToString(int[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = Integer.toString(array[0]);
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                ret += delimiter + array[i];
            }
        }
        return ret;
    }

    public static String longToString(long[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = Long.toString(array[0]);
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                ret += delimiter + array[i];
            }
        }
        return ret;
    }

    public static float[] doublesToFloats(double[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        float[] ret = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = (float) array[i];
        }
        return ret;
    }

    public static double[] floatsListToDoubles(List<Float> list) {
        double[] ret = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Float f = list.get(i);
            ret[i] = f;
        }
        return ret;
    }

    public static double[] floatsToDoubles(float[] vec) {
        double[] ret = new double[vec.length];
        for (int i = 0; i < ret.length; i++) {
            Float f = vec[i];
            ret[i] = f;
        }
        return ret;
    }

    public static double[] intsToDoubles(int[] vec) {
        double[] ret = new double[vec.length];
        for (int i = 0; i < ret.length; i++) {
            int number = vec[i];
            ret[i] = number;
        }
        return ret;
    }

    public static long[] stringToLongs(String string, String delimiter) {
        String[] split = string.split(delimiter);
        long[] ret = new long[split.length];
        for (int i = 0; i < split.length; i++) {
            ret[i] = Long.parseLong(split[i]);
        }
        return ret;
    }

    public static byte[] stringToBytes(String string, String delimiter) {
        String[] split = string.split(delimiter);
        byte[] ret = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            ret[i] = Byte.parseByte(split[i]);
        }
        return ret;
    }

    public static short[] stringToShorts(String string, String delimiter) {
        String[] split = string.split(delimiter);
        short[] ret = new short[split.length];
        for (int i = 0; i < split.length; i++) {
            ret[i] = Short.parseShort(split[i]);
        }
        return ret;
    }

    public static float[] stringsToFloats(List<String> list) {
        float[] ret = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = Float.parseFloat(list.get(i));
        }
        return ret;
    }

    public static float[] stringToFloats(String csvFloats, String delimiter) {
        String[] rows = csvFloats.split(delimiter);
        float[] ret = new float[rows.length];
        for (int i = 0; i < rows.length; i++) {
            try {
                ret[i] = Float.parseFloat(rows[i]);
            } catch (NumberFormatException ex) {
                Logger.getLogger(DataTypeConvertor.class.getName()).log(Level.SEVERE, "Expected delimiter: " + delimiter + ". Wrong string: {0}", rows[i]);
                throw new IllegalArgumentException();
            }
        }
        return ret;
    }

    public static int[] stringsToInts(List<String> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = Integer.parseInt(list.get(i));
        }
        return ret;
    }

    public static String floatMatrixToCsvString(float[][] array, String columnDelimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        int size = array.length * array[0].length * 4;
        size = Math.max(size, 0);
        StringBuilder ret = new StringBuilder(size);
        try {
            ret.append(DataTypeConvertor.floatsToString(array[0], columnDelimiter));
            for (int i = 1; i < array.length; i++) {
                ret.append("\n").append(DataTypeConvertor.floatsToString(array[i], columnDelimiter));
            }
            return ret.toString();
        } catch (java.lang.OutOfMemoryError ex) {
            LOG.log(Level.WARNING, "Unsufficient memory to store the matrix: {0} * {1}", new Object[]{array.length, array[0].length});
        }
        return "";
    }

    public static double[][] floatMatrixToDoubleMatrix(float[][] matrix) {
        double[][] ret = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            ret[i] = DataTypeConvertor.floatsToDoubles(matrix[i]);
        }
        return ret;
    }

    public static float[][] doubleMatrixToFloatMatrix(double[][] matrix) {
        float[][] ret = new float[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            ret[i] = DataTypeConvertor.doublesToFloats(matrix[i]);
        }
        return ret;
    }

    public static float[][] stringToFloatMatrix(String csvMatrix, String columnsDelimiter) {
        String[] rows = csvMatrix.split("\n");
        float[] row = DataTypeConvertor.stringToFloats(rows[0], columnsDelimiter);
        float[][] ret = new float[rows.length][row.length];
        ret[0] = row;
        if (rows.length > 1) {
            for (int i = 1; i < rows.length; i++) {
                ret[i] = DataTypeConvertor.stringToFloats(rows[i], columnsDelimiter);
            }
        }
        return ret;
    }

    public static int[] stringToInts(String string, String delimiter) {
        String[] split = string.split(delimiter);
        int[] ret = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            ret[i] = Integer.parseInt(split[i]);
        }
        return ret;
    }

    public static List<Integer> stringsToIntegers(List<String> strings) {
        List<Integer> ret = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            ret.add(Integer.parseInt(strings.get(i)));
        }
        return ret;
    }

    public static String[] objectsToStrings(List objects) {
        String[] ret = new String[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            ret[i] = objects.get(i).toString();
        }
        return ret;
    }

    public static double[] objectsToDoubleArray(Object[] values) {
        double[] ret = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            ret[i] = Double.parseDouble(values[i].toString());
        }
        return ret;
    }

    public static double[] doublesToPrimitiveArray(List<Double> list) {
        double[] ret = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static float[] floatToPrimitiveArray(List<Float> list) {
        float[] ret = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static float[][] listOfFloatsToMatrix(List<float[]> list) {
        if (list.isEmpty() || list.get(0).length == 0) {
            return null;
        }
        float[][] ret = new float[list.size()][list.get(0).length];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static float[] stringArrayToFloats(String[] array) {
        float[] ret = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = Float.parseFloat(array[i]);
        }
        return ret;
    }

    public static float[] objectsToPrimitiveFloats(Object[] array) {
        float[] ret = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = Float.parseFloat(array[i].toString());
        }
        return ret;
    }

    public static Float[] objectsToObjectFloats(Object[] groupsNames) {
        Float[] ret = new Float[groupsNames.length];
        for (int i = 0; i < groupsNames.length; i++) {
            ret[i] = Float.valueOf(groupsNames[i].toString());
        }
        return ret;
    }

    public static Object objectToSingularArray(Object object) {
        Class<? extends Object> aClass = object.getClass();
        Object ret = Array.newInstance(aClass, 1);
        Array.set(ret, 0, object);
        return ret;
    }

    public static Integer[] intsToIntegers(int[] ints) {
        Integer[] ret = new Integer[ints.length];
        for (int i = 0; i < ints.length; i++) {
            ret[i] = ints[i];
        }
        return ret;
    }

}
