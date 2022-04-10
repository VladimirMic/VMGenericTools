/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vm.datatools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vlada
 */
public class Tools {

    private static final Random RANDOM = new Random();
    private static final Logger LOG = Logger.getLogger(vm.math.Tools.class.getName());
    public static final Integer PARALELISATION = 24;

    public static List<Integer> transformStringsToIntegers(List<String> set) {
        List<Integer> ret = new ArrayList<>();
        for (int i = 0; i < set.size(); i++) {
            ret.add(Integer.parseInt(set.get(i)));
        }
        return ret;
    }

    public static List<String>[] parseCsvKeysValues(String path) {
        return parseCsv(path, 2, true);
    }

    public static List<String>[] parseCsvTriplets(String path) {
        return parseCsv(path, 3, true);
    }

    public static long[] parseArrayOfLongs(String string, String delimiter) {
        String[] split = string.split(delimiter);
        long[] ret = new long[split.length];
        for (int i = 0; i < split.length; i++) {
            ret[i] = Long.parseLong(split[i]);
        }
        return ret;
    }

    public static List<String>[] parseCsv(String path, int columnsNumber, boolean filterOnlyNumberOfColumns) {
        return parseCsv(path, columnsNumber, ";", filterOnlyNumberOfColumns);
    }

    public static List<String>[] parseCsv(String path, int columnsNumber, String delimiter, boolean filterOnlyNumberOfColumns) {
        return parseCsv(path, columnsNumber, -1, delimiter, filterOnlyNumberOfColumns);
    }

