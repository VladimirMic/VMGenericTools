/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vm.javatools;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Vlada
 */
public class Tools {

    public static final Integer PARALELISATION = 10;

    public static ExecutorService initExecutor(Integer paralelism) {
        if (paralelism == null || paralelism <= 0) {
            return initExecutor();
        }
        return Executors.newFixedThreadPool(paralelism);
    }

    public static ExecutorService initExecutor() {
        return Executors.newCachedThreadPool();
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
