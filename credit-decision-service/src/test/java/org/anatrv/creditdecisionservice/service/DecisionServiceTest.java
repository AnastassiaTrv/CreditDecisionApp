package org.anatrv.creditdecisionservice.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.anatrv.creditdecisionservice.config.CreditProperties;
import org.anatrv.creditdecisionservice.gateway.CreditRatingGateway;
import org.anatrv.creditdecisionservice.model.CreditDecision;
import org.anatrv.creditdecisionservice.model.CustomerCreditScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.APROOVED;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.UNDEFINED;
import static org.anatrv.creditdecisionservice.model.CreditDecisionStatus.REJECTED;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DecisionServiceTest {

    @Mock
    CustomerCreditScore creditScore;

    @Mock
    CreditRatingGateway creditRatingGateway;

    @Mock
    CreditProperties properties;

    @InjectMocks
    DecisionService decisionService;

    private static BigDecimal amountMax = BigDecimal.valueOf(10000);
    private static BigDecimal amountMin = BigDecimal.valueOf(2000);
    private static Integer periodMax = 60;
    private static Integer periodMin = 12;

    @BeforeEach
    public void configMockData() {
        when(properties.getAmountMax()).thenReturn(amountMax);
        when(properties.getAmountMin()).thenReturn(amountMin);
        when(properties.getPeriodMax()).thenReturn(periodMax);
        when(properties.getPeriodMin()).thenReturn(periodMin);
    }

    @Test
    public void getCreditDecision_shouldAprooveMaxAmount_ifScoreIsMoreThat1() {
        String customerId = "id";
        BigDecimal requestedAmount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double score = 2.5;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amountMax, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(score);

        CreditDecision decision = decisionService.getCreditDecision(customerId, requestedAmount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amountMax, period);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(amountMax);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveMaxAmount_ifScoreIs1() {
        String customerId = "id";
        BigDecimal requestedAmount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double score = 1;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amountMax, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(score);

        CreditDecision decision = decisionService.getCreditDecision(customerId, requestedAmount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amountMax, period);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(amountMax);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveIncreasedAmount_ifScoreIsSmallerThatOne() {
        String customerId = "id";
        BigDecimal requestedAmount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double score = 0.75;
        BigDecimal increased = BigDecimal.valueOf(7500);

        when(creditRatingGateway.getCustomerCreditScore(customerId, amountMax, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(score);

        CreditDecision decision = decisionService.getCreditDecision(customerId, requestedAmount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amountMax, period);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(increased);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveRoundedAmount_ifOfferedAmountIsNotFull() { // for exapmle 6988 should become 7000
        String customerId = "id";
        BigDecimal requestedAmount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double score = 0.69874657464;
        BigDecimal increased = BigDecimal.valueOf(7000);

        when(creditRatingGateway.getCustomerCreditScore(customerId, amountMax, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(score);

        CreditDecision decision = decisionService.getCreditDecision(customerId, requestedAmount, period);

        verify(creditRatingGateway).getCustomerCreditScore(customerId, amountMax, period);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(increased);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveDecreasedAmountAndIncreasePeriod_ifScoreIsSmallerThatOne() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(5000);
        Integer period = 12;

        double score = 0.199;
        BigDecimal desreasedAmount = BigDecimal.valueOf(2000);
        Integer increasedPeriod = 60;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amountMax, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(score);

        CreditDecision decision = decisionService.getCreditDecision(customerId, amount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amountMax, period);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(desreasedAmount);
        assertThat(decision.getPeriodAprooved()).isEqualTo(increasedPeriod);
    }

    @Test
    public void getCreditDecision_shouldReject_ifScoreIsBadButNoDebt() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double badScore = 0.1;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amountMax, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(badScore);

        CreditDecision decision = decisionService.getCreditDecision(customerId, amount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amountMax, period);
        assertThat(decision.getStatus()).isEqualTo(REJECTED);
        assertThat(decision.getAmountAprooved()).isNull();
        assertThat(decision.getPeriodAprooved()).isEqualTo(0);
    }

    @Test
    public void getCreditDecision_shouldReject_ifHasDebt() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(2500);
        Integer period = 15;

        double debtScore = -0.5;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amountMax, period)).thenReturn(creditScore);
        when(creditScore.getValue()).thenReturn(debtScore);

        CreditDecision decision = decisionService.getCreditDecision(customerId, amount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amountMax, period);
        assertThat(decision.getStatus()).isEqualTo(REJECTED);
        assertThat(decision.getAmountAprooved()).isNull();
        assertThat(decision.getPeriodAprooved()).isEqualTo(0);
    }

    @Test
    public void getCreditDecision_shouldReturnStatusUndefined_ifNoCreditScoreAvailable() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(2500);
        Integer period = 15;

        when(creditRatingGateway.getCustomerCreditScore(customerId, amountMax, period)).thenReturn(null);

        CreditDecision decision = decisionService.getCreditDecision(customerId, amount, period);
        
        verify(creditRatingGateway).getCustomerCreditScore(customerId, amountMax, period);
        assertThat(decision.getStatus()).isEqualTo(UNDEFINED);
        assertThat(decision.getAmountAprooved()).isNull();
        assertThat(decision.getPeriodAprooved()).isEqualTo(0);
    }
    
}

