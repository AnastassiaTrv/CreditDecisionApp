package org.anatrv.creditdecisionservice.gateway;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.anatrv.creditdecisionservice.model.CustomerCreditScore;
import org.springframework.stereotype.Component;

/**
 * A gateway class that would normally communicate with some external service to get customer credit score
 */
@Component
public class CreditRatingGateway {

    public static final Map<String, Integer> customerModifierMap;
    static {
        customerModifierMap = new HashMap<>();
        customerModifierMap.put("49002010965", -100); // negative value means debt according to our business rules
        customerModifierMap.put("49002010976", 100);
        customerModifierMap.put("49002010987", 300);
        customerModifierMap.put("49002010998", 1000);   
    }


    public CustomerCreditScore getCustomerCreditScore(String customerId, BigDecimal amount, Integer period) {
        Integer modifier = customerModifierMap.get(customerId);
        CustomerCreditScore result = null;

        if (modifier != null) {
            // the exactness is not the point here, we just need to get some score and round it up
            double score = (modifier / amount.doubleValue()) * period;
            score = (double) Math.round(score * 100) / 100;
            result = new CustomerCreditScore(customerId, score);
        }

        return result;
    }
    
}