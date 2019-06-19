package com.spbu.bigdatatasks.task1;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Random;

public class RandomIntBinaryFileGenerator implements BinaryFileGenerator {

    @Override
    public void generateFile(String path, long bytesNumber) {
        final int numberLength = 4;
        final long numbersAmount = bytesNumber / numberLength;
        Random rand = new Random();
        ByteBuffer buffer = ByteBuffer.allocate(numberLength);

        try(OutputStream stream = new BufferedOutputStream(new FileOutputStream(path))) {
            for (long i = 0; i < numbersAmount; i++) {
                buffer.putInt(rand.nextInt());
                stream.write(buffer.array());
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
