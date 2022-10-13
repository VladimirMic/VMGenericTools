package vm.datatools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<String>[] parseCsvKeysValues(String path) {
        return parseCsv(path, 2, true);
    }

    public static List<String>[] parseCsvTriplets(String path) {
        return parseCsv(path, 3, true);
    }

    public static List<String>[] parseCsv(String path, int columnsNumber, boolean filterOnlyNumberOfColumns) {
        return parseCsv(path, columnsNumber, ";", filterOnlyNumberOfColumns);
    }

    public static List<String>[] parseCsv(String path, int columnsNumber, String delimiter, boolean filterOnlyNumberOfColumns) {
        return parseCsv(path, columnsNumber, -1, delimiter, filterOnlyNumberOfColumns);
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

    public static int getIndexOfSmallest(double[] array, SortedSet<Integer> indexesToCheck) {
        int ret = -1;
        double val = Double.MAX_VALUE;
        for (int i : indexesToCheck) {
            if (array[i] < val) {
                ret = i;
                val = array[i];
            };
        }
        return ret;
    }

    public static int[] sortArray(double[] array) {
        int[] ret = new int[array.length];
        SortedSet<Integer> indexesToCheck = new TreeSet<>();
        for (int i = 0; i < array.length; i++) {
            indexesToCheck.add(i);
        }
        for (int i = 0; i < ret.length; i++) {
            int smallestIndex = getIndexOfSmallest(array, indexesToCheck);
            ret[i] = smallestIndex;
            indexesToCheck.remove(smallestIndex);
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

    public static float[][] parseFloatMatrix(String path, int rowNumber, String delimiter) {
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

    public static <T> T[] concatArrays(T[] array1, T[] array2) {
        T[] ret = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, ret, array1.length, array2.length);
        return ret;
    }

    public static <T> T[] copyArray(T[] array) {
        T[] ret = Arrays.copyOf(array, array.length);
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

    public static class MapByValueComparator<T extends Comparable> implements Comparator<Map.Entry<T, Float>> {

        @Override
        public int compare(Map.Entry<T, Float> o1, Map.Entry<T, Float> o2) {
            float val1 = o1.getValue();
            float val2 = o2.getValue();
            if (val1 != val2 && (!Float.isNaN(val1) || !Float.isNaN(val2))) {
                return Float.compare(val1, val2);
            }
            T key1 = o1.getKey();
            T key2 = o2.getKey();
            return key1.compareTo(key2);
        }

    }

    public static double[] getFirstValuesOfVector(double[] array, int finalDimensions) {
        if (finalDimensions > array.length) {
            throw new IllegalArgumentException("Cannot extend the vector");
        }
        double[] ret = new double[finalDimensions];
        System.arraycopy(array, 0, ret, 0, finalDimensions);
        return ret;
    }

    public static Float getRandom(Float[] array) {
        int rnd = RANDOM.nextInt(array.length);
        return array[rnd];
    }

    public static String removeQuotes(String string) {
        if (string.startsWith("\"") && string.endsWith("\"")) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }

    public static List<Object> getObjectsFromIterator(Iterator it) {
        return Tools.getObjectsFromIterator(0, Integer.MAX_VALUE, it);
    }

    public static List<Object> getObjectsFromIterator(Iterator it, int maxCount) {
        return Tools.getObjectsFromIterator(0, maxCount, it);
    }

    public static List<Object> getObjectsFromIterator(int fromPosition, int toPosition, Iterator it) {
        List<Object> ret = new ArrayList<>();
        int counter;
        for (counter = 0; counter < fromPosition && it.hasNext(); counter++) {
            it.next();
        }
        if (counter != fromPosition) {
            return ret;
        }
        for (counter = fromPosition; counter < toPosition && it.hasNext(); counter++) {
            ret.add(it.next());
            if (ret.size() % 10000 == 0) {
                LOG.log(Level.INFO, "Read {0} objects from iterator", ret.size());
            }
        }
        return ret;
    }

    public static float[][] shrinkMatrix(double[][] matrix, int rowCount, int columnCount) {
        float[][] ret = new float[rowCount][columnCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                ret[i][j] = (float) matrix[i][j];
            }
        }
        return ret;
    }

    public static float[][] shrinkMatrix(float[][] matrix, int rowCount, int columnCount) {
        float[][] ret = new float[rowCount][columnCount];
        for (int i = 0; i < rowCount; i++) {
            System.arraycopy(matrix[i], 0, ret[i], 0, columnCount);
        }
        LOG.log(Level.INFO, "Matrix shrunk to size {0} x {1}", new Object[]{rowCount, columnCount});
        return ret;
    }

    public static short booleanToShort(boolean value, int shortTrue, int shortFalse) {
        return (short) (value ? shortTrue : shortFalse);
    }

}
