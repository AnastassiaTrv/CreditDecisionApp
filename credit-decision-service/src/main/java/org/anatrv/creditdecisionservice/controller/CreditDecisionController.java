package org.anatrv.creditdecisionservice.controller;

import java.math.BigDecimal;

import org.anatrv.creditdecisionservice.model.CreditDecision;
import org.anatrv.creditdecisionservice.model.CreditRequest;
import org.anatrv.creditdecisionservice.service.CreditRequestValidationService;
import org.anatrv.creditdecisionservice.service.DecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@CrossOrigin
@RestController
@RequestMapping(path = "api/credit-decision", produces = "application/json")
public class CreditDecisionController {

    @Autowired
    private DecisionService decisionService;

    @Autowired
    private CreditRequestValidationService validationService;

    @GetMapping
    public CreditDecision getCreditDecision(@RequestParam @NotBlank String customerId, 
                                            @RequestParam @NotNull BigDecimal amount, 
                                            @RequestParam @NotNull Integer period) {
        var creditRequest = new CreditRequest(customerId, amount, period);
        validationService.validate(creditRequest);
        return decisionService.getCreditDecision(creditRequest);
    }
    
}
