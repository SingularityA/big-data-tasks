package com.spbu.bigdatatasks.task3;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class ElectionStatsBenchmarks {

    private final String path = System.getProperty("user.dir")+ "\\src\\main\\resources\\task3\\table.cvs";
    private final ElectionStatsCounter counter = new SparkElectionStatsCounter(path);

    @Benchmark
    public void task1() {
        counter.countVoterTurnout();
    }

    @Benchmark
    public void task2() {
        counter.countFavouriteCandidate();
    }

    @Benchmark
    public void task3() {
        counter.countMaxDifference();
    }

    @Benchmark
    public void task4() {
        counter.countVariance();
    }

    @Benchmark
    public void task5() {
        counter.countSummaryTables();
    }

    public static void main(String[] args) {
        Options options = new OptionsBuilder()
                .include(ElectionStatsBenchmarks.class.getSimpleName())
                .forks(1)
                .build();
        try {
            new Runner(options).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
}
