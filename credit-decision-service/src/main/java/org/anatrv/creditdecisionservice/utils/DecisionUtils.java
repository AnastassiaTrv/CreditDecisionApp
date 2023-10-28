package org.anatrv.creditdecisionservice.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class DecisionUtils {
    public static int getPeriodInRange(int period, int min, int max) {
        if (period < min) {
            period = min;
        } else if (period > max) {
            period = max;
        }
        return period;
    }

    public static boolean isGreather(BigDecimal amount, BigDecimal test) {
        return amount.compareTo(test) == 1;
    }

    public static boolean isEqual(BigDecimal amount, BigDecimal test) {
        return amount.compareTo(test) == 0;
    }

    public static BigDecimal changeAmountByScore(BigDecimal initialAmount, double score) {
        BigDecimal changed = initialAmount.multiply(BigDecimal.valueOf(score)).setScale(0, RoundingMode.DOWN);
        return round(changed, 2);
    }

    public static BigDecimal round(BigDecimal initial, int precision) {
        BigDecimal rounded = initial.round(new MathContext(precision, RoundingMode.HALF_DOWN));
        return new BigDecimal(rounded.toPlainString()); // to get rid of scientific notation
    }

    public static int changePeriodByScore(int period, double customerScore) {
        double changed = Math.ceil((double) period / customerScore);
        return (int) changed;
    }
    
}
