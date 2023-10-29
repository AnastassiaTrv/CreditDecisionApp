package org.anatrv.creditdecisionservice.service;

import java.math.BigDecimal;

import org.anatrv.creditdecisionservice.config.CreditProperties;
import org.anatrv.creditdecisionservice.gateway.CreditRatingGateway;
import org.anatrv.creditdecisionservice.model.CreditDecision;
import org.anatrv.creditdecisionservice.model.CustomerCreditScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.APROOVED;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.REJECTED;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.UNDEFINED;
import static org.anatrv.creditdecisionservice.utils.DecisionUtils.*;

@Service
public class DecisionService {

    @Autowired
    private CreditRatingGateway creditRatingGateway;

    @Autowired
    private CreditProperties properties;

    public CreditDecision getCreditDecision(String customerId, BigDecimal amountRequested, int periodRequested) {
        BigDecimal maxAmount = properties.getAmountMax();
        BigDecimal minAmount = properties.getAmountMin();
        int maxPeriod = properties.getPeriodMax();
        int minPeriod = properties.getPeriodMin();

        var decision = buildRawDecision(amountRequested, periodRequested);

        // don't reject the decision if the requested period and amount don't fit into our constraints, just adjust them and proceed 
        int periodOffered = getPeriodInRange(periodRequested, minPeriod, maxPeriod);
        BigDecimal amountOffered = getAmountInRange(amountRequested, minAmount, maxAmount);

        CustomerCreditScore customerScore = creditRatingGateway.getCustomerCreditScore(customerId, amountOffered, periodOffered);
        if (customerScore == null) {
            // if for some reason external service experiences a trouble and cannot provide us with credit score
            // then we are unable to make a credit decision, we don't want to reject the credit and give a customer bad experience with our service,
            // and we also cannot aproove it without any check, so we leave status UNDEFINED
            decision.setMsg("Unable to obtain customer credit score, try later");
            return decision;
        } else {
            double score = customerScore.getValue();
            if (score > 1) {
                // we can increase the amount but keep it in range
                amountOffered = changeAmountByScore(amountOffered, score);
                aprooveDecision(decision, getAmountInRange(amountOffered, minAmount, maxAmount), periodOffered);
            } else if (score == 1) {
                aprooveDecision(decision, amountOffered, periodOffered);
            } else if (score < 1 && score > 0) {
                // if the score is less than 1 but still positive then we can try first to decrease the amount,
                // and if the new amount is smaller than min we then try to increase the period
                amountOffered = changeAmountByScore(amountOffered, score);
                if (isGreatherOrEqual(amountOffered, minAmount)) {
                    aprooveDecision(decision, amountOffered, periodOffered);
                } else {
                    // we can offer only min amount and not less, but then we have to increase the period
                    periodOffered = changePeriodByScore(periodOffered, score);
                    if (periodOffered <= maxPeriod) {
                        aprooveDecision(decision, minAmount, periodOffered);
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

    private CreditDecision buildRawDecision(BigDecimal amountRequested, int periodRequested) {
        var decision = new CreditDecision();
        decision.setAmountRequested(amountRequested);
        decision.setPeriodRequested(periodRequested);
        decision.setAmountAprooved(BigDecimal.ZERO);
        decision.setPeriodAprooved(0);
        decision.setStatus(UNDEFINED);
        return decision;
    }

    private void aprooveDecision(CreditDecision decision, BigDecimal aproovedAmount, Integer aproovedPeriod) {
        decision.setStatus(APROOVED);
        decision.setAmountAprooved(aproovedAmount);
        decision.setPeriodAprooved(aproovedPeriod);
    }

    private void rejectDecision(CreditDecision decision, String message) {
        decision.setStatus(REJECTED);
        decision.setMsg(message);
    }
    
}
