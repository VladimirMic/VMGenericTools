package vm.javatools;

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

    public static final Integer PARALELISATION = Runtime.getRuntime().availableProcessors() - 1;

    public static ExecutorService initExecutor(Integer paralelism) {
        if (paralelism == null || paralelism <= 0) {
            return initExecutor();
        }
        return Executors.newFixedThreadPool(paralelism);
    }

    public static ExecutorService initExecutor() {
        return Executors.newCachedThreadPool();
    }

    public static void sleep(long seconds) {
        System.out.println("Going to sleep for " + seconds + " seconds");
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static class MetricObjectArrayIterator implements Iterator<Object> {

        private final Object[] array;
        private int currPos;

        public MetricObjectArrayIterator(Object[] array) {
            this.array = array;
            currPos = 0;
        }

        @Override
        public boolean hasNext() {
            return currPos < array.length;
        }

        @Override
        public Object next() {
            currPos++;
            return array[currPos - 1];
        }

    }

}
