package com.spbu.bigdatatasks.task1;

import java.math.BigInteger;

public class ResultBag {
    private BigInteger sum;
    private int min;
    private int max;

    public ResultBag(BigInteger sum, int min, int max) {
        this.sum = sum;
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        return "Sum = " + sum
                + " Min = " + Integer.toUnsignedString(min)
                + " Max = " + Integer.toUnsignedString(max);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultBag resultBag = (ResultBag) o;

        if (min != resultBag.min) return false;
        if (max != resultBag.max) return false;
        return sum != null ? sum.equals(resultBag.sum) : resultBag.sum == null;
    }

    @Override
    public int hashCode() {
        int result = sum != null ? sum.hashCode() : 0;
        result = 31 * result + min;
        result = 31 * result + max;
        return result;
    }
}
