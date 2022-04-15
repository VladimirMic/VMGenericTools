/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vm.datatools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public static String floatsToString(float[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = Float.toString(array[0]);
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                ret += delimiter + array[i];
            }
        }
        return ret;
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

    public static double[] floatsToDoubles(List<Float> list) {
        double[] ret = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Float f = list.get(i);
            ret[i] = f;
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
            ret[i] = Float.parseFloat(rows[i]);
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
        String ret = DataTypeConvertor.floatsToString(array[0], columnDelimiter);
        for (int i = 1; i < array.length; i++) {
            ret += "\n" + DataTypeConvertor.floatsToString(array[i], columnDelimiter);
        }
        return ret;
    }

    public static float[][] stringSquareMatrixToFloats(String path, int rowNumber, String delimiter) {
        BufferedReader br = null;
        float[][] ret = null;
        if (rowNumber < 0) {
            rowNumber = Integer.MAX_VALUE;
        }
        try {
            br = new BufferedReader(new FileReader(path));
            try {
                for (int j = 0; j < rowNumber; j++) {
                    String line = br.readLine();
                    String[] split = line.split(delimiter);
                    if (ret == null) {
                        ret = new float[split.length][split.length];
                    }
                    for (int i = 0; i < split.length; i++) {
                        ret[i][j] = Float.parseFloat(split[i]);
                        ret[j][i] = ret[i][j];
                    }
                    if (j % 500 == 0) {
                        LOG.log(Level.INFO, "Processed: {0} lines", j);
                    }
                }
            } catch (NullPointerException e) {
                // ignore
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
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

}
