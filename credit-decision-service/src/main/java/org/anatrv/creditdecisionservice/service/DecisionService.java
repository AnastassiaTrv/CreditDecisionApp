package org.anatrv.creditdecisionservice.service;

import java.math.BigDecimal;

import org.anatrv.creditdecisionservice.config.CreditProperties;
import org.anatrv.creditdecisionservice.gateway.CreditRatingGateway;
import org.anatrv.creditdecisionservice.model.CreditDecision;
import org.anatrv.creditdecisionservice.model.CreditRequest;
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

    public CreditDecision getCreditDecision(CreditRequest request) {
        var decision = buildRawDecision(request.getAmount(), request.getPeriod());

        CustomerCreditScore customerScore = creditRatingGateway.getCustomerCreditScore(request);

        if (customerScore == null) {
            // unable to make a credit decision, we don't want to reject the credit and give a customer bad experience with our service,
            // and we also cannot aproove it without any check, so we leave status UNDEFINED
            decision.setMsg("Unable to obtain customer credit score, try later");
            return decision;
        } else if (customerScore.isHasDebt()) {
            rejectDecision(decision, "Customer has debt");
        } else {
            processDecision(decision, request.getAmount(), request.getPeriod(), customerScore.getValue());
        }

        return decision;
    }

    private void processDecision(CreditDecision decision, BigDecimal amount, int period, double score) {
        BigDecimal maxAmount = properties.getAmountMax();
        BigDecimal minAmount = properties.getAmountMin();
        if (score > 1) {
            // we can increase the amount but keep it in range
            amount = changeAmountByScore(amount, score);
            aprooveDecision(decision, getAmountInRange(amount, minAmount, maxAmount), period);
        } else if (score == 1) {
            aprooveDecision(decision, amount, period);
        } else if (score < 1 && score > 0) {
            decreaseDecisionAmount(decision, amount, minAmount, period, score);
        }
    }

    private void decreaseDecisionAmount(CreditDecision decision, BigDecimal amount, BigDecimal minAmount, int period, double score) {
        // if the score is less than 1 but still positive then we can try first to decrease the amount,
        amount = changeAmountByScore(amount, score);
        if (isGreatherOrEqual(amount, minAmount)) {
            aprooveDecision(decision, amount, period);
        } else {
             // if the new amount is smaller than min we then try to increase the period
            period = changePeriodByScore(period, score);
            if (period <= properties.getPeriodMax()) {
                aprooveDecision(decision, minAmount, period);
            } else {
                // we cannot offer a loan that would fit into our amount and period constraints
                rejectDecision(decision, "Customer credit score is too low");
            }
        }
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
