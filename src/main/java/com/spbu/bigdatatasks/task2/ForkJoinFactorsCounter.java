package com.spbu.bigdatatasks.task2;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinFactorsCounter extends FactorsCounter {

    private class Task extends RecursiveTask<Long> {
        static final int THRESHOLD = 25;

        final List<Long> numbers;
        final int start;
        final int end;

        Task(List<Long> numbers) {
            this(numbers, 0, numbers.size());
        }

        Task(List<Long> numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long totalFactorsAmount = 0L;
            if (end - start < THRESHOLD) {
                List<Long> factors;
                for (int i = start; i < end; i++) {
                    factors = PrimeFactors.factorize(numbers.get(i));
                    totalFactorsAmount += factors.size();

                    log.info("Number: " + Long.toUnsignedString(numbers.get(i)) + " factors: " + factors);
                }
                return totalFactorsAmount;
            } else {
                int split = (end + start) / 2;
                Task left = new Task(numbers, start, split);
                left.fork();
                Task right = new Task(numbers, split, end);

                long rightResult = right.compute();
                long leftResult = left.join();

                return leftResult + rightResult;
            }
        }
    }

    @Override
    public long readAndCount(String path) {
        List<Long> numbers = readAllNumbers(path);
        Task task = new Task(numbers);
        return new ForkJoinPool().invoke(task);
    }
}
