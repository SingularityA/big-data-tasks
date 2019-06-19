package com.spbu.bigdatatasks.task2;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class FactorizationTests {

    private final String testFilePath = System.getProperty("user.dir") + "\\src\\main\\resources\\task2\\test_file.txt";
    private final String benchmarkPath = System.getProperty("user.dir") + "\\src\\main\\resources\\task2\\benchmark_file.txt";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /* 'truly' random, sometimes huge primes */
    @Test
    public void generateRandomLongFiles() {
        FileGenerator generator = new RandomLongFileGenerator();

        int numbersAmount = 10;
        generator.generateFile(testFilePath, numbersAmount);

        numbersAmount = 100;
        generator.generateFile(benchmarkPath, numbersAmount);
    }

    /* for faster testing and benchmarking */
    @Test
    public void generateCheatLongFiles() {
        FileGenerator generator = new CheatLongFileGenerator();

        int numbersAmount = 100;
        generator.generateFile(testFilePath, numbersAmount);

        numbersAmount = 1000;
        generator.generateFile(benchmarkPath, numbersAmount);
    }

    @Test
    public void testCounters() {
        long[] results = new long[4];

        FactorsCounter counter = new SequentialFactorsCounter();
        results[0] = counter.readAndCount(testFilePath);

        counter = new ForkJoinFactorsCounter();
        results[1] = counter.readAndCount(testFilePath);

        counter = new ForkJoinFactorsCounter();
        results[2] = counter.readAndCount(testFilePath);

        counter = new AkkaActorsFactorsCounter();
        results[3] = counter.readAndCount(testFilePath);

        for (int i = 1; i < 4; i++)
            Assert.assertEquals(results[0], results[i]);

        log.info("Sum of factors = " + results[0]);
    }

    /* slow on big primes but okay */
    @Test
    public void testFactorize() {
        long number = 4207203498037339203L;
        List<Long> expected = Lists.newArrayList(3L, 389L, 282287L, 12771202307L);
        List<Long> factors = PrimeFactors.factorize(number);

        Assert.assertEquals(expected, factors);
    }

    /* slow */
    @Test
    public void testFactorizeRho() {
        long number = 4207203498037339203L;
        List<Long> expected = Lists.newArrayList(3L, 389L, 282287L, 12771202307L);
        List<Long> factors = PrimeFactors.factorizeRho(number);

        Assert.assertEquals(expected, factors);
    }
}
