package org.anatrv.creditdecisionservice.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDecision {
    private CreditDecisionStatus status;

    // what the customer wants
    private BigDecimal amountRequested;
    private int periodRequested;

    // what we can offer
    private BigDecimal amountAprooved;
    private int periodAprooved;

    // I think it's good to provide the customer with some explanation about the decision
    private String msg;
}
