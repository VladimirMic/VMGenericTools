package vm.javatools;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vlada
 */
public class Tools {

//    public static final Integer PARALELISATION = 1;
    public static final Integer PARALELISATION = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    public static final Logger LOG = Logger.getLogger(Tools.class.getName());

    public static ExecutorService initExecutor(Integer paralelism) {
        if (paralelism == null || paralelism <= 0) {
            return initExecutor();
        }
        return Executors.newFixedThreadPool(paralelism);
    }

    public static ExecutorService initExecutor() {
        return initExecutor(PARALELISATION);
    }

    public static void sleep(long minutes) {
        LOG.log(Level.INFO, "Going to sleep for {0} minutes", minutes);
        try {
            Thread.sleep(minutes * 1000 * 60);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static void sleepSeconds(long seconds) {
        LOG.log(Level.INFO, "Going to sleep for {0} seconds", seconds);
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static void clearJavaCache() {
        try {
            Runtime run = Runtime.getRuntime();
            Process pr = run.exec("javaws -Xclearcache -silent -Xnosplash");
            pr.waitFor(); // wait for the process to complete
        } catch (IOException | InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static float getRatioOfConsumedRam(boolean print) {
        Runtime r = Runtime.getRuntime();
        long maxMemory = r.maxMemory();
        long totalMemory = r.totalMemory();
        float ret = ((float) totalMemory) / maxMemory;
        if (print) {
            LOG.log(Level.INFO, "Max memory: {0} MB. Total occupied memory: {1} MB. Ratio: {2}", new Object[]{maxMemory / 1024 / 1024, totalMemory / 1024 / 1024, ret});
        }
        return ret;
    }

    public static class ArrayIterator<T> implements Iterator<T> {

        private final T[] array;
        private int currPos;
        private final int maxCount;

        public ArrayIterator(T[] array) {
            this(array, Integer.MAX_VALUE);
        }

        public ArrayIterator(T[] array, int maxCount) {
            this.array = array;
            currPos = 0;
            this.maxCount = Math.min(maxCount, array.length);
        }

        @Override
        public boolean hasNext() {
            return currPos < maxCount;
        }

        @Override
        public T next() {
            currPos++;
            return array[currPos - 1];
        }

    }

}
