package org.anatrv.creditdecisionservice.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.anatrv.creditdecisionservice.config.CreditProperties;
import org.anatrv.creditdecisionservice.exception.ValidationException;
import org.anatrv.creditdecisionservice.model.CreditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.anatrv.creditdecisionservice.utils.DecisionUtils.*;
import static java.lang.String.format;

@Service
public class CreditRequestValidationService {
    
    @Autowired
    private CreditProperties properties;

    public void validate(CreditRequest request) {
        Map<String, String> errors = new HashMap<>();

        BigDecimal maxAmount = properties.getAmountMax();
        BigDecimal minAmount = properties.getAmountMin();

        if (isLess(request.getAmount(), minAmount)) {
            errors.put("invalid.amount.min", format("Amount cannot be less than '%s'", minAmount));
            
        } else if (isGreather(request.getAmount(), maxAmount)) {
            errors.put("invalid.amount.max", format("Amount cannot be greather than '%s'", maxAmount));
        }

        int maxPeriod = properties.getPeriodMax();
        int minPeriod = properties.getPeriodMin();

        if (request.getPeriod() < minPeriod) {
            errors.put("invalid.period.min", format("Period cannot be less than '%s'", minPeriod));
            
        } else if (request.getPeriod() > maxPeriod) {
            errors.put("invalid.period.max", format("Period cannot be greather than '%s'", maxPeriod));
        }

        if (!errors.isEmpty()) {
            String msg = errors.values().stream().collect(Collectors.joining(", "));
            throw new ValidationException(msg);
        }
    }
}
