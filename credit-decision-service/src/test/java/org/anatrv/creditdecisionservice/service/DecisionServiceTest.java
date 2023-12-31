package org.anatrv.creditdecisionservice.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.anatrv.creditdecisionservice.config.CreditProperties;
import org.anatrv.creditdecisionservice.gateway.CreditRatingGateway;
import org.anatrv.creditdecisionservice.model.CreditRequest;
import org.anatrv.creditdecisionservice.model.CustomerCreditScore;
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

    @Test
    public void getCreditDecision_shouldAprooveMaxAmount_ifScoreIsMoreThanOne() {
        String customerId = "id";
        BigDecimal requestedAmount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double score = 2.5;
        var request = new CreditRequest(customerId, requestedAmount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(creditScore);
        when(creditScore.isHasDebt()).thenReturn(false);
        when(creditScore.getValue()).thenReturn(score);
        mockAmount();

        var decision = decisionService.getCreditDecision(request);
        
        verify(creditRatingGateway).getCustomerCreditScore(request);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(amountMax);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveIncreasedAmount_ifScoreIsMoreThanOne() {
        String customerId = "id";
        BigDecimal requestedAmount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double score = 1.2;
        BigDecimal increased = BigDecimal.valueOf(6000);
        var request = new CreditRequest(customerId, requestedAmount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(creditScore);
        when(creditScore.isHasDebt()).thenReturn(false);
        when(creditScore.getValue()).thenReturn(score);
        mockAmount();

        var decision = decisionService.getCreditDecision(request);
        
        verify(creditRatingGateway).getCustomerCreditScore(request);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(increased);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveRequestedAmount_ifScoreIsEqualToOne() {
        String customerId = "id";
        BigDecimal requestedAmount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double score = 1.0;
        var request = new CreditRequest(customerId, requestedAmount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(creditScore);
        when(creditScore.isHasDebt()).thenReturn(false);
        when(creditScore.getValue()).thenReturn(score);

        var decision = decisionService.getCreditDecision(request);
        
        verify(creditRatingGateway).getCustomerCreditScore(request);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(requestedAmount);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveRoundedAmount_ifOfferedAmountIsNotFull() { // for exapmle 3493 should become 3500
        String customerId = "id";
        BigDecimal requestedAmount = BigDecimal.valueOf(5000);
        Integer period = 15;

        double score = 0.69874657464;
        BigDecimal increased = BigDecimal.valueOf(3500);
        var request = new CreditRequest(customerId, requestedAmount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(creditScore);
        when(creditScore.isHasDebt()).thenReturn(false);
        when(creditScore.getValue()).thenReturn(score);
        mockAmount();

        var decision = decisionService.getCreditDecision(request);

        verify(creditRatingGateway).getCustomerCreditScore(request);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(increased);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveDecreasedAmount_ifScoreIsSmallerThatOne() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(5000);
        Integer period = 12;

        double score = 0.5;
        BigDecimal desreasedAmount = BigDecimal.valueOf(2500);
        var request = new CreditRequest(customerId, amount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(creditScore);
        when(creditScore.isHasDebt()).thenReturn(false);
        when(creditScore.getValue()).thenReturn(score);
        mockAmount();

        var decision = decisionService.getCreditDecision(request);
        
        verify(creditRatingGateway).getCustomerCreditScore(request);
        assertThat(decision.getStatus()).isEqualTo(APROOVED);
        assertThat(decision.getAmountAprooved()).isEqualTo(desreasedAmount);
        assertThat(decision.getPeriodAprooved()).isEqualTo(period);
    }

    @Test
    public void getCreditDecision_shouldAprooveDecreasedAmountAndIncreasedPeriod_ifScoreIsSmallerThatOne() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(5000);
        Integer period = 12;

        double score = 0.35;
        BigDecimal desreasedAmount = BigDecimal.valueOf(2000);
        Integer increasedPeriod = 35;
        var request = new CreditRequest(customerId, amount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(creditScore);
        when(creditScore.isHasDebt()).thenReturn(false);
        when(creditScore.getValue()).thenReturn(score);
        when(properties.getPeriodMax()).thenReturn(periodMax);
        mockAmount();

        var decision = decisionService.getCreditDecision(request);
        
        verify(creditRatingGateway).getCustomerCreditScore(request);
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
        var request = new CreditRequest(customerId, amount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(creditScore);
        when(creditScore.isHasDebt()).thenReturn(false);
        when(creditScore.getValue()).thenReturn(badScore);
        mockAmount();

        var decision = decisionService.getCreditDecision(request);
        
        verify(creditRatingGateway).getCustomerCreditScore(request);
        assertThat(decision.getStatus()).isEqualTo(REJECTED);
        assertThat(decision.getAmountAprooved()).isEqualTo(BigDecimal.ZERO);
        assertThat(decision.getPeriodAprooved()).isEqualTo(0);
    }

    @Test
    public void getCreditDecision_shouldReject_ifHasDebt() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(2500);
        Integer period = 15;
        var request = new CreditRequest(customerId, amount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(creditScore);
        when(creditScore.isHasDebt()).thenReturn(true);

        var decision = decisionService.getCreditDecision(request);
        
        verify(creditRatingGateway).getCustomerCreditScore(request);
        assertThat(decision.getStatus()).isEqualTo(REJECTED);
        assertThat(decision.getAmountAprooved()).isEqualTo(BigDecimal.ZERO);
        assertThat(decision.getPeriodAprooved()).isEqualTo(0);
    }

    @Test
    public void getCreditDecision_shouldReturnStatusUndefined_ifNoCreditScoreAvailable() {
        String customerId = "id";
        BigDecimal amount = BigDecimal.valueOf(2500);
        Integer period = 15;
        var request = new CreditRequest(customerId, amount, period);

        when(creditRatingGateway.getCustomerCreditScore(request)).thenReturn(null);

        var decision = decisionService.getCreditDecision(request);
        
        verify(creditRatingGateway).getCustomerCreditScore(request);
        assertThat(decision.getStatus()).isEqualTo(UNDEFINED);
        assertThat(decision.getAmountAprooved()).isEqualTo(BigDecimal.ZERO);
        assertThat(decision.getPeriodAprooved()).isEqualTo(0);
    }

    private void mockAmount() {
        when(properties.getAmountMax()).thenReturn(amountMax);
        when(properties.getAmountMin()).thenReturn(amountMin);
    }
}

