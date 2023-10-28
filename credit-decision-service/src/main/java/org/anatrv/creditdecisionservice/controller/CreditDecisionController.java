package org.anatrv.creditdecisionservice.controller;

import java.math.BigDecimal;

import org.anatrv.creditdecisionservice.model.CreditDecision;
import org.anatrv.creditdecisionservice.service.DecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@Data
@CrossOrigin
@RestController
@RequestMapping(path = "api/credit-decision", produces = "application/json")
public class CreditDecisionController {

    @Autowired
    private DecisionService decisionService;

    @GetMapping
    public CreditDecision getCreditDecision(@RequestParam String customerId, 
                                            @RequestParam BigDecimal amount, 
                                            @RequestParam int period) {
        return decisionService.getCreditDecision(customerId, amount, period);
    }
    
}
