package org.anatrv.creditdecisionservice.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.anatrv.creditdecisionservice.config.CreditProperties;
import org.anatrv.creditdecisionservice.gateway.CreditRatingGateway;
import org.anatrv.creditdecisionservice.model.CreditDecision;
import org.anatrv.creditdecisionservice.model.CustomerCreditScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.APROOVED;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.REJECTED;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.UNDEFINED;

@Service
public class DecisionService {

    @Autowired
    private CreditRatingGateway creditRatingGateway;

    @Autowired
    private CreditProperties properties;

    public CreditDecision getCreditDecision(String customerId, BigDecimal requestedAmount, Integer period) {
        BigDecimal maxAmount = properties.getAmountMax();
        BigDecimal minAmount = properties.getAmountMin();
        int maxPeriod = properties.getPeriodMax();
        int minPeriod = properties.getPeriodMin();

        var decision = new CreditDecision();
        decision.setAmountRequested(requestedAmount);
        decision.setPeriodRequested(period);
        decision.setStatus(UNDEFINED);

        // I think we can try to get the credit score for the max amount and then just decrease it if the score is less than 1
        BigDecimal amountOffered = maxAmount;

        // don't reject the decision if the initial period doesn't fit into our constraints, just adjust the period and proceed 
        int periodOffered = getPeriodInRange(period, minPeriod, maxPeriod);

        CustomerCreditScore customerScore = creditRatingGateway.getCustomerCreditScore(customerId, amountOffered, periodOffered);
        if (customerScore == null) {
            // if for some reason external service experiences a trouble and cannot provide us with credit score
            // then we are unable to make a credit decision, we don't want to reject the credit and give a customer bad experience with our service,
            // and we also cannot aproove it without any check, so we leave status UNDEFINED
            decision.setMsg("Unable to obtain customer credit score, try later");
            return decision;
        } else {
            double score = customerScore.getValue();
            if (score >= 1) {
                aprooveDecision(decision, amountOffered, periodOffered, "Aprooved with greather amount");
            } else if (score < 1 && score > 0) {
                // if the score is less than 1 but still positive then we can try first to decrease the amount,
                // and if the new amount is smaller than min we then try to increase the period
                amountOffered = changeAmountByScore(amountOffered, score);
                if (isGreather(amountOffered, minAmount)) {
                    aprooveDecision(decision, amountOffered, periodOffered, "");

                } else if (isEqual(amountOffered, minAmount)) {
                    // if offered amount equals to min, then we try to increase a period but keep it within or min-max range
                    int newPeriod = changePeriodByScore(periodOffered, score);
                    newPeriod = getPeriodInRange(newPeriod, minPeriod, maxPeriod);
                    aprooveDecision(decision, amountOffered, newPeriod, "Aprooved with increased period");

                } else {
                    //  we can offer only min amount and not less, but then we have to increase the period
                    int newPeriod = changePeriodByScore(periodOffered, score);
                    if (newPeriod <= maxPeriod) {
                        aprooveDecision(decision, minAmount, newPeriod, "");
                    } else {
                        // we cannot offer a loan that would fit into our amount and period constraints
                        rejectDecision(decision, "Customer credit score is too low");
                    }
                }
            } else {
                // if the score is negative, then we cannot offer any amount and give the user a negative decision
                rejectDecision(decision, "Customer has debt");
            }
            
        }

        return decision;
    }

    private int getPeriodInRange(int period, int min, int max) {
        if (period < min) {
            period = min;
        } else if (period > max) {
            period = max;
        }
        return period;
    }

    private boolean isGreather(BigDecimal amount, BigDecimal test) {
        int comparison = amount.compareTo(test);
        return comparison == 1;
    }

    private boolean isEqual(BigDecimal amount, BigDecimal test) {
        int comparison = amount.compareTo(test);
        return comparison == 0;
    }

    private void aprooveDecision(CreditDecision decision, BigDecimal aproovedAmount, Integer aproovedPeriod, String message) {
        decision.setStatus(APROOVED);
        decision.setAmountAprooved(aproovedAmount);
        decision.setPeriodAprooved(aproovedPeriod);
        decision.setMsg(message);
    }

    private void rejectDecision(CreditDecision decision, String message) {
        decision.setStatus(REJECTED);
        decision.setMsg(message);
    }

    private BigDecimal changeAmountByScore(BigDecimal initialAmount, double score) {
        BigDecimal changed = initialAmount.multiply(BigDecimal.valueOf(score)).setScale(0, RoundingMode.DOWN);
        return round(changed, 2);
    }

    private BigDecimal round(BigDecimal initial, int precision) {
        BigDecimal rounded = initial.round(new MathContext(precision, RoundingMode.HALF_DOWN));
        return new BigDecimal(rounded.toPlainString()); // to get rid of scientific notation
    }

    private int changePeriodByScore(int period, double customerScore) {
        double changed = Math.ceil((double) period / customerScore);
        return (int) changed;
    }
    
}
