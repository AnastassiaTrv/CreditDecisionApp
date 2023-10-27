package org.anatrv.creditdecisionservice.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreditDecision {
    private CreditDecisionStatus status;
    private BigDecimal amount;
    private Integer period;

    public CreditDecision(CreditDecisionStatus status, BigDecimal amount, Integer period) {
        this.status = status;
        this.amount = amount;
        this.period = period;
    }
}
