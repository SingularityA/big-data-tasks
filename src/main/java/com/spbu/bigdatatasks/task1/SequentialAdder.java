package com.spbu.bigdatatasks.task1;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class SequentialAdder implements Adder {

    @Override
    public ResultBag readAndSum(String path) {
        final int numberLength = 4;
        BigInteger sum = BigInteger.valueOf(0);
        int min = UNSIGNED_MAX;
        int max = UNSIGNED_MIN;
        int number;

        ByteBuffer buffer = ByteBuffer.allocate(numberLength);

        try(InputStream stream = new BufferedInputStream(new FileInputStream(path))) {
            while (stream.read(buffer.array()) != -1) {
                number = buffer.getInt();
                sum = sum.add(BigInteger.valueOf(Integer.toUnsignedLong(number)));
                if (Integer.compareUnsigned(number, min) < 0)
                    min = number;
                if (Integer.compareUnsigned(number, max) > 0)
                    max = number;
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResultBag(sum, min, max);
    }
}
