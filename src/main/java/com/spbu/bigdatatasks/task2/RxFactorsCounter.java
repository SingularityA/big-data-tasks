package com.spbu.bigdatatasks.task2;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class RxFactorsCounter extends FactorsCounter {
    @Override
    public long readAndCount(String path) {
        List<Long> numbers = readAllNumbers(path);
        Flowable<Long> sequence = Flowable.fromIterable(numbers);

        return sequence
                .onBackpressureBuffer()
                .parallel()
                .runOn(Schedulers.computation())
                .map(number -> {
                    List<Long> factors = PrimeFactors.factorize(number);
                    log.info("Number: " + Long.toUnsignedString(number) + " factors: " + factors);
                    return factors.size();
                })
                .sequential()
                .reduceWith(() -> 0, (acc, item) -> acc + item)
                .blockingGet();
    }
}
