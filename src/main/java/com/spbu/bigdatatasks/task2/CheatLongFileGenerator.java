package com.spbu.bigdatatasks.task2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * For faster tests
 * Guarantees that generated number will have at least two small prime factors
 */
public class CheatLongFileGenerator implements FileGenerator {

    private static int[] lookUpTable = {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103,
            107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223,
            227, 229, 233, 239, 241, 251, 257, 263, 269, 271
    };

    @Override
    public void generateFile(String path, int numbersAmount) {
        SecureRandom rand = new SecureRandom();
        int smallPart;
        long number;
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (int i = 0; i < numbersAmount; i++) {
                smallPart = lookUpTable[rand.nextInt(lookUpTable.length)] *
                        lookUpTable[rand.nextInt(lookUpTable.length)];
                number = rand.nextLong();
                number -= Long.remainderUnsigned(number, smallPart);

                writer.write(Long.toUnsignedString(number));
                writer.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
