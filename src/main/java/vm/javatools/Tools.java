package vm.javatools;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

    private static boolean sleepDuringTheNight = true;
//    public static final Integer PARALELISATION = 1;
    public static final Integer PARALELISATION = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    public static final Logger LOG = Logger.getLogger(Tools.class.getName());
    public static final DateFormat DF_DDMMYYYY_HHMM = new SimpleDateFormat("dd.MM.YYYY HH:mm");
    public static final int[] HOURS_OF_PAUSE = new int[]{21, 10};

    public static boolean isSleepDuringTheNight() {
        return sleepDuringTheNight;
    }

    public static void setSleepDuringTheNight(boolean sleepDuringTheNight) {
        Tools.sleepDuringTheNight = sleepDuringTheNight;
    }

    public static ExecutorService initExecutor(Integer paralelism) {
        if (paralelism == null || paralelism <= 0) {
            return initExecutor();
        }
        return Executors.newFixedThreadPool(paralelism);
    }

    public static void sleepDuringTheNight() {
        LOG.log(Level.INFO, "Sleeping during the night is set to {0}", sleepDuringTheNight);
        if (!sleepDuringTheNight) {
            return;
        }
        LOG.log(Level.INFO, "Sleeping between {0} to {1} (hours)", new Object[]{HOURS_OF_PAUSE[0], HOURS_OF_PAUSE[1]});
        Calendar now = new GregorianCalendar();
        int h = now.get(Calendar.HOUR_OF_DAY);
        if (h >= HOURS_OF_PAUSE[0] || h < HOURS_OF_PAUSE[1]) {
            Calendar until = new GregorianCalendar();
            until.set(Calendar.HOUR_OF_DAY, HOURS_OF_PAUSE[1]);
            until.clear(Calendar.MINUTE);
            until.clear(Calendar.SECOND);
            if (now.after(until)) {
                until.add(Calendar.DATE, 1);
            }
            long sleep = until.getTimeInMillis() - now.getTimeInMillis();
            sleepSeconds(sleep / 1000);
        }
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
        LOG.log(Level.INFO, "Going to sleep for {0} seconds, which is {1} minutes", new Object[]{seconds, seconds / 60f});
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

    public static String getCurrDateAndTime() {
        return DF_DDMMYYYY_HHMM.format(new Date());
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
