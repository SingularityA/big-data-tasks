package com.spbu.bigdatatasks.task2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class SequentialFactorsCounter extends FactorsCounter {

    @Override
    public long readAndCount(String path) {
        long totalFactorsAmount = 0L;
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            long number;
            List<Long> factors;
            while ((line = reader.readLine()) != null) {
                number = Long.parseUnsignedLong(line);
                factors = PrimeFactors.factorize(number);
                totalFactorsAmount += factors.size();

                log.info("Number: " + Long.toUnsignedString(number) + " factors: " + factors);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalFactorsAmount;
    }
}
