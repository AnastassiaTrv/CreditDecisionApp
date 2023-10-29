package org.anatrv.creditdecisionservice.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditRequest {
    private String customerId;
    private BigDecimal amount;
    private int period;
}
