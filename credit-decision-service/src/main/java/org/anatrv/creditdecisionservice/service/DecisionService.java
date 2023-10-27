package org.anatrv.creditdecisionservice.service;

import java.math.BigDecimal;

import org.anatrv.creditdecisionservice.gateway.CreditRatingGateway;
import org.anatrv.creditdecisionservice.model.CreditDecision;
import org.anatrv.creditdecisionservice.model.CustomerCreditScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.NEGATIVE;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.POSITIVE;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.UNDEFINED;

@Service
public class DecisionService {

    @Autowired
    private CreditRatingGateway creditRatingGateway;

    public CreditDecision getCreditDecision(String customerId, BigDecimal amount, Integer period) {
        BigDecimal maxAmount = BigDecimal.valueOf(10000);
        BigDecimal minAmount = BigDecimal.valueOf(2000);
        int maxPeriod = 60;
        int minPeriod = 12;

        var decision = new CreditDecision(UNDEFINED, amount, period);

        CustomerCreditScore customerRating = creditRatingGateway.getCustomerCreditScore(customerId, amount, period);
        if (customerRating != null) {
            double score = customerRating.getValue();
            if (score > 1) {
                // if the score is good, then we can offer a customer bigger amount
                BigDecimal newAmount = increaseAmountByScore(amount, minAmount, maxAmount, score);
                decision.setAmount(newAmount);
                decision.setStatus(POSITIVE);
            } else if (score == 1) {
                // if the score happens to equal 1, then we can only offer a requested amount
                decision.setStatus(POSITIVE);
            } else if (score > 0 && score < 1) {
                // this is the most difficult case,
                // if the score is less than 1 but still positive then we can try first to decrease the amount,
                // and if the new amount is smaller than min we then try to increase the period
                BigDecimal newAmount = changeAmountByScore(amount, score);
                if (isGreatherOrEqual(newAmount, minAmount)) {
                    decision.setAmount(newAmount);
                    decision.setStatus(POSITIVE);
                } else {
                    newAmount = minAmount;
                    int newPeriod = increasePeriodByScore(period, score);
                    if (newPeriod <= maxPeriod) {
                        decision.setAmount(newAmount);
                        decision.setPeriod(newPeriod);
                        decision.setStatus(POSITIVE);
                    } else {
                        // we cannot offer a loan that would fit into our amount and period bounds
                        decision.setStatus(NEGATIVE);
                    }
                }
            } else {
                // if the score is negative, then we cannot offer any amount and give the user a negative decision
                decision.setStatus(NEGATIVE);
            }
            
        } else {
            // if for some reason we've got a null or maybe some external service experiences a trouble and cannot provide us with credit score
            // then we are unable to make a credit decision, we don't want to set status to NEGATIVE and give a customer bad experience with our service,
            // and we also cannot set it to POSITIVE without any check, so we leave status UNDEFINED and let the customer try it later
        }

        return decision;
    }

    private boolean isGreatherOrEqual(BigDecimal amount, BigDecimal test) {
        int comparison = amount.compareTo(test);
        return comparison >= 0;
    }

    private BigDecimal increaseAmountByScore(BigDecimal initial, BigDecimal min, BigDecimal max, double score) {
        BigDecimal increased = changeAmountByScore(initial, score);
        return getWithinBounds(increased, min, max);
    }

    private BigDecimal getWithinBounds(BigDecimal amount, BigDecimal min, BigDecimal max) {
        int comparingToMin = amount.compareTo(min);
        if (comparingToMin == -1) {
            return min;
        }

        int comparingToMax = amount.compareTo(max);
        if (comparingToMax == 1) {
            return max;
        }

        return amount;

    }

    private BigDecimal changeAmountByScore(BigDecimal initialAmount, double score) {
        return initialAmount.multiply(BigDecimal.valueOf(score));
    }

    private int increasePeriodByScore(int period, double customerScore) {
        double changed = Math.ceil((double) period / customerScore);
        return (int) changed;
    }
    
}