    public static float[][] parseSquareMatrixOfFloats(String path, int rowNumber, String delimiter) {
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

    public static List<String>[] parseCsv(String path, int columnNumber, int rowNumber, String delimiter, boolean filterOnlyNumberOfColumns) {
        if (rowNumber < 0) {
            rowNumber = Integer.MAX_VALUE;
        }
        if (columnNumber < 0) {
            columnNumber = Integer.MAX_VALUE;
        }
        BufferedReader br = null;
        List<String>[] ret = null;
        try {
            br = new BufferedReader(new FileReader(path));
            try {
                for (int counter = 0; counter < rowNumber; counter++) {
                    String line = br.readLine();
                    String[] split = line.split(delimiter);
                    if (ret == null) {
                        columnNumber = Math.min(split.length, columnNumber);
                        ret = new List[columnNumber];
                        for (int i = 0; i < columnNumber; i++) {
                            ret[i] = new ArrayList<>();
                        }
                    }
                    boolean add = (filterOnlyNumberOfColumns && split.length == columnNumber) || !filterOnlyNumberOfColumns;
                    if (add) {
                        for (int i = 0; i < Math.min(columnNumber, split.length); i++) {
                            ret[i].add(split[i]);
                        }
                    }
                    if (counter % 100 == 0) {
                        LOG.log(Level.INFO, "Processed: {0} lines", counter);
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

    public static SortedMap<String, String> parseCsvMapStrings(String path) {
        BufferedReader br = null;
        SortedMap<String, String> ret = new TreeMap<>();
        try {
            br = new BufferedReader(new FileReader(path));
            try {
                while (true) {
                    String line = br.readLine();
                    String[] split = line.split(";");
                    if (split.length == 2) {
                        ret.put(split[0], split[1]);
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

    public static SortedMap<Float, Float> parseCsvMapFloats(String path) {
        SortedMap<Float, Float> ret = new TreeMap<>();
        List<String>[] csv = parseCsvKeysValues(path);
        int count = csv[0].size();
        for (int i = 0; i < count; i++) {
            Float dist = Float.parseFloat(csv[0].get(i));
            Float prob = Float.parseFloat(csv[1].get(i));
            ret.put(dist, prob);
        }
        return ret;
    }

    public static SortedMap<String, String> parseCsvMap(String path) {
        SortedMap<String, String> ret = new TreeMap<>();
        if (!new File(path).exists()) {
            return ret;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            try {
                while (true) {
                    String line = br.readLine();
                    String[] split = line.split(";");
                    if (split.length == 2) {
                        ret.put(split[0], split[1]);
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

    public static void printMap(Map map) {
        Set keySet = map.keySet();
        for (Object key : keySet) {
            Object value = map.get(key);
            System.out.println(key + ";" + value);
        }
    }

    public static void printCollection(Collection collection, String separator) {
        for (Object obj : collection) {
            if (obj instanceof int[]) {
                int[] intArray = (int[]) obj;
                for (int j = 0; j < intArray.length; j++) {
                    System.err.print(intArray[j] + separator + " ");
                }
                System.err.println();
            } else if (obj instanceof int[]) {
                int[] intArray = (int[]) obj;
                for (int j = 0; j < intArray.length; j++) {
                    System.err.print(intArray[j] + separator + " ");
                }
                System.err.println();
            } else {
                System.err.print(obj.toString() + separator);
            }
        }
    }

    public static void printCollection(Collection collection) {
        printCollection(collection, ";");
    }

    public static int[] stringToIntArray(String string) {
        String[] split = string.split(",");
        int[] ret = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            ret[i] = Integer.parseInt(split[i]);
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

    public static String floatsToCsvString(float[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = Float.toString(array[0]);
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                ret += "," + array[i];
            }
        }
        return ret;
    }

    public static String bytesToCsvString(byte[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        String ret = Byte.toString(array[0]);
        if (array.length > 1) {
            for (int i = 1; i < array.length; i++) {
                ret += "," + array[i];
            }
        }
        return ret;
    }

    public static void printMatrix(float[][] m) {
        for (int i = 0; i < m.length; i++) {
            float[] column = m[i];
            for (int j = 0; j < column.length; j++) {
                System.err.print(column[j] + ";");
            }
            System.err.println();
        }
    }

    public static void printArray(float[] array) {
        for (int i = 0; i < array.length; i++) {
            float val = array[i];
            System.err.print(val + ";");
        }
        System.err.println();
    }

    public static void printAsPairs(int[] selectedIndexes, OutputStream out) {
        try {
            for (int i = 0; i < selectedIndexes.length - 1; i = i + 2) {
                String s = "\"" + selectedIndexes[i] + "\";\"" + selectedIndexes[i + 1] + "\"\n";
                out.write(s.getBytes());
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static float[] listStringsToArrayFloats(List<String> list) {
        float[] ret = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = Float.parseFloat(list.get(i));
        }
        return ret;
    }

    public static int[] listStringsToArrayInts(List<String> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = Integer.parseInt(list.get(i));
        }
        return ret;
    }

    public static Object randomObject(List objects) {
        return objects.get(RANDOM.nextInt(objects.size()));
    }

    public static List<Integer> arrayToList(int[] values) {
        List<Integer> ret = new ArrayList<>();
        for (int i : values) {
            ret.add(i);
        }
        return ret;
    }

    public static List<Float> arrayToList(float[] values) {
        List<Float> ret = new ArrayList<>();
        for (float i : values) {
            ret.add(i);
        }
        return ret;
    }

    public static void printMapValues(Map<Float, Integer> counts, boolean newLines) {
        for (Map.Entry<Float, Integer> entry : counts.entrySet()) {
            Integer value = entry.getValue();
            if (newLines) {
                System.out.println(value + ";");
            } else {
                System.out.print(value + ";");
            }
        }
    }

    public static List filterList(List list, int[] indexes) {
        List<Object> ret = new ArrayList();
        for (int idx : indexes) {
            ret.add(list.get(idx));
        }
        return ret;
    }

    public static class IntArraySameLengthsComparator implements Comparator<int[]>, Serializable {

        private static final long serialVersionUID = 159756321810L;

        @Override
        public int compare(int[] o1, int[] o2) {
            for (int i = 0; i < o1.length; i++) {
                int ret = Integer.compare(o1[i], o2[i]);
                if (ret != 0) {
                    return ret;
                }
            }
            return 0;
        }

    }

    public static class MapByValueComparator implements Comparator<Map.Entry<Integer, Float>> {

        @Override
        public int compare(Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2) {
            float val1 = o1.getValue();
            float val2 = o2.getValue();
            if (val1 != val2 && (!Float.isNaN(val1) || !Float.isNaN(val2))) {
                return Float.compare(val1, val2);
            }
            Integer key1 = o1.getKey();
            Integer key2 = o2.getKey();
            return key1.compareTo(key2);
        }

    }

    public static Float getRandom(Float[] array) {
        int rnd = RANDOM.nextInt(array.length);
        return array[rnd];
    }

    public static SortedSet<Map.Entry<Integer, Float>> evaluateSumsPerRow(float[][] matrix, boolean sortedList) {
        SortedSet<Map.Entry<Integer, Float>> ret = new TreeSet<>(new Tools.MapByValueComparator());
        for (Integer i = 0; i < matrix.length; i++) {
            float[] row = matrix[i];
            Float sum = 0f;
            for (int j = 0; j < row.length; j++) {
                sum += row[j];
            }
            ret.add(new AbstractMap.SimpleEntry<>(i, sum));
        }
        return ret;
    }

    public static String removeQuotes(String string) {
        if (string.startsWith("\"") && string.endsWith("\"")) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }

    public static List<Object> getObjectsFromIterator(int fromPosition, int toPosition, Iterator it) {
        List<Object> ret = new ArrayList<>();
        int counter = 0;
        for (counter = 0; counter < fromPosition && it.hasNext(); counter++) {
            it.next();
        }
        if (counter != fromPosition) {
            return ret;
        }
        for (counter = fromPosition; counter < toPosition && it.hasNext(); counter++) {
            ret.add(it.next());
        }
        return ret;
    }

}
