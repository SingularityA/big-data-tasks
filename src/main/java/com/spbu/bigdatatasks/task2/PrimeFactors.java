package com.spbu.bigdatatasks.task2;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PrimeFactors {

    private final static SecureRandom random = new SecureRandom();

    public static List<Long> factorize(long number) {
        List<Long> factors = new ArrayList<>();

        if (Long.compareUnsigned(number, 2) < 0) {
            throw new IllegalArgumentException();
        }

        while (Long.remainderUnsigned(number, 2) == 0) {
            factors.add(2L);
            number = Long.divideUnsigned(number, 2);
        }

        if (Long.compareUnsigned(number, 1) > 0) {
            long factor = 3L;
            while (Long.compareUnsigned(factor * factor, number) <= 0) {
                if (Long.remainderUnsigned(number, factor) == 0) {
                    factors.add(factor);
                    number = Long.divideUnsigned(number, factor);
                }
                else {
                    factor += 2;
                }
            }
            factors.add(number);
        }
        return factors;
    }

    /* very slow implementation */
    public static List<Long> factorizeRho(long number) {
        List<Long> factors = new ArrayList<>();
        factorizeRho(number, factors);
        return factors;
    }

    private static void factorizeRho(long number, List<Long> factors) {
        if (Long.compareUnsigned(number, 1) == 0) return;

        BigInteger bNumber = new BigInteger(Long.toUnsignedString(number));
        if (bNumber.isProbablePrime(20)) {
            factors.add(number);
            return;
        }

        long divisor = rho(number);
        factorizeRho(divisor, factors);
        factorizeRho(Long.divideUnsigned(number, divisor), factors);
    }

    private static long rho(long number) {
        long divisor;
        long c  = random.nextLong();
        long x  = random.nextLong();
        long xx = x;

        if (Long.compareUnsigned(Long.remainderUnsigned(number, 2), 0) == 0)
            return 2L;

        do {
            x = Long.remainderUnsigned(Long.remainderUnsigned(x * x, number) + c, number);
            xx = Long.remainderUnsigned(Long.remainderUnsigned(xx * xx, number) + c, number);
            xx = Long.remainderUnsigned(Long.remainderUnsigned(xx * xx, number) + c, number);
            divisor = gcd(x - xx, number);
        } while(Long.compareUnsigned(divisor, 1) == 0);

        return divisor;
    }

    private static long gcd(long a, long b) {
        BigInteger b1 = new BigInteger(Long.toUnsignedString(a));
        BigInteger b2 = new BigInteger(Long.toUnsignedString(b));
        return Long.parseUnsignedLong(b1.gcd(b2).toString());
    }
}
