package com.spbu.bigdatatasks.task2;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class FactorizationBenchmarks {

    private final String benchmarkPath = System.getProperty("user.dir") + "\\src\\main\\resources\\task2\\benchmark_file.txt";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private FactorsCounter counter;
    private long result;

    @Benchmark
    public void sequentialCounter() {
        counter = new SequentialFactorsCounter();
        result = counter.readAndCount(benchmarkPath);
        log.info("Sum of factors = " + result);
    }

    @Benchmark
    public void forkJoinCounter() {
        counter = new ForkJoinFactorsCounter();
        result = counter.readAndCount(benchmarkPath);
        log.info("Sum of factors = " + result);
    }

    @Benchmark
    public void akkaActorsCounter() {
        counter = new AkkaActorsFactorsCounter();
        result = counter.readAndCount(benchmarkPath);
        log.info("Sum of factors = " + result);
    }

    @Benchmark
    public void rxCounter() {
        counter = new RxFactorsCounter();
        result = counter.readAndCount(benchmarkPath);
        log.info("Sum of factors = " + result);
    }

    public static void main(String[] args) {
        Options options = new OptionsBuilder()
                .include(FactorizationBenchmarks.class.getSimpleName())
                .forks(1)
                .build();
        try {
            new Runner(options).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
}
