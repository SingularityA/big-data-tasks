package com.spbu.bigdatatasks.task1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MemoryMappedAdder implements Adder {

    private final int threadsNumber;

    MemoryMappedAdder(int threadsNumber) {
        if (threadsNumber < 1)
            throw new IllegalArgumentException();
        this.threadsNumber = threadsNumber;
    }
    MemoryMappedAdder() {
        this(Runtime.getRuntime().availableProcessors());
    }

    private class Region implements Runnable {

        final long position;
        final long size;
        final FileChannel channel;
        final Thread thread;

        BigInteger sum = BigInteger.valueOf(0);
        int min = UNSIGNED_MAX;
        int max = UNSIGNED_MIN;
        int number;

        Region(long position, long size, FileChannel channel) {
            this.position = position;
            this.size = size;
            this.channel = channel;

            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            try {
                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, position, size);
                while (buffer.hasRemaining()) {
                    number = buffer.getInt();
                    sum = sum.add(BigInteger.valueOf(Integer.toUnsignedLong(number)));
                    if (Integer.compareUnsigned(number, min) < 0)
                        min = number;
                    if (Integer.compareUnsigned(number, max) > 0)
                        max = number;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultBag readAndSum(String path) {
        BigInteger sum = BigInteger.valueOf(0);
        int min = UNSIGNED_MAX;
        int max = UNSIGNED_MIN;

        try(FileChannel channel = new RandomAccessFile(path, "r").getChannel()) {

            long channelSize = channel.size();
            long regionSize = regionSize(channelSize);

            List<Region> regions = new ArrayList<>(threadsNumber);
            for (int i = 0; i < threadsNumber - 1; i++)
                regions.add(new Region(i * regionSize, regionSize, channel));

            regions.add(new Region((threadsNumber - 1) * regionSize,
                    (int)(channelSize - (threadsNumber - 1) * regionSize),
                    channel
            ));

            for (Region region: regions)
                region.thread.join();

            for (Region region: regions) {
                sum = sum.add(region.sum);
                if (Integer.compareUnsigned(region.min, min) < 0)
                    min = region.min;
                if (Integer.compareUnsigned(region.max, max) > 0)
                    max = region.max;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ResultBag(sum, min, max);
    }

    private long regionSize(long fileSize) {
        long part = (int)(fileSize / threadsNumber);
        return part - part % 4;
    }
}
