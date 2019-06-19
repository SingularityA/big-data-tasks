package com.spbu.bigdatatasks.task1;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummationTests {

    private final String testFilePath = System.getProperty("user.dir") + "\\src\\main\\resources\\task1\\test_file";
    private final String benchmarkPath = System.getProperty("user.dir") + "\\src\\main\\resources\\task1\\benchmark_file";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void generateFiles() {
        BinaryFileGenerator generator = new RandomIntBinaryFileGenerator();

        long bytesNumber = 4 * 100;
        generator.generateFile(testFilePath, bytesNumber);

        bytesNumber = 2 * 1024 * 1024 * 1024L;
        generator.generateFile(benchmarkPath, bytesNumber);
    }

    @Test
    public void testSequentialAndMemoryMappedAdder() {
        Adder adder = new SequentialAdder();
        ResultBag result1 = adder.readAndSum(testFilePath);

        adder = new MemoryMappedAdder();
        ResultBag result2 = adder.readAndSum(testFilePath);
        Assert.assertEquals(result1, result2);

        log.info("Results of 'Adder' calculations: " + result1.toString());
    }
}
