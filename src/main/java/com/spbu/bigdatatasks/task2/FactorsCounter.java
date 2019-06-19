package com.spbu.bigdatatasks.task2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class FactorsCounter {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    abstract long readAndCount(String path);

    protected List<Long> readAllNumbers(String path) {
        List<Long> numbers = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                numbers.add(Long.parseUnsignedLong(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numbers;
    }
}
