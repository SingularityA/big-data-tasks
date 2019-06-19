package com.spbu.bigdatatasks.task1;

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
public class SummationBenchmarks {

    private final String benchmarkPath = System.getProperty("user.dir") + "\\src\\main\\resources\\task1\\benchmark_file";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Adder adder;
    private ResultBag result;

    @Benchmark
    public void sequentialAdder() {
        adder = new SequentialAdder();
        result = adder.readAndSum(benchmarkPath);
        log.info(result.toString());
    }

    @Benchmark
    public void memoryMappedAdder() {
        adder = new MemoryMappedAdder();
        result = adder.readAndSum(benchmarkPath);
        log.info(result.toString());
    }

    public static void main(String[] args) {
        Options options = new OptionsBuilder()
                .include(SummationBenchmarks.class.getSimpleName())
                .forks(1)
                .build();
        try {
            new Runner(options).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
}
