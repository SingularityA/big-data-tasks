package com.spbu.bigdatatasks.task1;

public interface Adder {
    int UNSIGNED_MIN = 0x00000000;
    int UNSIGNED_MAX = 0xffffffff;

    ResultBag readAndSum(String path);
}
