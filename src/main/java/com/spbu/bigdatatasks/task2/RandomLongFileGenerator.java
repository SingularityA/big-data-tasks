package com.spbu.bigdatatasks.task2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomLongFileGenerator implements FileGenerator {
    @Override
    public void generateFile(String path, int numbersAmount) {
        Random rand = new Random();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (int i = 0; i < numbersAmount; i++) {
               writer.write(Long.toUnsignedString(rand.nextLong()));
               writer.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
