package org.anatrv.creditdecisionservice.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.anatrv.creditdecisionservice.gateway.CreditRatingGateway;
import org.anatrv.creditdecisionservice.model.CreditDecision;
import org.anatrv.creditdecisionservice.model.CustomerCreditScore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.NEGATIVE;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.UNDEFINED;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.POSITIVE;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DecisionServiceTest {

    @Mock
    CustomerCreditScore creditScore;

    @Mock
    CreditRatingGateway creditRatingGateway;

    @InjectMocks
    DecisionService decisionService;

    @Test
    public void getCreditDecision_shouldReturnStatusUndefined_ifNoCreditScoreAvailable() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(2500);
        int period = 15;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amount, period)).thenReturn(null);

        CreditDecision decision = decisionService.getCreditDecision(customerId, amount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amount, period);
        assertThat(decision.getStatus()).isEqualTo(UNDEFINED);
        assertThat(decision.getAmount()).isEqualTo(amount);
        assertThat(decision.getPeriod()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldReturnStatusNegative_ifHasDebt() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(2500);
        int period = 15;

        double debtScore = -0.5;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amount, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(debtScore);

        CreditDecision decision = decisionService.getCreditDecision(customerId, amount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amount, period);
        assertThat(decision.getStatus()).isEqualTo(NEGATIVE);
        assertThat(decision.getAmount()).isEqualTo(amount);
        assertThat(decision.getPeriod()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldReturnStatusNegative_ifScoreIsBadButNoDebt() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(5000);
        int period = 15;

        double badScore = 0.1;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amount, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(badScore);

        CreditDecision decision = decisionService.getCreditDecision(customerId, amount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amount, period);
        assertThat(decision.getStatus()).isEqualTo(NEGATIVE);
        assertThat(decision.getAmount()).isEqualTo(amount);
        assertThat(decision.getPeriod()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldReturnWithPositiveAndDecreaseAmount_ifScoreIsSmallerThatOne() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(5000);
        int period = 15;

        double score = 0.5;
        BigDecimal decreased = BigDecimal.valueOf(2500.0);

        when(creditRatingGateway.getCustomerCreditScore(customerId, amount, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(score);

        CreditDecision decision = decisionService.getCreditDecision(customerId, amount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amount, period);
        assertThat(decision.getStatus()).isEqualTo(POSITIVE);
        assertThat(decision.getAmount()).isEqualTo(decreased);
        assertThat(decision.getPeriod()).isEqualTo(period);
    }
    
}

