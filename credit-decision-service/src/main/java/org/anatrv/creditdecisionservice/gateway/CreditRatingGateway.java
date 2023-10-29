package org.anatrv.creditdecisionservice.gateway;

import java.util.HashMap;
import java.util.Map;

import org.anatrv.creditdecisionservice.model.CreditRequest;
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


    public CustomerCreditScore getCustomerCreditScore(CreditRequest request) {
        String customerId = request.getCustomerId();
        Integer modifier = customerModifierMap.get(customerId);
        CustomerCreditScore result = null;

        if (modifier != null) {
            if (modifier > 0) {
                // the exactness is not the point here, we just need to get some score and round it up
                double score = (modifier / request.getAmount().doubleValue()) * request.getPeriod();
                score = (double) Math.round(score * 100) / 100;
                result = new CustomerCreditScore(customerId, score, false);
            } else {
                result = new CustomerCreditScore(customerId, 0, true);
            }
        }

        return result;
    }
    
}
