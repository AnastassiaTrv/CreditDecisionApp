package org.anatrv.creditdecisionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerCreditScore {
    private String customerId;
    private double value;
}
