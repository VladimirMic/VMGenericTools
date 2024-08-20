package vm.datatools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
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
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Vlada
 */
public class Tools {

    private static final Random RANDOM = new Random();
    private static final Logger LOG = Logger.getLogger(vm.math.Tools.class.getName());
    private static final Float IMPLICIT_MAX_MEMORY_OCCUPATION_FOR_DATA_READING = 80F;

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
        if (!new File(path).exists()) {
            throw new IllegalArgumentException("File  " + path + " does not exist");
        }
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
                for (int counter = 1; counter < rowNumber; counter++) {
                    String line = br.readLine();
                    String[] split = line.split(delimiter);
                    if (ret == null) {
                        if (columnNumber == Integer.MAX_VALUE) {
                            columnNumber = split.length;
                        }
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

    public static List<String[]> parseCsvRowOriented(String path, String delimiter) {
        BufferedReader br = null;
        List<String[]> ret = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(path));
            try {
                String line = "";
                while (line != null) {
                    line = br.readLine();
                    String[] split = line.split(delimiter);
                    for (int i = 0; i < split.length; i++) {
                        split[i] = removeQuotes(split[i]);
                    }
                    ret.add(split);
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

    public static SortedMap<String, String[]> parseCsvMapKeyValues(String path) {
        BufferedReader br = null;
        SortedMap<String, String[]> ret = new TreeMap<>();
        try {
            InputStreamReader inputStreamReader;
            if (path.toLowerCase().endsWith("gz")) {
                inputStreamReader = new InputStreamReader(new GZIPInputStream(new FileInputStream(path)));
            } else {
                inputStreamReader = new InputStreamReader(new FileInputStream(path));
            }
            br = new BufferedReader(inputStreamReader);
            try {
                while (true) {
                    String line = br.readLine();
                    String[] split = line.split(";");
                    if (split.length > 0) {
                        ret.put(Tools.removeQuotes(split[0]), split);
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

    public static SortedMap<String, float[]> parseCsvMapKeyFloatValues(String path) {
        BufferedReader br = null;
        SortedMap<String, float[]> ret = new TreeMap<>();
        try {
            br = new BufferedReader(new FileReader(path));
            try {
                while (true) {
                    String line = br.readLine();
                    String[] split = line.split(";");
                    float[] floats = new float[split.length - 1];
                    for (int i = 1; i < split.length; i++) {
                        split[i] = Tools.removeQuotes(split[i]);
                        floats[i - 1] = Float.parseFloat(split[i]);
                    }
                    String key = Tools.removeQuotes(split[0]);
                    ret.put(key, floats);
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

    public static TreeMap<Float, Float> parseCsvMapFloats(String path) {
        TreeMap<Float, Float> ret = new TreeMap<>();
        List<String>[] csv = parseCsvKeysValues(path);
        int count = csv[0].size();
        for (int i = 0; i < count; i++) {
            try {
                Float col0 = Float.valueOf(csv[0].get(i));
                Float col1 = Float.valueOf(csv[1].get(i));
                ret.put(col0, col1);
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING, "Number format exception for index {2} and values {0}, {1}", new Object[]{csv[0].get(i), csv[1].get(i), i});
            }
        }
        return ret;
    }

    public static SortedMap<String, Float> parseCsvMapStringFloat(String path) {
        SortedMap<String, Float> ret = new TreeMap<>();
        List<String>[] csv = parseCsvKeysValues(path);
        int count = csv[0].size();
        for (int i = 0; i < count; i++) {
            String col0 = csv[0].get(i);
            Float col1 = Float.parseFloat(csv[1].get(i));
            ret.put(col0, col1);
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

    public static String mapAsCSVString(Map<String, Object> map, String pairDelimiter, String keyDelimiter) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(keyDelimiter).append(entry.getValue().toString()).append(pairDelimiter);
        }
        return sb.toString();
    }

    public static void printMapOfKeyFloatValues(Map<Object, float[]> map) {
        Iterator<Map.Entry<Object, float[]>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, float[]> next = it.next();
            System.err.print(next.getKey().toString() + ";");
            float[] value = next.getValue();
            printArray(value, true);
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
            }
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

    public static void printMatrixWithRowHeaders(String[] rowHeaders, float[][] m) {
        for (int i = 0; i < m.length; i++) {
            System.err.print(rowHeaders[i] + ";");
            float[] column = m[i];
            for (int j = 0; j < column.length; j++) {
                System.err.print(column[j] + ";");
            }
            System.err.println();
        }
    }

    public static void printArray(float[] array) {
        printArray(array, true);
    }

    public static void printArray(float[] array, String separator, boolean newline, PrintStream ps) {
        for (int i = 0; i < array.length; i++) {
            float val = array[i];
            ps.print(val + separator);
        }
        if (newline) {
            ps.println();
        }
    }

    public static void printArray(float[] array, boolean newline) {
        Tools.printArray(array, ";", newline, System.err);
    }

    public static void printArray(double[] array, boolean newline) {
        for (int i = 0; i < array.length; i++) {
            double val = array[i];
            System.err.print(val + ";");
        }
        if (newline) {
            System.err.println();
        }
    }

    public static void printArray(Object[] array) {
        printArray(array, ";", true, System.err);
    }

    public static void printArray(Object[] array, String separator, boolean newline, PrintStream ps) {
        for (int i = 0; i < array.length; i++) {
            ps.print(array[i].toString() + separator);
        }
        if (newline) {
            ps.println();
        }
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

    public static Object[] randomUniqueObjects(List objects, int count) {
        if (count < objects.size()) {
            throw new IllegalArgumentException("List has just " + objects.size() + " objects. Cannot return " + count + " unique objects");
        }
        if (count == objects.size()) {
            return objects.toArray();
        }
        Set ret = new HashSet();
        while (ret.size() < count) {
            ret.add(randomObject(objects));
        }
        return ret.toArray();
    }

    public static List<Integer> arrayToList(int[] values) {
        List<Integer> ret = new ArrayList<>();
        for (int i : values) {
            ret.add(i);
        }
        return ret;
    }

    public static List<Object> arrayToList(Object[] values) {
        List<Object> ret = new ArrayList<>();
        for (Object i : values) {
            if (i == null) {
                String dsf = "";
            }
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

    public static TreeSet<Object> arrayToSet(Object[] values) {
        TreeSet<Object> ret = new TreeSet<>();
        for (Object i : values) {
            ret.add(i);
        }
        return ret;
    }

    public static List<String> arrayToList(String[] values) {
        List<String> ret = new ArrayList<>();
        for (String i : values) {
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

    public static List<Object> filterMapValues(Map map, Collection keys) {
        return Tools.filterMapValues(map, keys, false);
    }

    public static List<Object> filterMapValues(Map map, Collection keys, boolean addKey) {
        List<Object> ret = new ArrayList<>();
        for (Object key : keys) {
            Object value = map.get(key);
            if (addKey) {
                value = new AbstractMap.SimpleEntry<>(key, value);
            }
            ret.add(value);
        }
        return ret;
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

    public static Object[] concatArrays(Object[] array1, Object obj) {
        List<Object> list = new ArrayList<>();
        list.addAll(Arrays.asList(array1));
        list.add(obj);
        return list.toArray(array1);
    }

    public static Object[] concatArrays(Object obj, Object[] array1) {
        List<Object> list = new ArrayList<>();
        list.add(obj);
        list.addAll(Arrays.asList(array1));
        return list.toArray(array1);
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

    public static boolean isZeroInArray(float[] dists) {
        for (float dist : dists) {
            if (dist == 0) {
                return true;
            }
        }
        return false;
    }

    public static SortedSet<Map.Entry<Integer, Float>> evaluateSumsPerRow(float[][] matrix) {
        SortedSet<Map.Entry<Integer, Float>> ret = new TreeSet<>(new Tools.MapByFloatValueComparator());
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

    public static float[] vectorPreffix(float[] vector, int pcaPreffixLength) {
        int length = Math.min(vector.length, pcaPreffixLength);
        float[] ret = new float[length];
        System.arraycopy(vector, 0, ret, 0, length);
        return ret;
    }

    public static List<Object> getAndRemoveFirst(List<Object> list, int countToRemove) {
        if (list.size() < countToRemove) {
            throw new IllegalArgumentException("The size of the list " + list.size() + " is smaller than the number of required objects" + countToRemove);
        }
        List<Object> ret = new ArrayList<>();
        for (int i = 0; i < countToRemove; i++) {
            ret.add(list.remove(0));
        }
        return ret;
    }

    public static float[] splitStringFloatVector(String string, String separator) {
        String[] array = string.split(separator);
        float[] ret = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            ret[i] = Float.parseFloat(array[i]);
        }
        return ret;
    }

    public static List<Object> createAllPairs(List<Object> pivots) {
        List<Object> ret = new ArrayList<>();
        for (int i = 0; i < pivots.size() - 1; i++) {
            Object p1 = pivots.get(i);
            for (int j = i + 1; j < pivots.size(); j++) {
                Object p2 = pivots.get(j);
                ret.add(p1);
                ret.add(p2);
            }
        }
        return ret;
    }

    public static final float[] get8Angles(float[] sixDists, boolean inDegress) {
        float[] ret = new float[8]; // beta1, delta2, gamma2, alphao, deltao, betaq, alphaq, gamma1
        float[] angles = vm.math.Tools.evaluateAnglesOfTriangle(sixDists[0], sixDists[1], sixDists[4], inDegress); //a, b, e
        ret[0] = angles[1]; // beta1
        ret[3] = angles[0]; // delta2
        angles = vm.math.Tools.evaluateAnglesOfTriangle(sixDists[1], sixDists[2], sixDists[5], inDegress); //b, c, f
        ret[2] = angles[1]; // gamma2
        ret[5] = angles[0]; // betaq
        angles = vm.math.Tools.evaluateAnglesOfTriangle(sixDists[2], sixDists[3], sixDists[4], inDegress); //c, d, e
        ret[4] = angles[1]; // deltao
        ret[7] = angles[0]; // gamma1
        angles = vm.math.Tools.evaluateAnglesOfTriangle(sixDists[3], sixDists[0], sixDists[5], inDegress); //d, a, f
        ret[6] = angles[1]; // alphaq
        ret[1] = angles[0]; // delta2
        return ret;
    }

    public static String getDateYYYYMMDD() {
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
        return df.format(new Date());
    }

    public static String getDateYYYYMM() {
        DateFormat df = new SimpleDateFormat("yyyy_MM");
        return df.format(new Date());
    }

    public static List truncateList(List list, long finalSize) {
        int size = list.size();
        while (size > finalSize) {
            size--;
            list.remove(size);
        }
        return list;
    }

    public static Integer parseInteger(Object object) {
        try {
            float f = parseFloat(object);
            String s = Float.toString(f);
            s = s.substring(0, s.indexOf("."));
            int i = Integer.valueOf(s);
            if (f == i) {
                return i;
            }
        } catch (Exception e) {
        }
        return null;

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

    public static double[] getPrefixOfVector(double[] array, int finalDimensions) {
        if (finalDimensions > array.length) {
            throw new IllegalArgumentException("Cannot extend the vector");
        }
        double[] ret = new double[finalDimensions];
        System.arraycopy(array, 0, ret, 0, finalDimensions);
        return ret;
    }

    public static Float random(Float[] array) {
        int rnd = RANDOM.nextInt(array.length);
        return array[rnd];
    }

    public static List<Object> randomUniformNonZeroDistance(Iterator<Object> it, int estimatedSizeOfIterator, int count) {
        int batchSize = estimatedSizeOfIterator / count;
        LOG.log(Level.INFO, "Batch size {0}", batchSize);
        int idx = RANDOM.nextInt(batchSize);
        List<Object> ret = new ArrayList<>();
        for (int i = 0; it.hasNext(); i++) {
            Object o = it.next();
            if (i % batchSize == idx) {
                ret.add(o);
                LOG.log(Level.INFO, "Index {0}, selected {1}", new Object[]{i, ret.size()});
                idx = RANDOM.nextInt(batchSize);
            }
        }
        LOG.log(Level.INFO, "Selected {0} objects", ret.size());
        return ret;
    }

    public static List<Object> randomUniform(Iterator<Object> it, int estimatedSizeOfIterator, int count) {
        int batchSize = estimatedSizeOfIterator / count;
        LOG.log(Level.INFO, "Batch size {0}", batchSize);
        int idx = RANDOM.nextInt(batchSize);
        List<Object> ret = new ArrayList<>();
        int lastBatch = -1;
        for (int i = 0; it.hasNext(); i++) {
            Object o = it.next();
            if (i % batchSize == idx && i / batchSize > lastBatch) {
                lastBatch = i / batchSize;
                ret.add(o);
                LOG.log(Level.INFO, "Index {0}, selected {1}", new Object[]{i, ret.size()});
                idx = RANDOM.nextInt(batchSize);
            }
        }
        LOG.log(Level.INFO, "Selected {0} objects", ret.size());
        return ret;
    }

    public static List<Object> getObjectsFromIterator(Iterator it) {
        return Tools.getObjectsFromIterator(it, Integer.MAX_VALUE);
    }

    public static List<Object> getObjectsFromIterator(float memoryLimitInPercentages, Iterator it) {
        return Tools.getObjectsFromIterator(it, Integer.MAX_VALUE, memoryLimitInPercentages);
    }

    public static List<Object> getObjectsFromIterator(Iterator it, int maxCount) {
        return Tools.getObjectsFromIterator(it, maxCount, IMPLICIT_MAX_MEMORY_OCCUPATION_FOR_DATA_READING);

    }

    public static List<Object> getObjectsFromIterator(Iterator it, int maxCount, float memoryLimitInPercentages) {
        if (it == null) {
            return null;
        }
        if (memoryLimitInPercentages < 0) {
            memoryLimitInPercentages = 1000;
        }
        List<Object> ret = new ArrayList<>();
        if (maxCount == 0) {
            return ret;
        }
        for (int counter = 0; counter < maxCount && it.hasNext(); counter++) {
            ret.add(it.next());
            if (ret.size() % 500000 == 0) {
                System.gc();
                float ram = vm.javatools.Tools.getRatioOfConsumedRam(true) * 100;
                if (ram > memoryLimitInPercentages) {
                    if (maxCount == Integer.MAX_VALUE) {
                        LOG.log(Level.INFO, "Loaded {0} objects from iterator. Terminaning batch reading due to occupied ram RAM: ({1} %, limit is {2})", new Object[]{ret.size(), ram, memoryLimitInPercentages});
                    } else {
                        LOG.log(Level.WARNING, "Loaded {0} objects from iterator. Terminaning batch reading due to occupied ram RAM: ({1} %, limit is {2})", new Object[]{ret.size(), ram, memoryLimitInPercentages});
                    }
                    return ret;
                } else {
                    LOG.log(Level.INFO, "Read {0} objects from iterator. RAM occupation: {1} % out of {2} % permited", new Object[]{ret.size(), ram, memoryLimitInPercentages});
                }
            }
        }
        float ram = vm.javatools.Tools.getRatioOfConsumedRam(false);
        LOG.log(Level.INFO, "Returning {0} objects from iterator. RAM occupation:", new Object[]{ret.size(), ram});
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

    public static class MapByFloatValueComparator<T extends Comparable> implements Comparator<Map.Entry<T, Float>> {

        @Override
        public int compare(Map.Entry<T, Float> o1, Map.Entry<T, Float> o2) {
            float val1 = o1.getValue();
            float val2 = o2.getValue();
            if (val1 != val2) {
                return Float.compare(val1, val2);
            }
            T key1 = o1.getKey();
            T key2 = o2.getKey();
            return key1.compareTo(key2);
        }
    }

    public static class MapByValueIntComparator<T extends Comparable> implements Comparator<Map.Entry<T, Integer>> {

        @Override
        public int compare(Map.Entry<T, Integer> o1, Map.Entry<T, Integer> o2) {
            int val1 = o1.getValue();
            int val2 = o2.getValue();
            if (val1 != val2) {
                return Integer.compare(val1, val2);
            }
            T key1 = o1.getKey();
            T key2 = o2.getKey();
            return key1.compareTo(key2);
        }
    }

    public static class MapByValueComparatorWithOwnValueComparator<T> implements Comparator<Map.Entry<Object, T>> {

        private final Comparator<T> comp;

        public MapByValueComparatorWithOwnValueComparator(Comparator<T> comp) {
            this.comp = comp;
        }

        @Override
        public int compare(Map.Entry<Object, T> o1, Map.Entry<Object, T> o2) {
            int ret = comp.compare(o1.getValue(), o2.getValue());
            if (ret != 0) {
                return ret;
            }
            return o1.getKey().toString().compareTo(o2.getKey().toString());
        }
    }

    public static class MapByValueComparatorWithOwnKeyComparator<T> implements Comparator<Map.Entry<T, Float>> {

        private final Comparator<T> comp;

        public MapByValueComparatorWithOwnKeyComparator(Comparator<T> comp) {
            this.comp = comp;
        }

        public MapByValueComparatorWithOwnKeyComparator() {
            this.comp = (Comparator<T>) new ObjectArrayIdentityComparator();
        }

        @Override
        public int compare(Map.Entry<T, Float> o1, Map.Entry<T, Float> o2) {
            float val1 = o1.getValue();
            float val2 = o2.getValue();
            if (val1 != val2 && (!Float.isNaN(val1) || !Float.isNaN(val2))) {
                return Float.compare(val1, val2);
            }
            T key1 = o1.getKey();
            T key2 = o2.getKey();
            return comp.compare(key1, key2);
        }
    }

    public static class FloatVectorComparator implements Comparator<float[]> {

        @Override
        public int compare(float[] o1, float[] o2) {
            if (o1.length != o2.length) {
                throw new IllegalArgumentException("Vector do not have the same length");
            }
            for (int i = 0; i < o1.length; i++) {
                int ret = Float.compare(o1[i], o2[i]);
                if (ret != 0) {
                    return ret;
                }
            }
            return 0;
        }
    }

    public static class ObjectArrayIdentityComparator implements Comparator<Object[]> {

        @Override
        public int compare(Object[] o1, Object[] o2) {
            if ((o1 == null && o2 != null) || (o1 != null && o2 == null)) {
                return -1;
            }
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1.length != o2.length) {
                return -1;
            }
            for (int i = 0; i < o1.length; i++) {
                Object oi1 = o1[i];
                Object oi2 = o2[i];
                if (!oi1.equals(oi2)) {
                    return -1;
                }
            }
            return 0;
        }

    }

    public static boolean isEmptyString(String string) {
        return string == null || string.trim().equals("");
    }

    public static String removeQuotes(String string) {
        if (string == null) {
            return null;
        }
        string = string.trim();
        if (string.startsWith("\"") && string.endsWith("\"")) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }

    public static Float parseFloat(String string) {
        try {
            string = Tools.removeQuotes(string);
            if (string == null || string.isBlank() || string.toLowerCase().equals("nan") || string.endsWith("D")) {
                return null;
            }
            return Float.valueOf(string);
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean isParseableToFloats(Object[] array) {
        for (Object o : array) {
            Float floatValue = parseFloat(o);
            if (floatValue == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean isParseableToIntegers(Object[] array) {
        for (Object o : array) {
            Integer iValue = parseInteger(o);
            if (iValue == null) {
                return false;
            }
        }
        return true;
    }

    public static Float parseFloat(Object object) {
        return parseFloat(object.toString());
    }

}
