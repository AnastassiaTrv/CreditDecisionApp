package org.anatrv.creditdecisionservice.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "constraints.credit")
public class CreditProperties {
    private BigDecimal amountMax;
    private BigDecimal amountMin;
    private Integer periodMax;
    private Integer periodMin;
}
