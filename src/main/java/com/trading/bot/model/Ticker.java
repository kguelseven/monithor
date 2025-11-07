package com.trading.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ticker {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("priceChange")
    private BigDecimal priceChange;

    @JsonProperty("priceChangePercent")
    private BigDecimal priceChangePercent;

    @JsonProperty("lastPrice")
    private BigDecimal lastPrice;

    @JsonProperty("bidPrice")
    private BigDecimal bidPrice;

    @JsonProperty("askPrice")
    private BigDecimal askPrice;

    @JsonProperty("volume")
    private BigDecimal volume;

    @JsonProperty("openPrice")
    private BigDecimal openPrice;

    @JsonProperty("highPrice")
    private BigDecimal highPrice;

    @JsonProperty("lowPrice")
    private BigDecimal lowPrice;
}
